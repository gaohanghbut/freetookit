package cn.yxffcode.freetookit.collection;

import java.util.Map;

/**
 * 将Iterable对象转换成Map
 *
 * @author gaohang on 16/7/9.
 */
public interface IterTransMap<K, V> extends Map<K, V> {
  Iterable<V> metaData();
}
