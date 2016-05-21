package io.musician101.itembank.common;

import io.musician101.common.java.minecraft.AbstractConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractItemBankConfig<I> extends AbstractConfig
{
    protected boolean enableEconomy = false;
    protected boolean isMultiWorldStorageEnabled = false;
    protected boolean isWhitelist = false;
    protected boolean useMySQL = false;
    protected double transactionCost;
    protected int pageLimit = 0;
    protected final List<I> itemList = new ArrayList<>();

    protected AbstractItemBankConfig(File file)
    {
        super(file);
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

    public abstract I getItem(I itemStack);
}
