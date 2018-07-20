package su.sirus.launcher.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import su.sirus.launcher.entities.Delete;
import su.sirus.launcher.entities.Patch;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PatchListResponse
{
    ArrayList<Patch> patches;
    ArrayList<Delete> delete;

    public ArrayList<Patch> getPatches()
    {
        return patches;
    }

    public void setPatches(ArrayList<Patch> patches)
    {
        this.patches = patches;
    }

    public ArrayList<Delete> getDelete()
    {
        return delete;
    }

    public void setDelete(ArrayList<Delete> deletes)
    {
        this.delete = deletes;
    }
}
