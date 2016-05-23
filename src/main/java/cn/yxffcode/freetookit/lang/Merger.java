package cn.yxffcode.freetookit.lang;

/**
 * 表示两个元素的合并
 *
 * @author gaohang on 16/5/17.
 */
public interface Merger<T> {
  T merge(T left, T right);
}
