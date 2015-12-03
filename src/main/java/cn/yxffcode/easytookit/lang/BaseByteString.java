package cn.yxffcode.easytookit.lang;

import cn.yxffcode.easytookit.io.CharsetUtil;
import cn.yxffcode.easytookit.utils.ConcurrentUtils;
import cn.yxffcode.easytookit.utils.StringUtils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 从netty里copy而来
 */
public class BaseByteString {
    protected interface ByteStringFactory {
        BaseByteString newInstance(byte[] value,
                                   int start,
                                   int length,
                                   boolean copy);
    }

    private static final   ByteStringFactory DEFAULT_FACTORY = new ByteStringFactory() {
        @Override
        public BaseByteString newInstance(byte[] value,
                                          int start,
                                          int length,
                                          boolean copy) {
            return new BaseByteString(value, start, length, copy);
        }
    };
    public static final    BaseByteString    EMPTY_STRING    = new BaseByteString(0);
    protected static final int               HASH_CODE_PRIME = 31;

    protected final byte[] value;
    private final   int    offset;
    private final   int    length;
    private         int    hash;

    BaseByteString(int length) {
        value = new byte[length];
        offset = 0;
        this.length = length;
    }

    public BaseByteString(byte[] value) {
        this(value, true);
    }

    public BaseByteString(byte[] value,
                          boolean copy) {
        this(value, 0, checkNotNull(value, "value").length, copy);
    }

    public BaseByteString(byte[] value,
                          int start,
                          int length,
                          boolean copy) {
        if (copy) {
            this.value = Arrays.copyOfRange(value, start, start + length);
            this.offset = 0;
            this.length = length;
        } else {
            if (start < 0 || start > value.length - length) {
                throw new IndexOutOfBoundsException("expected: " + "0 <= start(" + start + ") <= start + length(" +
                                                    length + ") <= " + "value.length(" + value.length + ')');
            }
            this.value = value;
            this.offset = start;
            this.length = length;
        }
    }

    public BaseByteString(BaseByteString value,
                          boolean copy) {
        checkNotNull(value, "value");
        this.length = value.length();
        this.hash = value.hash;
        if (copy) {
            this.value = new byte[length];
            System.arraycopy(value.array(), value.arrayOffset(), this.value, 0, length);
            this.offset = 0;
        } else {
            this.value = value.array();
            this.offset = value.offset;
        }
    }

    public BaseByteString(ByteBuffer value) {
        this(value, true);
    }

    public BaseByteString(ByteBuffer value,
                          boolean copy) {
        this(value, value.position(), checkNotNull(value, "value").remaining(), copy);
    }

    public BaseByteString(ByteBuffer value,
                          int start,
                          int length,
                          boolean copy) {
        if (start < 0 || length > checkNotNull(value, "value").capacity() - start) {
            throw new IndexOutOfBoundsException("expected: " + "0 <= start(" + start + ") <= start + length(" + length
                                                + ") <= " + "value.capacity(" + value.capacity() + ')');
        }

        if (value.hasArray()) {
            if (copy) {
                final int bufferOffset = value.arrayOffset() + start;
                this.value = Arrays.copyOfRange(value.array(), bufferOffset, bufferOffset + length);
                offset = 0;
                this.length = length;
            } else {
                this.value = value.array();
                this.offset = start;
                this.length = length;
            }
        } else {
            this.value = new byte[length];
            int oldPos = value.position();
            value.get(this.value, 0, length);
            value.position(oldPos);
            this.offset = 0;
            this.length = length;
        }
    }

    public BaseByteString(char[] value,
                          Charset charset) {
        this(value, charset, 0, checkNotNull(value, "value").length);
    }

