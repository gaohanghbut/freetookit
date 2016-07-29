package cn.yxffcode.freetookit.lang;

/**
 * @author gaohang on 15/12/7.
 */
public class CharArrayIntSequence extends AbstractIntSequence {
  private final char[] source;

  protected CharArrayIntSequence(final char[] source, final int offset, final int length) {
    super(offset, length);
    this.source = source;
  }

  @Override public int element(final int index) {
    return source[index + offset];
  }

  @Override public IntSequence slice(int offset, int length) {
    if (offset < 0) {
      throw new IllegalArgumentException("offset cannot be negative " + offset);
    }
    if (offset + length > this.length) {
      throw new IndexOutOfBoundsException(
              "slice is too long, but the source length is "
                      + this.length);
    }
    return new CharArrayIntSequence(source, offset + this.offset, length);
  }

  @Override public String toString() {
    return new String(source);
  }
}
