package cn.yxffcode.easytookit.algorithm;

/**
 * 单字符串模式匹配,{@link String#indexOf(char[], int, int, char[], int, int, int)}方法使用的是BF的方式,
 * 在模式串比较短时效果比KMP好,当模式串太长后,KMP的优势比较明显
 * <p/>
 * 给一个中间值{@link #STRING_INDEX_OF_THRESHOLD},当模式串长度小于中间值时使用BF,大于中间值时使用KMP.
 * 针对一同一个模式串,对象可以被复用
 *
 * @author gaohang on 15/12/18.
 */
public class CharsMatcher {

  private static final int STRING_INDEX_OF_THRESHOLD = 10;

  public static CharsMatcher create(char[] target, int toff, int tlen) {
    return new CharsMatcher(target, toff, tlen);
  }

  private int[] next;
  private final char[] target;
  private final int toff;
  private final int tlen;

  private CharsMatcher(char[] target, int toff, int tlen) {
    this.target = target;
    this.toff = toff;
    this.tlen = tlen;
  }

  public CharsMatcher buildNextIfAbsent() {
    if (next == null) {
      synchronized (next) {
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
  public int indexOf(char[] source, int soff, int slen) {
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
    if (next == null) {
      next = compileNext(target, toff, tlen);
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

  private int[] compileNext(char[] target, int toff, int tlen) {
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
    return next;
  }

}
