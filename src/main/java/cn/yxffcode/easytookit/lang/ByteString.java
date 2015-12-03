package cn.yxffcode.easytookit.lang;

import java.nio.charset.Charset;

/**
 * @author gaohang on 15/12/3.
 */
public class ByteString extends BaseByteString {

    private final ByteStringFactory FACTORY = new ByteStringFactory() {
        @Override
        public BaseByteString newInstance(final byte[] value,
                                          final int start,
                                          final int length,
                                          final boolean copy) {
            return new ByteString(value, start, length, copy, charset);
        }
    };

    private final Charset charset;

    public ByteString(final byte[] value,
                      final Charset charset) {
        super(value);
        this.charset = charset;
    }

    public ByteString(final byte[] value,
                      final int offset,
                      final int length,
                      final boolean copy,
                      final Charset charset) {
        super(value, offset, length + offset, copy);
        this.charset = charset;
    }

    public ByteString(final byte[] value) {
        this(value, Charset.defaultCharset());
    }

    /**
     * Translates the [{@code start}, {@code end}) range of this byte string to a {@link String}.
     *
     * @see {@link #toString(Charset, int, int)}
     */
    public final String toString(int start,
                                 int end) {
        return toString(charset, start, end);
    }

    public static void main(String[] args) {
        ByteString str = new ByteString("厘米库".getBytes());
        System.out.println(str);
    }

    @Override
    public BaseByteString subSequence(int start,
                                      int end,
                                      boolean copy) {
        return subSequence(start, end, copy, FACTORY);
    }

}
