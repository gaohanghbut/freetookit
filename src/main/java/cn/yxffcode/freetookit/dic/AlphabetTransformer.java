package cn.yxffcode.freetookit.dic;

/**
 * @author gaohang on 15/12/12.
 */
public interface AlphabetTransformer {
  int wrap(int unwrapped);

  int unwrap(int wrapped);
}
