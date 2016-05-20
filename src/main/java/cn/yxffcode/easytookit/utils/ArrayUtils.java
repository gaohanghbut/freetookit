package cn.yxffcode.easytookit.utils;

import java.lang.reflect.Array;

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

  public static char[] reverseGrow(char[] src, int newLength) {
    char[] tmp = new char[newLength];
    System.arraycopy(src, 0, tmp, src.length, newLength - src.length);
    return tmp;
  }

  public static <T> T[] grow(T[] src, Class<T> type, int newLength) {
    T[] tmp = (T[]) Array.newInstance(type, newLength);
    System.arraycopy(src, 0, tmp, 0, src.length);
    return tmp;
  }
}
