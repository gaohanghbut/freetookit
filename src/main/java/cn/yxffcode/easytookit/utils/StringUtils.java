package cn.yxffcode.easytookit.utils;

/**
 * @author gaohang on 15/9/12.
 */
public final class StringUtils {
    private StringUtils() {
    }

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

}