    public BaseByteString(char[] value,
                          Charset charset,
                          int start,
                          int length) {
        if (start < 0 || length > checkNotNull(value, "value").length - start) {
            throw new IndexOutOfBoundsException("expected: " + "0 <= start(" + start + ") <= start + length(" + length
                                                + ") <= " + "length(" + length + ')');
        }

        CharBuffer     cbuf         = CharBuffer.wrap(value, start, start + length);
        CharsetEncoder encoder      = CharsetUtil.getEncoder(charset);
        ByteBuffer     nativeBuffer = ByteBuffer.allocate((int) (encoder.maxBytesPerChar() * length));
        encoder.encode(cbuf, nativeBuffer, true);
        final int bufferOffset = nativeBuffer.arrayOffset();
        this.value = Arrays.copyOfRange(nativeBuffer.array(), bufferOffset, bufferOffset + nativeBuffer.position());
        this.offset = 0;
        this.length = this.value.length;
    }

    public BaseByteString(CharSequence value,
                          Charset charset) {
        this(value, charset, 0, checkNotNull(value, "value").length());
    }

    public BaseByteString(CharSequence value,
                          Charset charset,
                          int start,
                          int length) {
        if (start < 0 || length > checkNotNull(value, "value").length() - start) {
            throw new IndexOutOfBoundsException("expected: " + "0 <= start(" + start + ") <= start + length(" + length
                                                + ") <= " + "length(" + value.length() + ')');
        }

        CharBuffer     cbuf         = CharBuffer.wrap(value, start, start + length);
        CharsetEncoder encoder      = CharsetUtil.getEncoder(charset);
        ByteBuffer     nativeBuffer = ByteBuffer.allocate((int) (encoder.maxBytesPerChar() * length));
        encoder.encode(cbuf, nativeBuffer, true);
        final int offset = nativeBuffer.arrayOffset();
        this.value = Arrays.copyOfRange(nativeBuffer.array(), offset, offset + nativeBuffer.position());
        this.offset = 0;
        this.length = this.value.length;
    }

    public static final BaseByteString fromAscii(CharSequence value) {
        return new BaseByteString(value, CharsetUtil.US_ASCII);
    }

    public final int forEachByte(ByteProcessor visitor) throws Exception {
        return forEachByte0(0, length(), visitor);
    }

    public final int forEachByte(int index,
                                 int length,
                                 ByteProcessor visitor) throws Exception {
        if (index < 0 || length > length() - index) {
            throw new IndexOutOfBoundsException("expected: " + "0 <= index(" + index + ") <= start + length(" + length
                                                + ") <= " + "length(" + length() + ')');
        }

        return forEachByte0(index, length, visitor);
    }

    private int forEachByte0(int index,
                             int length,
                             ByteProcessor visitor) throws Exception {
        final int len = offset + length;
        for (int i = offset + index; i < len; ++ i) {
            if (! visitor.process(value[i])) {
                return i - offset;
            }
        }
        return - 1;
    }

    public final int forEachByteDesc(ByteProcessor visitor) throws Exception {
        return forEachByteDesc0(0, length(), visitor);
    }

    public final int forEachByteDesc(int index,
                                     int length,
                                     ByteProcessor visitor) throws Exception {
        if (index < 0 || length > length() - index) {
            throw new IndexOutOfBoundsException("expected: " + "0 <= index(" + index + ") <= start + length(" + length
                                                + ") <= " + "length(" + length() + ')');
        }

        return forEachByteDesc0(index, length, visitor);
    }

    private int forEachByteDesc0(int index,
                                 int length,
                                 ByteProcessor visitor) throws Exception {
        final int end = offset + index;
        for (int i = offset + index + length - 1; i >= end; -- i) {
            if (! visitor.process(value[i])) {
                return i - offset;
            }
        }
        return - 1;
    }

