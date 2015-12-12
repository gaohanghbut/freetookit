package cn.yxffcode.easytookit.lang;

import cn.yxffcode.easytookit.utils.UnsafeUtils;
import sun.misc.Unsafe;

import java.io.Serializable;

/**
 * @author gaohang on 15/8/6.
 * @see ModifiableInteger
 */
public class ModifiableLong extends Number implements Serializable {

    private static final long   serialVersionUID = 1478723535778680295L;
    private static final Unsafe UNSAFE           = UnsafeUtils.UNSAFE;

    private static final long VALUE_OFFSET;

    static {
        try {
            VALUE_OFFSET = UNSAFE.objectFieldOffset
                    (ModifiableLong.class.getDeclaredField("value"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }


    private volatile long value;

    public ModifiableLong() {
        this(0);
    }

    public ModifiableLong(int value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    /**
     * 非线程安全的加1
     */
    public long increaseAndGet() {
        return ++ value;
    }

    /**
     * 线程安全的加1
     */
    public void atomIncrease() {
        atomAdd(1);
    }

    private void atomAdd(long delta) {
        for (; ; ) {
            long current = value;
            long next    = current + delta;
            if (cas(current,
                    next)) {
                return;
            }
        }
    }

    private boolean cas(long current, long next) {
        return UNSAFE.compareAndSwapLong(this, VALUE_OFFSET, current, next);
    }

    /**
     * 线程安全的减1
     */
    public void atomDecrease() {
        atomAdd(- 1);
    }

    public long value() {
        return value;
    }

    public void setValue(long value) {
        for (; ; ) {
            long current = this.value;
            if (cas(current,
                    value)) {
                return;
            }
        }
    }
}
