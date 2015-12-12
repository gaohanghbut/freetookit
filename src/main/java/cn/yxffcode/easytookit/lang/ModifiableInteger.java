package cn.yxffcode.easytookit.lang;

import cn.yxffcode.easytookit.utils.UnsafeUtils;
import sun.misc.Unsafe;

import java.io.Serializable;

/**
 * {@link Integer}是不可变对象，此类是int的可变包装，与{@link java.util.concurrent.atomic.AtomicInteger} 不同的是，{@link
 * ModifiableInteger}同时提供了原子更新方法和非原子的更新方法
 *
 * @author gaohang on 15/8/6.
 */
public class ModifiableInteger extends Number implements Serializable {

    private static final long   serialVersionUID = 1478723535778680295L;
    private static final Unsafe UNSAFE           = UnsafeUtils.UNSAFE;

    private static final long VALUE_OFFSET;

    static {
        try {
            VALUE_OFFSET = UNSAFE.objectFieldOffset
                    (ModifiableInteger.class.getDeclaredField("value"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }


    private volatile int value;

    public ModifiableInteger() {
        this(0);
    }

    public ModifiableInteger(int value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return value;
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
    public int increaseAndGet() {
        return ++ value;
    }

    /**
     * 线程安全的加1
     */
    public void atomIncrease() {
        atomAdd(1);
    }

    private void atomAdd(int delta) {
        for (; ; ) {
            int current = value;
            int next    = current + delta;
            if (UNSAFE.compareAndSwapInt(this, VALUE_OFFSET, current, next)) {
                break;
            }
        }
    }

    /**
     * 线程安全的减1
     */
    public void atomDecrease() {
        atomAdd(- 1);
    }

    public int value() {
        return value;
    }
}
