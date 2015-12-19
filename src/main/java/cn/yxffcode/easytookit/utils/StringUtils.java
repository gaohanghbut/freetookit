package cn.yxffcode.easytookit.utils;

import cn.yxffcode.easytookit.algorithm.CharsMatcher;
import cn.yxffcode.easytookit.algorithm.CharSequenceMatcher;
import cn.yxffcode.easytookit.dic.DoubleArrayTrie;
import cn.yxffcode.easytookit.algorithm.DictionaryTokenFilter;

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
    return CharSequenceMatcher.create(target, toff, tlen).indexOf(source, soff, slen);
  }

  public static int indexOf(char[] source, char[] target) {
    return indexOf(source, 0, source.length, target, 0, target.length);
  }

  /**
   * {@link CharsMatcher}的代理方法,如果单个模式串需要匹配多次,直接使用{@link CharsMatcher}避免多次生成next数组
   *
   * @return -1表示没找到
   */
  public static int indexOf(char[] source, int soff, int slen,
                            char[] target, int toff, int tlen) {
    return CharsMatcher.create(target, toff, tlen).indexOf(source, soff, slen);
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

  public boolean contains(String source, Iterable<String> targets) {
    return new DictionaryTokenFilter(DoubleArrayTrie.create(targets)).match(source);
  }

}