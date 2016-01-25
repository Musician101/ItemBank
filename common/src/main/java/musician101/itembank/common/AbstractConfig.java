package musician101.itembank.common;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractConfig<ItemStack>
{
    protected boolean enableEconomy = false;
    protected boolean checkForUpdate = true;
    protected boolean isMultiWorldStorageEnabled = false;
    protected boolean isWhitelist = false;
    protected boolean useMySQL = false;
    protected double transactionCost;
    protected int pageLimit = 0;
    protected List<ItemStack> itemList = new ArrayList<>();

    protected AbstractConfig() {}

    public boolean checkForUpdate()
    {
        return checkForUpdate;
    }

    public boolean useEconomy()
    {
        return enableEconomy;
    }

    public boolean isMultiWorldStorageEnabled()
    {
        return isMultiWorldStorageEnabled;
    }

    public boolean isWhitelist()
    {
        return isWhitelist;
    }

    public boolean useMySQL()
    {
        return useMySQL;
    }

    public double getTransactionCost()
    {
        return transactionCost;
    }

    public int getPageLimit()
    {
        return pageLimit;
    }

    public abstract ItemStack getItem(ItemStack itemStack);

    protected abstract void reload();
}
