package cn.yxffcode.easytookit.lang;

/**
 * 对整型数串的表示,如同{@link CharSequence}表示的是字符序列,IntSequence表示整型数序列
 *
 * @author gaohang on 15/12/7.
 */
public interface IntSequence extends Comparable<IntSequence> {
  int element(int index);

  int length();
}
