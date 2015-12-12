package cn.yxffcode.easytookit.lang;

/**
 * @author gaohang on 15/12/7.
 */
public class StringIntsRef extends AbstractIntsRef {

    private final String source;

    public StringIntsRef(final String word) {
        this(word, 0, word.length());
    }

    public StringIntsRef(final String source, final int offset, final int length) {
        super(offset, length);
        this.source = source;
    }

    @Override
    public int element(final int index) {
        return source.charAt(index + offset);
    }

    @Override
    public String toString() {
        return source;
    }
}
