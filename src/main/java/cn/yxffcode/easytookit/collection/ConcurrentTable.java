package cn.yxffcode.easytookit.collection;

import com.google.common.collect.Table;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 底层使用{@link java.util.concurrent.ConcurrentMap}实现的{@link Table}.
 * <p>
 * <pre>{@code ConcurrentMap<R, ConcurrentMap<C, V>>}</pre>
 *
 * 由于{@link java.util.concurrent.ConcurrentMap}是线程安全的，使用{@link ConcurrentTable} 的读操作是线程安全，但是{@link #delegate}的{@link
 * Table#put(Object, Object, Object)} 操作会判断第二级{@link java.util.concurrent.ConcurrentMap}是否存在，不存在就会创建,
 * 这个创建的过程是非线程安全的，{@link Table#remove(Object, Object)}方法在删除元素后会 检查第二级{@link java.util.concurrent.ConcurrentMap}是否是空集合，如果是，则删除，
 * 删除过程也不是线程安全的，所以在{@link #put(Object, Object, Object)},{@link #remove(Object, Object)}, 和{@link
 * #putAll(Table)}三个方法需要加锁
 *
 *
 * @author gaohang on 15/8/12.
 * @see java.util.concurrent.ConcurrentMap
 * @see java.util.concurrent.ConcurrentHashMap
 * @see com.google.common.collect.HashBasedTable
 * <p>
 */
public class ConcurrentTable<R, C, V> implements Table<R, C, V>, Serializable {

    private static final long           serialVersionUID = 8050987423067929531L;
    private              Table<R, C, V> delegate         = MoreCollections.newConcurrentMapBasedTable();
    private              Object         dataWriteLock    = new Object();

    public ConcurrentTable() {
    }

    public static <R, C, V> ConcurrentTable<R, C, V> create() {
        return new ConcurrentTable<R, C, V>();
    }

    @Override
    public boolean contains(Object rowKey,
                            Object columnKey) {
        return delegate.contains(rowKey,
                                 columnKey);
    }

    @Override
    public boolean containsRow(Object rowKey) {
        return delegate.containsRow(rowKey);
    }

    @Override
    public boolean containsColumn(Object columnKey) {
        return delegate.containsColumn(columnKey);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public V get(Object rowKey,
                 Object columnKey) {
        return delegate.get(rowKey,
                            columnKey);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public V put(R rowKey,
                 C columnKey,
                 V value) {
        synchronized (dataWriteLock) {
            return delegate.put(rowKey,
                                columnKey,
                                value);
        }
    }

    @Override
    public void putAll(Table<? extends R, ? extends C, ? extends V> table) {
        synchronized (dataWriteLock) {
            delegate.putAll(table);
        }
    }

    @Override
    public V remove(Object rowKey,
                    Object columnKey) {
        synchronized (dataWriteLock) {
            return delegate.remove(rowKey,
                                   columnKey);
        }
    }

    @Override
    public Map<C, V> row(R rowKey) {
        return delegate.row(rowKey);
    }

    @Override
    public Map<R, V> column(C columnKey) {
        return delegate.column(columnKey);
    }

    @Override
    public Set<Cell<R, C, V>> cellSet() {
        return delegate.cellSet();
    }

    @Override
    public Set<R> rowKeySet() {
        return delegate.rowKeySet();
    }

    @Override
    public Set<C> columnKeySet() {
        return delegate.columnKeySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public Map<R, Map<C, V>> rowMap() {
        return delegate.rowMap();
    }

    @Override
    public Map<C, Map<R, V>> columnMap() {
        return delegate.columnMap();
    }

}
