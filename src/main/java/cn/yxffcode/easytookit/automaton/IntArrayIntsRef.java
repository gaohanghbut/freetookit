package cn.yxffcode.easytookit.automaton;

/**
 * @author gaohang on 15/12/7.
 */
public class IntArrayIntsRef extends AbstractIntsRef {
    private final int[] source;

    public IntArrayIntsRef(final int[] source,
                           final int offset,
                           final int length) {
        super(offset, length);
        this.source = source;
    }

    public IntArrayIntsRef(final int[] word) {
        this(word, 0, word.length);
    }

    @Override
    public int element(final int index) {
        return source[index + offset];
    }

}
