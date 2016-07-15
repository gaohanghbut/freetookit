package cn.yxffcode.freetookit.collection;

import com.google.common.base.Supplier;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import java.util.Collection;

/**
 * @author gaohang on 7/16/16.
 */
public final class MultiTables {
  private MultiTables() {
  }

  public static <R, C, V> MultiTable<R, C, V> newHashMultiTable() {
    return new StandardMultiTable<>(new HashBasedTableSupplier<>(), new HashSetSupplier<>());
  }

  public static <R, C, V> MultiTable<R, C, V> newListHashMultiTable() {
    return new StandardMultiTable<>(new HashBasedTableSupplier<>(), new ArrayListSupplier<>());
  }

  public static <R, C, V> MultiTable<R, C, V> newCustomMultiTable(Supplier<Table<R, C, Collection<V>>> tableSupplier,
                                                                  Supplier<Collection<V>> valueCollectionSupplier) {
    return new StandardMultiTable<>(tableSupplier, valueCollectionSupplier);
  }

  private static class HashBasedTableSupplier<R, C, V> implements Supplier<Table<R, C, Collection<V>>> {
    @Override
    public Table<R, C, Collection<V>> get() {
      return HashBasedTable.create();
    }
  }

  private static class HashSetSupplier<V> implements Supplier<Collection<V>> {
    @Override
    public Collection<V> get() {
      return Sets.newHashSet();
    }
  }

  private static class ArrayListSupplier<V> implements Supplier<Collection<V>> {
    @Override
    public Collection<V> get() {
      return Lists.newArrayList();
    }
  }
}
