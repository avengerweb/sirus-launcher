package su.sirus.launcher.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import su.sirus.launcher.MainScreenController;
import su.sirus.launcher.entities.UserSettings;
import su.sirus.launcher.events.UserSettingsUpdated;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

@Service
public class UserSettingsService
{
    private final ApplicationEventPublisher publisher;
    private final String userHomeDirectory;
    private final ObjectMapper mapper;

    private String directoryName = "sirus-launcher";
    private String configName = "user-settings.json";

    private UserSettings userSettings;

    private static final Logger log = LoggerFactory.getLogger(MainScreenController.class);

    @Autowired
    public UserSettingsService(ApplicationEventPublisher publisher)
    {
        this.publisher = publisher;
        this.userHomeDirectory = System.getProperty("user.home");
        mapper = new ObjectMapper();
    }

    public void loadSetting()
    {
        File file = new File(this.getUserSettingsPath());

        if (!file.exists()) {
            try
            {
                // Create directory before;
                new File(userHomeDirectory + File.separator + directoryName).mkdirs();

                file.createNewFile();

                mapper.writeValue(file, new UserSettings());
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            this.userSettings = mapper.readValue(file, UserSettings.class);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        this.refresh();
    }

    public UserSettings getUserSettings()
    {
        return userSettings;
    }

    public void setUserSettings(UserSettings userSettings)
    {
        this.userSettings = userSettings;
    }

    @Async
    public void saveSettings()
    {
        try
        {
            mapper.writeValue(new File(getUserSettingsPath()), this.userSettings);
        } catch (IOException e)
        {
            log.error("Could not write settings file", e.getStackTrace());
        }
    }

    public String getUserSettingsPath()
    {
        return userHomeDirectory + File.separator + directoryName + File.separator + configName;
    }

    public void refresh()
    {
        this.publisher.publishEvent(new UserSettingsUpdated(this.userSettings));
    }
}
