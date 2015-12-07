package cn.yxffcode.easytookit.automaton;

/**
 * @author gaohang on 15/12/7.
 */
public class CharArrayIntsRef extends AbstractIntsRef {
    private final char[] source;

    protected CharArrayIntsRef(final char[] source,
                               final int offset,
                               final int length) {
        super(offset, length);
        this.source = source;
    }

    @Override
    public int element(final int index) {
        return source[index + offset];
    }

}
