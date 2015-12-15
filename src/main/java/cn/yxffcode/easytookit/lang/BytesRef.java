package cn.yxffcode.easytookit.lang;

/**
 * 对字节串的表示
 *
 * @author gaohang on 15/12/9.
 */
public interface BytesRef {

  byte element(int index);

  int length();
}
