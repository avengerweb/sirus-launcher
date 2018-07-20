package su.sirus.launcher.events;

import su.sirus.launcher.entities.UserSettings;
import su.sirus.launcher.responses.PatchListResponse;

public class UserSettingsUpdated
{
    UserSettings userSettings;

    public UserSettingsUpdated(UserSettings userSettings)
    {
        this.userSettings = userSettings;
    }

    public UserSettings getUserSettings()
    {
        return userSettings;
    }

    public void setUserSettings(UserSettings userSettings)
    {
        this.userSettings = userSettings;
    }
}
