package su.sirus.launcher.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import su.sirus.launcher.events.PatchListDownloaded;
import su.sirus.launcher.responses.PatchListResponse;

import java.util.concurrent.CompletableFuture;

@Service
public class DownloadService
{
    private final RestTemplate restTemplate;
    private PatchListResponse downloaderConfiguration;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public DownloadService(ApplicationEventPublisher publisher)
    {
        this.publisher = publisher;
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
}
