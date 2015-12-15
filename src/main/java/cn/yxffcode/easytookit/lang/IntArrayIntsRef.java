package cn.yxffcode.easytookit.lang;

/**
 * @author gaohang on 15/12/7.
 */
public class IntArrayIntsRef extends AbstractIntsRef {
  private final int[] source;

  public IntArrayIntsRef(final int[] word) {
    this(word, 0, word.length);
  }

  public IntArrayIntsRef(final int[] source, final int offset, final int length) {
    super(offset, length);
    this.source = source;
  }

  @Override
  public String toString() {
    IntArrayStringBuilder appender = new IntArrayStringBuilder();
    for (int i = 0, j = length(); i < j; i++) {
      appender.append(element(i));
    }
    return appender.toString();
  }

  @Override
  public int element(final int index) {
    return source[index + offset];
  }
}
