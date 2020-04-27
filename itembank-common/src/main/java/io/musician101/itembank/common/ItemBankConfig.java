package io.musician101.itembank.common;

import io.musician101.itembank.common.Reference.Config;
import io.musician101.musicianlibrary.java.minecraft.common.config.AbstractConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ItemBankConfig<I> extends AbstractConfig {

    protected final Map<String, String> databaseOptions = new HashMap<>();
    protected final List<I> blackList = new ArrayList<>();
    protected final Map<I, Integer> itemRestrictions = new HashMap<>();
    protected final List<I> whiteList = new ArrayList<>();
    protected boolean enableEconomy = false;
    protected boolean enableMultiWorldStorage = false;
    protected String format = Config.YAML;
    protected int pageLimit = 0;
    protected double transactionCost;

    protected ItemBankConfig(File file) {
        super(file);
    }

    public List<I> getBlackList() {
        return blackList;
    }

    public boolean isBlacklisted(I type) {
        return !blackList.isEmpty() && blackList.contains(type);
    }

    public boolean isWhitelisted(I type) {
        return whiteList.isEmpty() || whiteList.contains(type);
    }

    public Map<String, String> getDatabaseOptions() {
        return databaseOptions;
    }

    public String getFormat() {
        return format;
    }

    public int getMaxAmount(I type) {
        return itemRestrictions.getOrDefault(type, 0);
    }

    public int getPageLimit() {
        return pageLimit;
    }

    public double getTransactionCost() {
        return transactionCost;
    }

    public List<I> getWhiteList() {
        return whiteList;
    }

    public boolean isMultiWorldStorageEnabled() {
        return enableMultiWorldStorage;
    }

    public boolean useEconomy() {
        return enableEconomy;
    }
}
