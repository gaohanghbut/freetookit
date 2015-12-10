package cn.yxffcode.easytookit.utils;

/**
 * @author gaohang on 15/12/10.
 */
public abstract class ArrayUtils {
    private ArrayUtils() {
    }

    public static int[] grow(int[] src, int newLength) {
        int[] tmp = new int[newLength];
        System.arraycopy(src, 0, tmp, 0, src.length);
        return tmp;
    }
}