package cn.yxffcode.freetookit.lang;

import cn.yxffcode.freetookit.utils.EmptyArrays;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author gaohang on 16/8/1.
 */
public class MutableString implements CharSequence, Comparable<MutableString> {

  public static final MutableString EMPTY = new MutableString();

  private char[] sources;
  private int off;
  private int len;

  public MutableString() {
    this(EmptyArrays.EMPTY_CHARS);
  }

  public MutableString(final char[] sources) {
    this(sources, 0, sources.length);
  }

  public MutableString(final char[] sources, final int off, final int len) {
    this(sources, off, len, true);
  }

  public MutableString(final char[] sources, final int off, final int len, boolean copy) {
    if (!copy) {
      this.sources = sources;
      this.off = off;
      this.len = len;
    } else {
      char[] chars = new char[len];
      System.arraycopy(sources, off, chars, 0, len);
      this.off = 0;
      this.len = len;
    }
  }

  @Override public int length() {
    return sources.length;
  }

  @Override public char charAt(final int index) {
    return sources[index + off];
  }

  public void setChar(final int index, char elem) {
    sources[index + off] = elem;
  }

  @Override public CharSequence subSequence(final int start, final int end) {
    checkArgument(start >= 0, "start不能小于0");
    checkArgument(end + start <= len, "end太大");
    return new MutableString(sources, off + start, start + end);
  }

  @Override public int compareTo(final MutableString o) {
    return 0;
  }
}
