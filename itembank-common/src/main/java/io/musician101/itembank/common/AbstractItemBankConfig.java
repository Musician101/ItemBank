package io.musician101.itembank.common;

import io.musician101.musicianlibrary.java.MySQLHandler;
import io.musician101.musicianlibrary.java.minecraft.config.AbstractConfig;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public abstract class AbstractItemBankConfig<I> extends AbstractConfig {

    protected final Map<I, Integer> typeList = new HashMap<>();
    protected boolean enableEconomy = false;
    protected boolean isMultiWorldStorageEnabled = false;
    protected boolean isWhitelist = false;
    @Nullable
    protected MySQLHandler mysql;
    protected int pageLimit = 0;
    protected double transactionCost;
    protected boolean useMySQL = false;

    protected AbstractItemBankConfig(File file) {
        super(file);
    }

    public int getMaxAmount(I type) {
        return typeList.getOrDefault(type, 0);
    }

    @Nullable
    public MySQLHandler getMySQL() {
        return mysql;
    }

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
