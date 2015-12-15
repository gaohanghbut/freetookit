package cn.yxffcode.easytookit.participle;

/**
 * @author gaohang on 15/12/12.
 */
public interface AlphabetTransformer {
  int wrap(int unwrapped);

  int unwrap(int wrapped);
}
