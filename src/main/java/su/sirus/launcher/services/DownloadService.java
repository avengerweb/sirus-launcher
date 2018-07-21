package su.sirus.launcher.services;

import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import su.sirus.launcher.AppState;
import su.sirus.launcher.entities.Patch;
import su.sirus.launcher.events.ChangeApplicationState;
import su.sirus.launcher.events.FileCheckProgress;
import su.sirus.launcher.events.PatchListDownloaded;
import su.sirus.launcher.responses.PatchListResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;

@Service
public class DownloadService
{
    private final RestTemplate restTemplate;
    private PatchListResponse downloaderConfiguration;
    private final ApplicationEventPublisher publisher;
    private UserSettingsService userSettingsService;

    @Autowired
    public DownloadService(ApplicationEventPublisher publisher, UserSettingsService userSettingsService)
    {
        this.publisher = publisher;
        this.userSettingsService = userSettingsService;
        restTemplate = new RestTemplate();
    }

    @Async
    public CompletableFuture<PatchListResponse> downloadPatchList()
    {
        if (downloaderConfiguration == null) {
            downloaderConfiguration = restTemplate.getForObject(
                    "https://api.sirus.su/api/client/patches",
                    PatchListResponse.class);
        }

        this.publisher.publishEvent(new PatchListDownloaded(downloaderConfiguration));

        return CompletableFuture.completedFuture(downloaderConfiguration);
    }

    public PatchListResponse getDownloaderConfiguration()
    {
        return downloaderConfiguration;
    }

    public void setDownloaderConfiguration(PatchListResponse downloaderConfiguration)
    {
        this.downloaderConfiguration = downloaderConfiguration;
    }

    public class ProgressListener implements MediaHttpDownloaderProgressListener
    {
        private Patch patch;

        public ProgressListener(Patch patch) {

            this.patch = patch;
        }

        public void progressChanged(MediaHttpDownloader downloader)
        {
            switch (downloader.getDownloadState())
            {
                case MEDIA_IN_PROGRESS:
                    this.patch.setDownloadedSize(downloader.getNumBytesDownloaded());
                    break;
                case MEDIA_COMPLETE:
                    this.patch.setDownloading(false);
                    this.patch.setShouldDownload(false);

                    if (downloaderConfiguration.getPatches().stream().noneMatch(Patch::isShouldDownload)) {
                        publisher.publishEvent(new ChangeApplicationState(AppState.FORCE_CHECK_FILES));
                    }
            }
        }
    }

    @Async
    public void downloadFile(Patch patch)
    {
        String path = userSettingsService.getUserSettings().getGamePath();

        try {
            File file = new File(path + patch.getPath() + patch.getFilename());

            if (file.exists() && patch.isChecked()) {
                file.delete();
            }

            OutputStream out = new FileOutputStream(file.getPath(), true);
            HttpTransport httpTransport = new ApacheHttpTransport();

            MediaHttpDownloader downloader = new MediaHttpDownloader(httpTransport, null);
            downloader.setChunkSize(256 * 1024);
            downloader.setProgressListener(new ProgressListener(patch));
            downloader.download(new GenericUrl(patch.getHost() + patch.getFilename()), out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 500)
    public void progressUpdate()
    {
        if (this.downloaderConfiguration == null) {
            return;
        }

        if (this.downloaderConfiguration.getPatches().stream().noneMatch(Patch::isShouldDownload)) {
            return;
        }

        Long size = this.downloaderConfiguration.getPatches().stream().mapToLong(Patch::getSize).sum();
        Long downloadedSize = this.downloaderConfiguration.getPatches().stream().mapToLong(Patch::getDownloadedSize).sum();

        this.publisher.publishEvent(new FileCheckProgress(downloadedSize, size));
    }
}
