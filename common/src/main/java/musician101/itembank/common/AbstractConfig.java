package musician101.itembank.common;

import java.io.File;
import java.util.UUID;

public abstract class AbstractConfig
{
    private boolean checkForUpdate = true;
    private boolean isMultiWorldStorageEnabled = false;
    private boolean isWhitelist = false;
    private boolean useMYSQL = false;
    private int pageLimit = 0;
    private String format = "json";

    protected AbstractConfig()
    {
    }

    public boolean checkForUpdate()
    {
        return checkForUpdate;
    }

    public void setCheckForUpdate(boolean checkForUpdate)
    {
        this.checkForUpdate = checkForUpdate;
    }

    public boolean isMultiWorldStorageEnabled()
    {
        return isMultiWorldStorageEnabled;
    }

    public void setMultiWorldStorageEnabled(boolean isMultiWorldStorageEnabled)
    {
        this.isMultiWorldStorageEnabled = isMultiWorldStorageEnabled;
    }

    public boolean isWhitelist()
    {
        return isWhitelist;
    }

    public void setIsWhitelist(boolean isWhitelist)
    {
        this.isWhitelist = isWhitelist;
    }

    public boolean useMYSQL()
    {
        return useMYSQL;
    }

    public void setUseMySQL(boolean useMYSQL)
    {
        this.useMYSQL = useMYSQL;
    }

    public int getPageLimit()
    {
        return pageLimit;
    }

    public void setPageLimit(int pageLimit)
    {
        this.pageLimit = pageLimit;
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format)
    {
        this.format = format;
    }

    public abstract File getPlayerFile(UUID uuid);
}
