package musician101.itembank.spigot.lib;

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
