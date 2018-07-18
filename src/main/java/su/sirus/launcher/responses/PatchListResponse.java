package su.sirus.launcher.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import su.sirus.launcher.entities.Delete;
import su.sirus.launcher.entities.Patch;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PatchListResponse
{
    ArrayList<Patch> patches;
    ArrayList<Delete> deletes;

    public ArrayList<Patch> getPatches()
    {
        return patches;
    }

    public void setPatches(ArrayList<Patch> patches)
    {
        this.patches = patches;
    }

    public ArrayList<Delete> getDeletes()
    {
        return deletes;
    }

    public void setDeletes(ArrayList<Delete> deletes)
    {
        this.deletes = deletes;
    }
}
