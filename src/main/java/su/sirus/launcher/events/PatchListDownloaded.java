package su.sirus.launcher.events;

import su.sirus.launcher.responses.PatchListResponse;

public class PatchListDownloaded
{
    PatchListResponse patchListResponse;

    public PatchListDownloaded(PatchListResponse patchListResponse)
    {
        this.patchListResponse = patchListResponse;
    }

    public PatchListResponse getPatchListResponse()
    {
        return patchListResponse;
    }

    public void setPatchListResponse(PatchListResponse patchListResponse)
    {
        this.patchListResponse = patchListResponse;
    }
}
