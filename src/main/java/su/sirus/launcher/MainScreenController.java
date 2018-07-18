package su.sirus.launcher;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import su.sirus.launcher.responses.PatchListResponse;
import su.sirus.launcher.services.DownloadService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Future;

@Component
public class MainScreenController
{
    public Button startBtn;
    public Label statusLabel;

    public final DownloadService downloadService;

    private static final Logger log = LoggerFactory.getLogger(MainScreenController.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    public MainScreenController(DownloadService downloadService)
    {
        this.downloadService = downloadService;
    }

    public void actionStart(ActionEvent actionEvent)
    {
        statusLabel.setText("Prepare for download...");
    }

    public void startDownloading()
    {
        downloadService.downloadPatchList().thenAccept(r -> {
            // patchCheckerService.checkFiles()
            Platform.runLater(() -> {
                statusLabel.setText("Downloading patch list...");
                statusLabel.setText("Download completed, found: " + r.getPatches().size() + " files");
            });
        });
    }

    @Scheduled(fixedRate = 20000)
    public void reportCurrentTime()
    {
        this.startDownloading();
    }
}
