package misaka.nemesiss.com.findlostthings.Model;

public class VersionInfo
{
    private String VersionNum;
    private String Download;
    private String Description;

    public String getDescription()
    {
        return Description;
    }

    public String getDownload()
    {
        return Download;
    }

    public String getVersionNum()
    {
        return VersionNum;
    }

    public void setDescription(String description)
    {
        Description = description;
    }

    public void setDownload(String download)
    {
        Download = download;
    }

    public void setVersionNum(String versionNum)
    {
        VersionNum = versionNum;
    }
}
