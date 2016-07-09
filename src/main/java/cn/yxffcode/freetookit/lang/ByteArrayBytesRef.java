package cn.yxffcode.freetookit.lang;

import cn.yxffcode.freetookit.utils.ArrayUtils;

/**
 * @author gaohang on 15/12/10.
 */
public class ByteArrayBytesRef implements BytesRef {
  private final byte[] data;
  private final int offset;
  private final int length;

  public ByteArrayBytesRef(final byte[] data) {
    this(data, 0, data.length);
  }

  public ByteArrayBytesRef(final byte[] data, final int offset, final int length) {
    this.data = data;
    this.offset = offset;
    this.length = length;
  }

  @Override public byte element(final int index) {
    return data[index + offset];
  }

  @Override public int length() {
    return length;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ByteArrayBytesRef that = (ByteArrayBytesRef) o;
    if (this.length != that.length) {
      return false;
    }
    for (int i = 0; i < length; i++) {
      if (this.element(i) != that.element(i)) {
        return false;
      }
    }
    return true;
  }

  @Override public int hashCode() {
    return ArrayUtils.hashCode(data, offset, length);
  }

  @Override public String toString() {
    return ArrayUtils.toString(data, offset, length);
  }
}
