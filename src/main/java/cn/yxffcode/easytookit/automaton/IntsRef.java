package cn.yxffcode.easytookit.automaton;

/**
 * @author gaohang on 15/12/7.
 */
public interface IntsRef extends Comparable<IntsRef> {
    int element(int index);
    int length();
}
