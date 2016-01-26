package musician101.itembank.spigot.util;

public class IBUtils
{
    //TODO move to common library and rewrite to work like the one in Sponge module
    @Deprecated
    public static boolean isNumber(String s)
    {
        if (s == null)
            return false;

        int length = s.length();
        if (length == 0)
            return false;

        int i = 0;
        if (s.charAt(0) == '-')
        {
            if (length == 1)
                return false;

            i = 1;
        }

        for (; i < length; i++)
        {
            char c = s.charAt(i);
            if (c < '0' || c > '9')
                return false;
        }

        return true;
    }
}
