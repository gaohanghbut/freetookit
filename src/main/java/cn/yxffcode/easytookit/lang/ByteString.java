package cn.yxffcode.easytookit.lang;

import java.nio.charset.Charset;

/**
 * 更能用的ByteString
 *
 * @author gaohang on 15/12/3.
 */
public class ByteString extends BaseByteString {

    private final Charset charset;
    private final ByteStringFactory FACTORY = new ByteStringFactory() {
        @Override
        public BaseByteString newInstance(final byte[] value,
                                          final int start,
                                          final int length,
                                          final boolean copy
                                         ) {
            return new ByteString(value, start, length, copy, charset);
        }
    };

    public ByteString(final byte[] value,
                      final int offset,
                      final int length,
                      final boolean copy,
                      final Charset charset
                     ) {
        super(value, offset, length + offset, copy);
        this.charset = charset;
    }

    public ByteString(final byte[] value) {
        this(value, Charset.defaultCharset());
    }

    public ByteString(final byte[] value,
                      final Charset charset
                     ) {
        super(value);
        this.charset = charset;
    }

    @Override
    public BaseByteString subSequence(int start,
                                      int end,
                                      boolean copy
                                     ) {
        return subSequence(start, end, copy, FACTORY);
    }

    /**
     * Translates the [{@code start}, {@code end}) range of this byte string to a {@link String}.
     *
     * @see {@link #toString(Charset, int, int)}
     */
    public final String toString(int start,
                                 int end
                                ) {
        return toString(charset, start, end);
    }

}
