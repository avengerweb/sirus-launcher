package su.sirus.launcher;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import su.sirus.launcher.entities.Patch;
import su.sirus.launcher.events.ChangeApplicationState;
import su.sirus.launcher.events.FileCheckProgress;
import su.sirus.launcher.events.PatchListDownloaded;
import su.sirus.launcher.events.UserSettingsUpdated;
import su.sirus.launcher.services.DownloadService;
import su.sirus.launcher.services.FileCheckService;
import su.sirus.launcher.services.UserSettingsService;

import java.io.File;
import java.io.IOException;

@Component
public class MainScreenController
{
    private AppState appState = AppState.SETTINGS_NOT_SET;

    public Button startBtn;
    public Label statusLabel;
    public Label launcherStatus;
    public ProgressBar progressBar;

    public Button changeGameDir;
    public Label settingsGamePath;

    public final DownloadService downloadService;
    public final UserSettingsService userSettingsService;

    private static final Logger log = LoggerFactory.getLogger(MainScreenController.class);
    private final FileCheckService fileCheckService;

    @Autowired
    public MainScreenController(DownloadService downloadService, UserSettingsService userSettingsService, FileCheckService fileCheckService)
    {
        this.downloadService = downloadService;
        this.userSettingsService = userSettingsService;
        this.fileCheckService = fileCheckService;
    }

    public void startDownloading()
    {
        if (this.getAppState() == AppState.DOWNLOADING_FILES) {
            return;
        }

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

    /**
     * Run force check
     * @param actionEvent
     */
    public void forceCheckClient(ActionEvent actionEvent)
    {
//        publisher.publishEvent(new ChangeApplicationState(AppState.FORCE_CHECK_FILES));
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

        if (selectedFile != null) {
            return;
        }

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
            statusLabel.setText("Download patch list, found: " + patchListDownloaded.getPatchListResponse().getPatches().size() + " files");
        });

        try
        {
            this.fileCheckService.checkFiles();
        } catch (IOException e)
        {
            log.error("Problems on checking process...");
        }

        this.userSettingsService.getUserSettings().setLatestPatchList(patchListDownloaded.getPatchListResponse());
        this.userSettingsService.saveSettings();
    }

    @EventListener
    public void handleUserSettingsUpdatedEvent(UserSettingsUpdated userSettingsUpdated)
    {
        Platform.runLater(this::renderSettingsValues);
        if (getAppState() == AppState.SETTINGS_NOT_SET) {
            setAppState(AppState.READY_TO_PLAY);
        }
    }

    @EventListener
    public void handleFileCheckProgress(FileCheckProgress progress)
    {
        Platform.runLater(() -> {
            progressBar.setProgress((double)progress.getChecked() / progress.getSizeTocheck());
            statusLabel.setText("Downloading / Checking  " + progress.getChecked() / 1024 / 1024 + " / " + progress.getSizeTocheck() / 1024 / 1024);
        });
    }

    @EventListener
    public void handleChangeApplicationState(ChangeApplicationState applicationState)
    {
        setAppState(applicationState.getAppState());

        if (applicationState.getAppState() == AppState.DOWNLOADING_FILES)
        {
            downloadService.getDownloaderConfiguration().getPatches().stream().filter(Patch::isShouldDownload).forEach(downloadService::downloadFile);
        }

        if (applicationState.getAppState() == AppState.FORCE_CHECK_FILES)
        {
            setAppState(AppState.CHECKING_FILES);
            downloadService.getDownloaderConfiguration().getPatches().stream().filter(Patch::isShouldDownload).forEach(downloadService::downloadFile);
        }
    }

    @FXML
    void initialize()
    {
        // Load user settings
        this.userSettingsService.loadSetting();
    }

    public AppState getAppState() {
        return appState;
    }

    public void setAppState(AppState appState) {
        Platform.runLater(() -> launcherStatus.setText(appState.toString()));
        this.appState = appState;
    }
}
