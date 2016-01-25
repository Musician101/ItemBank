package musician101.itembank.spigot.lib;

import musician101.itembank.spigot.SpigotItemBank;
import musician101.itembank.spigot.util.IBUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Messages
{
    /* Vault Messages */
    public static String VAULT_BOTH_ENABLED;
    private static final String VAULT_BOTH_ENABLED_DEFAULT = "Vault detected and enabled in config. Using Vault for monetary transactions.";

    public static String VAULT_NO_CONFIG;
    private static final String VAULT_NO_CONFIG_DEFAULT = "Vault detected but disabled in config. No monetary transactions will occur.";

    public static String VAULT_NOT_INSTALLED;
    private static final String VAULT_NOT_INSTALLED_DEFAULT = "Error detecting Vault. Is it installed?";
}
