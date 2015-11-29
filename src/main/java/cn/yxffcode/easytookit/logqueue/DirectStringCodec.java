package cn.yxffcode.easytookit.logqueue;

/**
 * @author gaohang on 15/9/11.
 */
public class DirectStringCodec implements Codec<String> {
    @Override
    public String encode(String obj) {
        return obj;
    }

    @Override
    public String decode(String data) {
        return data;
    }
}
