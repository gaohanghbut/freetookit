package cn.yxffcode.easytookit.algorithm;

/**
 * @author gaohang on 15/12/18.
 * @see CharsMatcher
 */
public class CharSequenceMatcher {

  private static final int STRING_INDEX_OF_THRESHOLD = 10;
  private final CharSequence target;
  private final int toff;
  private final int tlen;
  private int[] next;

  private CharSequenceMatcher(CharSequence target, int toff, int tlen) {
    this.target = target;
    this.toff = toff;
    this.tlen = tlen;
  }

  public static CharSequenceMatcher create(CharSequence target, int toff, int tlen) {
    return new CharSequenceMatcher(target, toff, tlen);
  }

  public CharSequenceMatcher buildNextIfAbsent() {
    if (next == null) {
      synchronized (this) {
        if (next == null) {
          next = compileNext(target, toff, tlen);
        }
      }
    }
    return this;
  }

  /**
   * 调用此方法前,如果没有调用{@link #buildNextIfAbsent()},会以创建next数组,但next数组可能会被创建多次
   */
  public int indexOf(CharSequence source, int soff, int slen) {
    if (slen < STRING_INDEX_OF_THRESHOLD) {
      if (tlen == 0) {
        return 0;
      }

      char first = target.charAt(toff);
      int max = soff + (slen - tlen);

      for (int i = soff; i <= max; i++) {
        if (source.charAt(i) != first) {
          while (++i <= max && source.charAt(i) != first)
            ;
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
      while (++idx <= max && source.charAt(idx) != first)
        ;
    }
    if (idx > max) {
      return -1;
    }
    if (next == null) {
      next = compileNext(target, toff, tlen);
    }
    //from idx not 0
    int i = idx - soff;
    int j = 0;
    while (i < slen && j < tlen) {
      if (j == 0 && target.charAt(toff + j) != source.charAt(soff + i)) {
        while (++i <= max && source.charAt(i) != first)
          ;
        if (i >= max) {
          return -1;
        }
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

  private int[] compileNext(CharSequence target, int toff, int tlen) {
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
    return next;
  }

}
