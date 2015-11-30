package cn.yxffcode.easytookit.logqueue;

/**
 * 对象到文本内容的编码与解码
 *
 * @author gaohang on 15/9/11.
 */
public interface Codec<T> {
    String encode(T obj);

    T decode(String data);
}
