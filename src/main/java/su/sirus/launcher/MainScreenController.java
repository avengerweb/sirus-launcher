package su.sirus.launcher;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import su.sirus.launcher.events.PatchListDownloaded;
import su.sirus.launcher.events.UserSettingsUpdated;
import su.sirus.launcher.services.DownloadService;
import su.sirus.launcher.services.UserSettingsService;

import java.io.File;
import java.text.SimpleDateFormat;

@Component
public class MainScreenController
{
    public Button startBtn;
    public Label statusLabel;
    public Button changeGameDir;

    public Label settingsGamePath;

    public final DownloadService downloadService;
    public final UserSettingsService userSettingsService;

    private static final Logger log = LoggerFactory.getLogger(MainScreenController.class);

    @Autowired
    public MainScreenController(DownloadService downloadService, UserSettingsService userSettingsService)
    {
        this.downloadService = downloadService;
        this.userSettingsService = userSettingsService;

        // Load user settings
        this.userSettingsService.loadSetting();
    }

    public void startDownloading()
    {
        downloadService.downloadPatchList();
    }

    /**
     * Reload configuration every 3 minutes
     */
    @Scheduled(fixedRate = 3 * 60 * 1000)
    public void reportCurrentTime()
    {
        this.startDownloading();
    }

    /* Actions */
    public void actionStart(ActionEvent actionEvent)
    {
    }

    /* Settings actions */
    /**
     * Init select directory dialog
     *
     * @param actionEvent
     */
    public void actionChangeDirectory(ActionEvent actionEvent)
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите дирректорию с игрой");
        File selectedFile = directoryChooser.showDialog(((Node)actionEvent.getTarget()).getScene().getWindow());
        this.userSettingsService.getUserSettings().setGamePath(selectedFile.getPath());
        this.userSettingsService.refresh();
        this.userSettingsService.saveSettings();
    }

    public void renderSettingsValues()
    {
        this.settingsGamePath.setText(userSettingsService.getUserSettings().getGamePath() == null ?
                "Не выбрана" : userSettingsService.getUserSettings().getGamePath());
    }

    /* Listeners */
    @EventListener
    public void handlePatchListDownloadedEvent(PatchListDownloaded patchListDownloaded)
    {
        Platform.runLater(() -> {
            statusLabel.setText("Download completed, found: " + patchListDownloaded.getPatchListResponse().getPatches().size() + " files");
        });

        this.userSettingsService.getUserSettings().setLatestPatchList(patchListDownloaded.getPatchListResponse());
        this.userSettingsService.saveSettings();
    }

    @EventListener
    public void handleUserSettingsUpdatedEvent(UserSettingsUpdated userSettingsUpdated)
    {
        Platform.runLater(this::renderSettingsValues);
    }

    @FXML
    void initialize(){
        this.renderSettingsValues();
    }
}
