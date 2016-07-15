package cn.yxffcode.freetookit.collection;

import com.google.common.collect.Table;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author gaohang on 7/16/16.
 */
public interface MultiTable<R, C, V> {

  boolean contains(Object rowKey, Object columnKey);

  boolean containsRow(Object rowKey);

  boolean containsColumn(Object columnKey);

  boolean containsValue(Object value);

  Collection<V> get(Object rowKey, Object columnKey);

  boolean isEmpty();

  int size();

  boolean equals(Object obj);

  int hashCode();

  void clear();

  void put(R rowKey, C columnKey, V value);

  void putAll(R rowKey, C columnKey, Iterable<? extends V> values);

  void putAll(MultiTable<? extends R, ? extends C, ? extends V> table);

  void remove(Object rowKey, Object columnKey, Object value);

  Collection<V> remove(Object rowKey, Object columnKey);

  Map<C, Collection<V>> row(R rowKey);

  Map<R, Collection<V>> column(C columnKey);

  Set<R> rowKeySet();

  Set<C> columnKeySet();

  Collection<V> values();

  Map<R, Map<C, Collection<V>>> rowMap();

  Map<C, Map<R, Collection<V>>> columnMap();
}
