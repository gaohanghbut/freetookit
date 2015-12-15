package cn.yxffcode.easytookit.lang;

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

  public ByteArrayBytesRef(final byte[] data,
                           final int offset,
                           final int length
  ) {
    this.data = data;
    this.offset = offset;
    this.length = length;
  }

  @Override
  public byte element(final int index) {
    return data[index + offset];
  }

  @Override
  public int length() {
    return length;
  }
}
