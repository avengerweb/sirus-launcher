package su.sirus.launcher.events;

import su.sirus.launcher.AppState;

public class FileCheckProgress
{
    private Long checked;
    private Long sizeTocheck;

    public FileCheckProgress(Long checked, Long sizeTocheck)
    {
        this.checked = checked;
        this.sizeTocheck = sizeTocheck;
    }

    public Long getChecked()
    {
        return checked;
    }

    public void setChecked(Long checked)
    {
        this.checked = checked;
    }

    public Long getSizeTocheck()
    {
        return sizeTocheck;
    }

    public void setSizeTocheck(Long sizeTocheck)
    {
        this.sizeTocheck = sizeTocheck;
    }
}
