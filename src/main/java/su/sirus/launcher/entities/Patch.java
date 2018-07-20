package su.sirus.launcher.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Patch
{
    Integer id;
    String filename;
    String path;
    Long size;
    String md5;
    String host;

    @JsonIgnore
    boolean isChecked = false;
    @JsonIgnore
    boolean isChecking = false;
    @JsonIgnore
    boolean isDownloading = false;
    @JsonIgnore
    Long downloadedSize = 0L;
    @JsonIgnore
    boolean isShouldDownload;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public Long getSize()
    {
        return size;
    }

    public void setSize(Long size)
    {
        this.size = size;
    }

    public String getMd5()
    {
        return md5;
    }

    public void setMd5(String md5)
    {
        this.md5 = md5;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public boolean isChecked()
    {
        return isChecked;
    }

    public void setChecked(boolean checked)
    {
        isChecked = checked;
    }

    public boolean isChecking()
    {
        return isChecking;
    }

    public void setChecking(boolean checking)
    {
        isChecking = checking;
    }

    public boolean isDownloading()
    {
        return isDownloading;
    }

    public void setDownloading(boolean downloading)
    {
        isDownloading = downloading;
    }

    public Long getDownloadedSize()
    {
        return downloadedSize;
    }

    public void setDownloadedSize(Long downloadedSize)
    {
        this.downloadedSize = downloadedSize;
    }

    public boolean isShouldDownload()
    {
        return isShouldDownload;
    }

    public void setShouldDownload(boolean shouldDownload)
    {
        isShouldDownload = shouldDownload;
    }
}
