package su.sirus.launcher.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import su.sirus.launcher.AppState;
import su.sirus.launcher.entities.Patch;
import su.sirus.launcher.events.ChangeApplicationState;
import su.sirus.launcher.events.FileCheckProgress;
import su.sirus.launcher.responses.PatchListResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class FileCheckService
{
    private final DownloadService downloadService;
    private final UserSettingsService userSettingsService;
    private PatchListResponse downloaderConfiguration;
    private final ApplicationEventPublisher publisher;
    private static final Logger log = LoggerFactory.getLogger(FileCheckService.class);

    @Autowired
    public FileCheckService(ApplicationEventPublisher publisher,
                            DownloadService downloadService,
                            UserSettingsService userSettingsService
    )
    {
        this.publisher = publisher;
        this.downloadService = downloadService;
        this.userSettingsService = userSettingsService;
    }

    /**
     * Check md5 hash of files, size and etc
     */
    @Async
    public void checkFiles() throws IOException
    {
        log.debug("Start file checking");
        String gamePath = this.userSettingsService.getUserSettings().getGamePath();

        if (downloadService.getDownloaderConfiguration() != null && gamePath != null)
        {
            this.publisher.publishEvent(new ChangeApplicationState(AppState.CHECKING_FILES));

            Long sizeToCheck = downloadService.getDownloaderConfiguration().getPatches().stream().mapToLong(Patch::getSize).sum();
            Long sizeChecked = 0L;

            this.publisher.publishEvent(new FileCheckProgress(sizeChecked, sizeToCheck));

            for (Patch patch : downloadService.getDownloaderConfiguration().getPatches())
            {
                File file = new File(gamePath + patch.getPath() + patch.getFilename());

                if (!file.exists()) {
                    patch.setShouldDownload(true);
                } else
                {
                    String hash = DigestUtils.md5DigestAsHex(new FileInputStream(file.getPath()));
                    log.debug(patch.getFilename() + ", " + patch.getMd5() + " = " + hash.toUpperCase());

                    if (!hash.toUpperCase().equals(patch.getMd5())) {
                        patch.setShouldDownload(true);
                    }
                }

                sizeChecked += patch.getSize();
                patch.setChecked(true);

                this.publisher.publishEvent(new FileCheckProgress(sizeChecked, sizeToCheck));
            }

            if (downloadService.getDownloaderConfiguration().getPatches().stream().anyMatch(Patch::isShouldDownload))
            {
                this.publisher.publishEvent(new ChangeApplicationState(AppState.DOWNLOADING_FILES));
            } else {
                this.publisher.publishEvent(new ChangeApplicationState(AppState.READY_TO_PLAY));
            }
        }
    }
}

