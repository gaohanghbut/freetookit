package cn.yxffcode.easytookit.logqueue;

/**
 * 针对不需要编码与解码的字符串,对字符串做直接的返回
 *
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
