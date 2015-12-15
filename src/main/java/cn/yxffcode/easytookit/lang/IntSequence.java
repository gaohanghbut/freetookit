package cn.yxffcode.easytookit.lang;

/**
 * 对整型数串的表示
 *
 * @author gaohang on 15/12/7.
 */
public interface IntSequence extends Comparable<IntSequence> {
  int element(int index);

  int length();
}
