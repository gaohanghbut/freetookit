package cn.yxffcode.freetookit.algorithm;

/**
 * 前缀搜索接口,提供了三种实现方式:1.排好序的数组;2.SortedTree;3.字典树
 *
 * @author gaohang on 16/5/20.
 */
public interface PrefixSearcher {
  Iterable<String> search(String prefix);
}
