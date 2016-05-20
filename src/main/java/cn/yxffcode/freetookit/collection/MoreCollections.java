package cn.yxffcode.freetookit.collection;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author gaohang on 15/8/12.
 */
public final class MoreCollections {
  private static final SortedSet<Object> EMPTY_SORTED_SET = new TreeSet<Object>() {
    @Override public boolean add(Object o) {
      throw new UnsupportedOperationException("Immutable set");
    }
  };

  private MoreCollections() {
  }

  /**
   * 创建一个基于{@link java.util.concurrent.ConcurrentMap}的{@link Table}， {@link Table}无法保证外层{@link Table#put(Object,
   * Object, Object)}方法 是原子的。基于{@link java.util.concurrent.ConcurrentMap}的也无法保证。
   */
  public static <R, C, V> Table<R, C, V> newConcurrentMapBasedTable() {
    return Tables.newCustomTable(Maps.<R, Map<C, V>>newConcurrentMap(), new Supplier<Map<C, V>>() {
      @Override public Map<C, V> get() {
        return Maps.newConcurrentMap();
      }
    });
  }

  public static <E> List<E> group(List<E>... lists) {
    return GroupList.create(lists);
  }

  public static <E> SortedSet<E> emptySortedSet() {
    return (SortedSet<E>) EMPTY_SORTED_SET;
  }
}
