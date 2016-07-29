package cn.yxffcode.freetookit.collection;

import com.google.common.base.Supplier;
import com.google.common.collect.Table;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author gaohang on 7/16/16.
 */
public class StandardMultiTable<R, C, V> implements MultiTable<R, C, V> {

  private final Table<R, C, Collection<V>> backTable;
  private Supplier<Collection<V>> valueCollectionSupplier;

  private int size;

  protected StandardMultiTable(Supplier<Table<R, C, Collection<V>>> tableSupplier,
                               Supplier<Collection<V>> valueCollectionSupplier) {
    this.backTable = tableSupplier.get();
    this.valueCollectionSupplier = valueCollectionSupplier;
  }

  @Override
  public boolean contains(Object rowKey, Object columnKey) {
    return backTable.contains(rowKey, columnKey);
  }

  @Override
  public boolean containsRow(Object rowKey) {
    return backTable.containsRow(rowKey);
  }

  @Override
  public boolean containsColumn(Object columnKey) {
    return backTable.containsColumn(columnKey);
  }

  @Override
  public boolean containsValue(Object value) {
    for (Map.Entry<R, Map<C, Collection<V>>> re : rowMap().entrySet()) {
      for (Map.Entry<C, Collection<V>> ce : re.getValue().entrySet()) {
        if (ce.getValue().contains(value)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public Collection<V> get(Object rowKey, Object columnKey) {
    return backTable.get(rowKey, columnKey);
  }

  @Override
  public boolean isEmpty() {
    return backTable.isEmpty();
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public void clear() {
    backTable.clear();
    size = 0;
  }

  @Override
  public void put(R rowKey, C columnKey, V value) {
    Collection<V> values = backTable.get(rowKey, columnKey);
    if (values == null) {
      backTable.put(rowKey, columnKey, values = valueCollectionSupplier.get());
    }
    values.add(value);
    ++size;
  }

  @Override
  public void putAll(R rowKey, C columnKey, Iterable<? extends V> values) {
    checkNotNull(rowKey);
    checkNotNull(columnKey);
    checkNotNull(values);
    for (V value : values) {
      put(rowKey, columnKey, value);
    }
  }

  @Override
  public void putAll(MultiTable<? extends R, ? extends C, ? extends V> table) {
    checkNotNull(table);
    if (table.isEmpty()) {
      return;
    }
    for (Map.Entry<? extends R, ? extends Map<? extends C, ? extends Collection<? extends V>>> re : table.rowMap()
            .entrySet()) {
      for (Map.Entry<? extends C, ? extends Collection<? extends V>> ce : re.getValue().entrySet()) {
        putAll(re.getKey(), ce.getKey(), ce.getValue());
      }
    }
  }

  @Override
  public void remove(Object rowKey, Object columnKey, Object value) {
    Collection<V> values = backTable.get(rowKey, columnKey);
    if (values == null) {
      return;
    }
    if (values.remove(value)) {
      --size;
    }
    if (values.isEmpty()) {
      backTable.remove(rowKey, columnKey);
    }
  }

  @Override
  public Collection<V> remove(Object rowKey, Object columnKey) {
    Collection<V> values = backTable.remove(rowKey, columnKey);
    if (values == null) {
      return Collections.emptyList();
    }
    size -= values.size();
    return values;
  }

  @Override
  public Map<C, Collection<V>> row(R rowKey) {
    return backTable.row(rowKey);
  }

  @Override
  public Map<R, Collection<V>> column(C columnKey) {
    return backTable.column(columnKey);
  }

  @Override
  public Set<R> rowKeySet() {
    return backTable.rowKeySet();
  }

  @Override
  public Set<C> columnKeySet() {
    return backTable.columnKeySet();
  }

  @Override
  public Collection<V> values() {
    Collection<Collection<V>> values = backTable.values();
    return GroupCollection.create(values);
  }

  @Override
  public Map<R, Map<C, Collection<V>>> rowMap() {
    return backTable.rowMap();
  }

  @Override
  public Map<C, Map<R, Collection<V>>> columnMap() {
    return backTable.columnMap();
  }
}
