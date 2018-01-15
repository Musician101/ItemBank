package io.musician101.itembank.common;

import io.musician101.musicianlibrary.java.minecraft.config.AbstractConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractItemBankConfig<I> extends AbstractConfig {

    protected final List<I> itemList = new ArrayList<>();
    protected boolean enableEconomy = false;
    protected boolean isMultiWorldStorageEnabled = false;
    protected boolean isWhitelist = false;
    protected int pageLimit = 0;
    protected double transactionCost;
    protected boolean useMySQL = false;

    protected AbstractItemBankConfig(File file) {
        super(file);
    }

    public abstract I getItem(I itemStack);

    public int getPageLimit() {
        return pageLimit;
    }

    public double getTransactionCost() {
        return transactionCost;
    }

    public boolean isMultiWorldStorageEnabled() {
        return isMultiWorldStorageEnabled;
    }

    public boolean isWhitelist() {
        return isWhitelist;
    }

    public boolean useEconomy() {
        return enableEconomy;
    }

    public boolean useMySQL() {
        return useMySQL;
    }
}
