package cn.yxffcode.easytookit.utils;

import com.google.common.base.Throwables;

/**
 * @author gaohang on 15/12/4.
 */
public final class Reflections {
    private Reflections() {
    }

    public static <T>T defaultConstruct(Class<T> type) {
        try {
            return type.newInstance();
        } catch (Exception e) {
            Throwables.propagate(e);
            return null;
        }
    }
}
