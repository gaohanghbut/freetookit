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
        Unsafe unsafe;
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);

            // Ensure the unsafe supports all necessary methods to work around the mistake in the latest OpenJDK.
            // https://github.com/netty/netty/issues/1061
            // http://www.mail-archive.com/jdk6-dev@openjdk.java.net/msg00698.html
            try {
                if (unsafe != null) {
                    unsafe.getClass()
                          .getDeclaredMethod(
                                  "copyMemory", Object.class, long.class, Object.class, long.class, long.class);
                }
            } catch (NoSuchMethodError t) {
                throw t;
            } catch (NoSuchMethodException e) {
                throw e;
            }
        } catch (Throwable cause) {
            // Unsafe.copyMemory(Object, long, Object, long, long) unavailable.
            unsafe = null;
        }
        UNSAFE = unsafe;
    }
}
