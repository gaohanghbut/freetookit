package cn.yxffcode.easytookit.logqueue;

/**
 * @author gaohang on 15/9/11.
 */
public interface Codec<T> {
    String encode(T obj);

    T decode(String data);
}