    public final byte byteAt(int index) {
        // We must do a range check here to enforce the access does not go outside our sub region of the array.
        // We rely on the array access itself to pick up the array out of bounds conditions
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("index: " + index + " must be in the range [0," + length + ")");
        }
        return value[index + offset];
    }

    public final boolean isEmpty() {
        return length == 0;
    }

    public final int length() {
        return length;
    }

    public void arrayChanged() {
        hash = 0;
    }

    public final byte[] array() {
        return value;
    }

    public final int arrayOffset() {
        return offset;
    }

    public final boolean isEntireArrayUsed() {
        return offset == 0 && length == value.length;
    }

    public final byte[] toByteArray() {
        return toByteArray(0, length());
    }

    public final byte[] toByteArray(int start,
                                    int end) {
        return Arrays.copyOfRange(value, start + offset, end + offset);
    }

    public final void copy(int srcIdx,
                           byte[] dst,
                           int dstIdx,
                           int length) {
        if (srcIdx < 0 || length > length() - srcIdx) {
            throw new IndexOutOfBoundsException("expected: " + "0 <= srcIdx(" + srcIdx + ") <= srcIdx + length("
                                                + length + ") <= srcLen(" + length() + ')');
        }

        System.arraycopy(value, srcIdx + offset, checkNotNull(dst, "dst"), dstIdx, length);
    }

    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0) {
            final int end = offset + length;
            for (int i = offset; i < end; ++ i) {
                h = h * HASH_CODE_PRIME + value[i];
            }

            hash = h;
        }
        return hash;
    }

    public BaseByteString subSequence(int start) {
        return subSequence(start, length());
    }

    public BaseByteString subSequence(int start,
                                      int end) {
        return subSequence(start, end, true);
    }

    public BaseByteString subSequence(int start,
                                      int end,
                                      boolean copy) {
        return subSequence(start, end, copy, DEFAULT_FACTORY);
    }

    protected BaseByteString subSequence(int start,
                                         int end,
                                         boolean copy,
                                         ByteStringFactory factory) {
        if (start < 0 || start > end || end > length()) {
            throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= end (" + end + ") <= length("
                                                + length() + ')');
        }

        if (start == 0 && end == length()) {
            return this;
        }

        if (end == start) {
            return EMPTY_STRING;
        }

        return factory.newInstance(value, start + offset, end - start, copy);
    }

    public final int parseAsciiInt() {
        return parseAsciiInt(0, length(), 10);
    }

    public final int parseAsciiInt(int radix) {
        return parseAsciiInt(0, length(), radix);
    }

    public final int parseAsciiInt(int start,
                                   int end) {
        return parseAsciiInt(start, end, 10);
    }

    public final int parseAsciiInt(int start,
                                   int end,
                                   int radix) {
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            throw new NumberFormatException();
        }

        if (start == end) {
            throw new NumberFormatException();
        }

        int     i        = start;
        boolean negative = byteAt(i) == '-';
        if (negative && ++ i == end) {
            throw new NumberFormatException(subSequence(start, end, false).toString());
        }

        return parseAsciiInt(i, end, radix, negative);
    }

    private int parseAsciiInt(int start,
                              int end,
                              int radix,
                              boolean negative) {
        int max        = Integer.MIN_VALUE / radix;
        int result     = 0;
        int currOffset = start;
        while (currOffset < end) {
            int digit = Character.digit((char) (value[currOffset++ + offset] & 0xFF), radix);
            if (digit == - 1) {
                throw new NumberFormatException(subSequence(start, end, false).toString());
            }
            if (max > result) {
                throw new NumberFormatException(subSequence(start, end, false).toString());
            }
            int next = result * radix - digit;
            if (next > result) {
                throw new NumberFormatException(subSequence(start, end, false).toString());
            }
            result = next;
        }
        if (! negative) {
            result = - result;
            if (result < 0) {
                throw new NumberFormatException(subSequence(start, end, false).toString());
            }
        }
        return result;
    }

    public final long parseAsciiLong() {
        return parseAsciiLong(0, length(), 10);
    }

    public final long parseAsciiLong(int radix) {
        return parseAsciiLong(0, length(), radix);
    }

    public final long parseAsciiLong(int start,
                                     int end) {
        return parseAsciiLong(start, end, 10);
    }

    public final long parseAsciiLong(int start,
                                     int end,
                                     int radix) {
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            throw new NumberFormatException();
        }

        if (start == end) {
            throw new NumberFormatException();
        }

        int     i        = start;
        boolean negative = byteAt(i) == '-';
        if (negative && ++ i == end) {
            throw new NumberFormatException(subSequence(start, end, false).toString());
        }

        return parseAsciiLong(i, end, radix, negative);
    }

    private long parseAsciiLong(int start,
                                int end,
                                int radix,
                                boolean negative) {
        long max        = Long.MIN_VALUE / radix;
        long result     = 0;
        int  currOffset = start;
        while (currOffset < end) {
            int digit = Character.digit((char) (value[currOffset++ + offset] & 0xFF), radix);
            if (digit == - 1) {
                throw new NumberFormatException(subSequence(start, end, false).toString());
            }
            if (max > result) {
                throw new NumberFormatException(subSequence(start, end, false).toString());
            }
            long next = result * radix - digit;
            if (next > result) {
                throw new NumberFormatException(subSequence(start, end, false).toString());
            }
            result = next;
        }
        if (! negative) {
            result = - result;
            if (result < 0) {
                throw new NumberFormatException(subSequence(start, end, false).toString());
            }
        }
        return result;
    }

    public final char parseChar() {
        return parseChar(0);
    }

    public char parseChar(int start) {
        if (start + 1 >= length()) {
            throw new IndexOutOfBoundsException("2 bytes required to convert to character. index " +
                                                start + " would go out of bounds.");
        }
        final int startWithOffset = start + offset;
        return (char) (((value[startWithOffset] & 0xFF) << 8) | (value[startWithOffset + 1] & 0xFF));
    }

    public final short parseAsciiShort() {
        return parseAsciiShort(0, length(), 10);
    }

    public final short parseAsciiShort(int radix) {
        return parseAsciiShort(0, length(), radix);
    }

    public final short parseAsciiShort(int start,
                                       int end) {
        return parseAsciiShort(start, end, 10);
    }

    public final short parseAsciiShort(int start,
                                       int end,
                                       int radix) {
        int   intValue = parseAsciiInt(start, end, radix);
        short result   = (short) intValue;
        if (result != intValue) {
            throw new NumberFormatException(subSequence(start, end, false).toString());
        }
        return result;
    }

    public final float parseAsciiFloat() {
        return parseAsciiFloat(0, length());
    }

    public final float parseAsciiFloat(int start,
                                       int end) {
        return Float.parseFloat(toString(start, end));
    }

    public final double parseAsciiDouble() {
        return parseAsciiDouble(0, length());
    }

    public final double parseAsciiDouble(int start,
                                         int end) {
        return Double.parseDouble(toString(start, end));
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof BaseByteString)) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        BaseByteString other = (BaseByteString) obj;
        return hashCode() == other.hashCode() &&
               ConcurrentUtils.equals(array(), arrayOffset(), arrayOffset() + length(),
                                      other.array(), other.arrayOffset(), other.arrayOffset() + other.length());
    }

    /**
     * Translates the entire byte string to a {@link String}.
     *
     * @see {@link #toString(int, int)}
     */
    @Override
    public String toString() {
        return toString(0, length());
    }

    /**
     * Translates the entire byte string to a {@link String} using the {@code charset} encoding.
     *
     * @see {@link #toString(Charset, int, int)}
     */
    public final String toString(Charset charset) {
        return toString(charset, 0, length());
    }

    /**
     * Translates the [{@code start}, {@code end}) range of this byte string to a {@link String}.
     *
     * @see {@link #toString(Charset, int, int)}
     */
    public String toString(int start,
                           int end) {
        return toString(CharsetUtil.ISO_8859_1, start, end);
    }

    /**
     * Translates the [{@code start}, {@code end}) range of this byte string to a {@link String}
     * using the {@code charset} encoding.
     */
    public String toString(Charset charset,
                           int start,
                           int end) {
        int length = end - start;
        if (length == 0) {
            return StringUtils.EMPTY;
        }

        if (start < 0 || length > length() - start) {
            throw new IndexOutOfBoundsException("expected: " + "0 <= start(" + start + ") <= srcIdx + length("
                                                + length + ") <= srcLen(" + length() + ')');
        }

        return new String(value, start + offset, length, charset);
    }
}
