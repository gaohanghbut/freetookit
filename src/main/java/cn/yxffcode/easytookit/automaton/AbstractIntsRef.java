package cn.yxffcode.easytookit.automaton;

import com.google.common.primitives.Ints;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author gaohang on 15/12/7.
 */
public abstract class AbstractIntsRef implements IntsRef {

    protected final int offset;
    protected final int length;

    protected AbstractIntsRef(final int offset,
                              final int length) {
        this.offset = offset;
        this.length = length;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public int compareTo(final IntsRef o) {
        checkNotNull(o);
        for (int i = 0; i < this.length() && i < o.length(); i++) {
            int compared = Ints.compare(this.element(i), o.element(i));
            if (compared != 0) {
                return compared;
            }
        }
        //前缀完全相等,则长度大的更大
        return Ints.compare(this.length(), o.length());
    }

}
