package cn.yxffcode.easytookit.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author gaohang on 15/8/18.
 */
public final class UnsafeUtils {

    public static final Unsafe UNSAFE;

    static {
        Unsafe unsafe;
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);

        } catch (Throwable cause) {
            // Unsafe.copyMemory(Object, long, Object, long, long) unavailable.
            unsafe = null;
        }
        UNSAFE = unsafe;
    }

    private UnsafeUtils() {
    }
}
