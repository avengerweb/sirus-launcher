package su.sirus.launcher.events;

import su.sirus.launcher.AppState;

public class ChangeApplicationState
{
    private AppState appState;

    public ChangeApplicationState(AppState appState)
    {
        this.appState = appState;
    }

    public AppState getAppState()
    {
        return appState;
    }

    public void setAppState(AppState appState)
    {
        this.appState = appState;
    }
}
