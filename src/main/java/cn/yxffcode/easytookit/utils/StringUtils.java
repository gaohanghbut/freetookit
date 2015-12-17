package cn.yxffcode.easytookit.utils;

/**
 * @author gaohang on 15/9/12.
 */
public final class StringUtils {
  public static final String EMPTY = "";
  private static final int STRING_INDEX_OF_THRESHOLD = 10;

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

  public static boolean equalsIgnoreCase(String left, String right) {
    return (left == right) || (left != null && left.equalsIgnoreCase(right));
  }

  public static int indexOf(CharSequence source, CharSequence target) {
    return indexOf(source, 0, source.length(), target, 0, target.length());
  }

  /**
   * @see #indexOf(char[], int, int, char[], int, int)
   */
  public static int indexOf(CharSequence source, int soff, int slen,
                            CharSequence target, int toff, int tlen) {
    if (slen < STRING_INDEX_OF_THRESHOLD) {
      if (tlen == 0) {
        return 0;
      }

      char first = target.charAt(toff);
      int max = soff + (slen - tlen);

      for (int i = soff; i <= max; i++) {
        if (source.charAt(i) != first) {
          while (++i <= max && source.charAt(i) != first) ;
        }
        if (i <= max) {
          int j = i + 1;
          int end = j + tlen - 1;
          for (int k = toff + 1; j < end && source.charAt(j) == target.charAt(k); j++, k++) {
            //do nothing
          }

          if (j == end) {
            return i - soff;
          }
        }
      }
      return -1;
    }
    //find first equals character
    char first = target.charAt(toff);
    int max = soff + (slen - tlen);
    int idx = soff;
    if (source.charAt(idx) != first) {
      while (++idx <= max && source.charAt(idx) != first) ;
    }
    if (idx > max) {
      return -1;
    }
    //KMP next array
    int[] next = new int[tlen];
    next[0] = -1;
    int k = next[0];//可当作i==0的情况,k = next[i]
    for (int i = 0, j = target.length() - 1; i < j; ) {
      if (k == -1 || target.charAt(k + toff) == target.charAt(i + toff)) {
        //next[i+1]=next[i]+1 => next[i+1]=k+1
        next[++i] = ++k;
      } else {
        k = next[k];
      }
    }
    //from idx not 0
    int i = idx - soff;
    int j = 0;
    while (i < slen && j < tlen) {
      if (j == 0 && target.charAt(toff + j) != source.charAt(soff + i)) {
        while (++i <= max && source.charAt(i) != first) ;
      }
      if (i >= tlen) {
        return -1;
      }
      if (j == -1 || target.charAt(toff + j) == source.charAt(soff + i)) {
        ++i;
        ++j;
      } else {
        j = next[j];
      }
      if (tlen - j > slen - i) {
        return -1;
      }
    }
    return j == tlen ? i - j : -1;
  }

  public static int indexOf(char[] source, char[] target) {
    return indexOf(source, 0, source.length, target, 0, target.length);
  }

  /**
   * 模式匹配,{@link String#indexOf(char[], int, int, char[], int, int, int)}方法使用的是BF的方式,
   * 在模式串比较短时效果比KMP好,当模式串太长后,KMP的优势比较明显
   * <p/>
   * 给一个中间值{@link #STRING_INDEX_OF_THRESHOLD},当模式串长度小于中间值时使用BF,大于中间值时使用KMP
   *
   * @return -1表示没找到
   */
  public static int indexOf(char[] source, int soff, int slen,
                            char[] target, int toff, int tlen) {
    if (tlen < STRING_INDEX_OF_THRESHOLD) {
      if (tlen == 0) {
        return 0;
      }

      char first = target[toff];
      int max = soff + (slen - tlen);

      for (int i = soff; i <= max; i++) {
        if (source[i] != first) {
          while (++i <= max && source[i] != first) ;
        }
        if (i <= max) {
          int j = i + 1;
          int end = j + tlen - 1;
          for (int k = toff + 1; j < end && source[j] == target[k]; j++, k++)
            ;

          if (j == end) {
            return i - soff;
          }
        }
      }
      return -1;
    }
    //find first equals character, to prevent calculate next array if the first char is not in source
    char first = target[toff];
    int max = soff + (slen - tlen);
    int idx = soff;
    if (source[idx] != first) {
      while (++idx <= max && source[idx] != first) ;
    }
    if (idx > max) {
      return -1;
    }
    //KMP next array
    int[] next = new int[tlen];
    next[0] = -1;
    int k = next[0];//可当作i==0的情况,k = next[i]
    for (int i = 0, j = target.length - 1; i < j; ) {
      if (k == -1 || target[k + toff] == target[i + toff]) {
        //next[i+1]=next[i]+1 => next[i+1]=k+1
        next[++i] = ++k;
      } else {
        k = next[k];
      }
    }
    //from idx not 0
    int i = idx - soff;
    int j = 0;
    while (i < slen && j < tlen) {
      if (j == 0 && target[toff + j] != source[soff + i]) {
        while (++i <= max && source[i] != first) ;
      }
      if (i >= slen) {
        return -1;
      }
      if (j == -1 || target[toff + j] == source[soff + i]) {
        ++i;
        ++j;
      } else {
        j = next[j];
      }
      if (tlen - j > slen - i) {
        return -1;
      }
    }
    return j == tlen ? i - j : -1;
  }

  public boolean contains(CharSequence source, CharSequence target) {
    return indexOf(source, target) != -1;
  }

  public boolean contains(CharSequence source, int fromIndex, CharSequence target) {
    return indexOf(source, fromIndex, source.length() - fromIndex, target, 0, target.length()) != -1;
  }

  public boolean contains(char[] source, char[] target) {
    return indexOf(source, target) != -1;
  }

  public boolean contains(char[] source, int fromIndex, char[] target) {
    return indexOf(source, fromIndex, source.length - fromIndex, target, 0, target.length) != -1;
  }

}