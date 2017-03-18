package de.acepe.onkyoremote.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                                                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                                                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                                                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public static String makePartialIPRegex() {
        String partialBlock = "(([01]?[0-9]{0,2})|(2[0-4][0-9])|(25[0-5]))";
        String subsequentPartialBlock = "(\\." + partialBlock + ")";
        String ipAddress = partialBlock + "?" + subsequentPartialBlock + "{0,3}";
        return "^" + ipAddress;
    }

    public static boolean validateIP(String ip) {
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(IPADDRESS_PATTERN);
        matcher = pattern.matcher(ip);
        return matcher.matches();
    }
}
