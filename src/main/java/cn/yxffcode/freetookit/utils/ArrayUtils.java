package cn.yxffcode.freetookit.utils;

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

  public static int hashCode(byte[] data, int off, int len) {
    if (data == null) {
      return 0;
    }

    int result = 1;
    for (int i = off, j = off + len; i < j; i++) {
      result = 31 * result + data[i];
    }
    return result;
  }

  public static String toString(byte[] data, int off, int len) {
    if (data == null) {
      return "null";
    }
    if (len == 0) {
      return "[]";
    }

    StringBuilder b = new StringBuilder();
    b.append('[');
    for (int i = off, j = len + off - 1; ; i++) {
      b.append(data[i]);
      if (i == j) {
        return b.append(']').toString();
      }
      b.append(", ");
    }
  }
}
