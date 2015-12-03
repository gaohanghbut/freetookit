package cn.yxffcode.easytookit.lang;

/**
 * 使用面向int类型数据的StringBuilder性能表现更好,
 * 此工具类在只有append(int)的场景下可用于取代{@link StringBuilder}
 * <pre>
 *     {@code
 *          IntArrayStringBuilder appender = new IntArrayStringBuilder();
 *          try (Reader in = xxx) {
 *              appender.append(in.read());
 *          }
 *          return appender.toString();
 *     }
 * </pre>
 * <p/>
 * 支持只读模式下的切片{@link #slice(int, int)},切片表示此StringBuilder的一个部分,
 * 是一个视图,会随着原始StringBuilder的修改而变动,但不能直接修改一个切片
 *
 * @author gaohang on 15/11/17.
 */
public class IntArrayStringBuilder {

    private int[] dest;
    private int   pos;

    public IntArrayStringBuilder() {
        this(10);
    }

    public IntArrayStringBuilder(int initSize) {
        this.dest = new int[initSize];
    }

    public IntArrayStringBuilder append(int c) {
        if (pos == dest.length) {
            resize();
        }
        dest[pos++] = c;
        return this;
    }

    public IntArrayStringBuilder append(int... cs) {
        if (pos + cs.length > dest.length) {
            resize();
        }
        for (int c : cs) {
            dest[pos++] = c;
        }
        return this;
    }

    public int element(int index) {
        return dest[index];
    }

    public void clear() {
        pos = 0;
    }

    public int length() {
        return pos;
    }

    public boolean isEmpty() {
        return pos == 0;
    }

    public boolean isBlank() {
        for (int i = 0; i < pos; i++) {
            if (! Character.isWhitespace(dest[i])) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        return new String(toCharArray());
    }

    public String toString(int off,
                           int len) {
        return new String(toCharArray(off, len));
    }

    public char[] toCharArray() {
        return toCharArray(0, pos);
    }

    public boolean endWith(IntArrayStringBuilder appender) {
        int thisLength = length();
        int thatLength = appender.length();
        if (thisLength < thatLength) {
            return false;
        }
        if (thisLength == thatLength) {
            return equals(appender);
        }
        for (int i = thatLength - 1, j = thisLength - 1; i >= 0; -- i, -- j) {
            if (appender.element(i) != element(j)) {
                return false;
            }
        }
        return true;
    }

    public boolean startWith(IntArrayStringBuilder appender) {
        int thisLength = length();
        int thatLength = appender.length();
        if (thisLength < thatLength) {
            return false;
        }
        if (thisLength == thatLength) {
            return equals(appender);
        }
        for (int i = 0, j = 0; i < appender.length(); ++ i, ++ j) {
            if (appender.element(i) != element(j)) {
                return false;
            }
        }
        return true;
    }

    public IntArrayStringBuilder slice(int offset,
                                       int length) {
        return new SliceStringBuilder(this, offset, length);
    }

    public IntArrayStringBuilder slice(int offset) {
        return slice(offset, this.length() - offset);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (! getClass().isInstance(o)) {
            return false;
        }
        final IntArrayStringBuilder that = (IntArrayStringBuilder) o;
        if (this.length() != that.length()) {
            return false;
        }
        for (int i = 0; i < length(); i++) {
            if (this.element(i) != that.element(i)) {
                return false;
            }
        }
        return true;
    }

    public char[] toCharArray(int off,
                              int len) {
        char[] chars = new char[len];
        for (int i = off, j = len + off; i < j; i++) {
            chars[i - off] = (char) dest[i];
        }
        return chars;
    }

    private void resize() {
        int[] ndest = new int[dest.length * 2];
        System.arraycopy(dest, 0, ndest, 0, pos);
        this.dest = ndest;
    }

    private final class SliceStringBuilder extends IntArrayStringBuilder {

        private final IntArrayStringBuilder delegate;
        private final int                   offset;
        private final int                   length;

        private SliceStringBuilder(final IntArrayStringBuilder delegate,
                                   final int offset,
                                   final int length) {
            super(0);
            this.delegate = delegate;
            this.offset = offset;
            this.length = length;
        }


        @Override
        public char[] toCharArray(final int off,
                                  final int len) {
            return super.toCharArray(off + this.offset, len);
        }

        @Override
        public IntArrayStringBuilder append(final int c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public IntArrayStringBuilder append(final int... cs) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int element(final int index) {
            return delegate.element(index + this.offset);
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int length() {
            return this.length;
        }

        @Override
        public boolean isEmpty() {
            return this.length == 0;
        }

        @Override
        public boolean isBlank() {
            for (int i = this.offset, j = this.offset + this.length; i < j; i++) {
                if (! Character.isWhitespace(delegate.element(i))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            return delegate.toString(this.offset, this.length);
        }

        @Override
        public String toString(final int off,
                               final int len) {
            return delegate.toString(this.offset + len, len);
        }

        @Override
        public char[] toCharArray() {
            return delegate.toCharArray(this.offset, this.length);
        }


        @Override
        public IntArrayStringBuilder slice(final int offset,
                                           final int length) {
            return delegate.slice(this.offset + offset, length);
        }

    }
}
