package su.sirus.launcher.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import su.sirus.launcher.responses.PatchListResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSettings
{
    String gamePath;
    private PatchListResponse latestPatchList;

    public String getGamePath()
    {
        return gamePath;
    }

    public void setGamePath(String gamePath)
    {
        this.gamePath = gamePath;
    }

    public void setLatestPatchList(PatchListResponse patchListResponse)
    {
        this.latestPatchList = patchListResponse;
    }

    public PatchListResponse getLatestPatchList()
    {
        return latestPatchList;
    }
}
