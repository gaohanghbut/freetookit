package cn.yxffcode.freetookit.lang;

import cn.yxffcode.freetookit.utils.StringUtils;

import static cn.yxffcode.freetookit.utils.ArrayUtils.reverseGrow;

/**
 * 类似于{@link StringBuilder}
 *
 * @author gaohang on 16/5/20.
 */
public class ReverseStringBuilder {

  private static final int DEFAULT_CHARS_INIT_LEN = 10;
  private char[] data;
  private int pos;

  public ReverseStringBuilder() {
    this(DEFAULT_CHARS_INIT_LEN);
  }

  public ReverseStringBuilder(int len) {
    this.data = new char[len];
    this.pos = len;
  }

  public ReverseStringBuilder append(char c) {
    if (pos <= 0) {
      int oldLen = this.data.length;
      this.data = reverseGrow(data, data.length + DEFAULT_CHARS_INIT_LEN);
      this.pos = this.data.length - oldLen;
    }
    data[--pos] = c;
    return this;
  }

  /**
   * 反向转换成String
   */
  @Override public String toString() {
    if (pos == data.length) {
      return StringUtils.EMPTY;
    }
    return new String(data, pos, data.length - pos);
  }
}
