package su.sirus.launcher.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import su.sirus.launcher.responses.PatchListResponse;

import java.util.concurrent.CompletableFuture;

@Service
public class DownloadService
{
    private final RestTemplate restTemplate;
    private PatchListResponse downloaderConfiguration;

    public DownloadService()
    {
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

        return CompletableFuture.completedFuture(downloaderConfiguration);
    }
}
