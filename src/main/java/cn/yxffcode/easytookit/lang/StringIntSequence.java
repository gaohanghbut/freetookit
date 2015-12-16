package cn.yxffcode.easytookit.lang;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author gaohang on 15/12/7.
 */
public class StringIntSequence extends AbstractIntSequence {

  private final String source;

  public StringIntSequence(final String word) {
    this(word, 0, word.length());
  }

  public StringIntSequence(final String source, final int offset, final int length) {
    super(offset, length);
    this.source = source;
  }

  @Override public int element(final int index) {
    return source.charAt(index + offset);
  }

  @Override public IntSequence slice(int offset, int length) {
    checkState(offset >= 0 && offset + length <= this.length);
    return new StringIntSequence(this.source, offset + this.offset, length);
  }

  @Override public String toString() {
    return source;
  }
}
