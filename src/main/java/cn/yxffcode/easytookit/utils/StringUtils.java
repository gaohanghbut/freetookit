package cn.yxffcode.easytookit.utils;

/**
 * @author gaohang on 15/9/12.
 */
public final class StringUtils {
    private StringUtils() {
    }

    public static final String EMPTY = "";

    public static boolean isBlank(String value) {
        int strLen;
        if (value == null || (strLen = value.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(value.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalsIgnoreCase(String left, String right) {
        return (left == right) || (left != null && left.equalsIgnoreCase(right));
    }
}