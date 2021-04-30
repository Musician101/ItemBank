package io.musician101.itembank.common;

import io.musician101.itembank.common.Reference.Config;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ItemBankConfig<I> {

    protected final List<I> blacklist = new ArrayList<>();
    protected final Map<String, String> databaseOptions = new HashMap<>();
    protected final Map<I, Integer> itemRestrictions = new HashMap<>();
    protected final List<I> whitelist = new ArrayList<>();
    protected boolean enableEconomy = false;
    protected boolean enableMultiWorldStorage = false;
    protected String format = Config.YAML;
    protected int pageLimit = 0;
    protected double transactionCost;

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

    public boolean isBlacklisted(I type) {
        return !blacklist.isEmpty() && blacklist.contains(type);
    }

    public boolean isMultiWorldStorageEnabled() {
        return enableMultiWorldStorage;
    }

    public boolean isWhitelisted(I type) {
        return whitelist.isEmpty() || whitelist.contains(type);
    }

    public abstract void reload() throws IOException;

    public boolean useEconomy() {
        return enableEconomy;
    }
}
