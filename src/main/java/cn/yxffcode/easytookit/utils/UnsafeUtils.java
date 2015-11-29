package cn.yxffcode.easytookit.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author gaohang on 15/8/18.
 */
public final class UnsafeUtils {

    private UnsafeUtils() {
    }

    public static final Unsafe UNSAFE;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
