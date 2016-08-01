package cn.yxffcode.freetookit.collection;

import com.google.common.base.Supplier;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 针对读优化的线程安全的Map,使用copy-on-write的方式,修改的开销会非常大
 *
 * @author gaohang on 16/8/1.
 */
public class CopyOnWriterMap<K, V> implements ConcurrentMap<K, V> {
  private volatile Map<K, V> backingMap;
  private final Supplier<Map<K, V>> supplier;

  public CopyOnWriterMap(Supplier<Map<K, V>> supplier) {
    this.supplier = checkNotNull(supplier);
  }

  @Override public int size() {
    return backingMap.size();
  }

  @Override public boolean isEmpty() {
    return backingMap.isEmpty();
  }

  @Override public boolean containsKey(final Object key) {
    return backingMap.containsKey(key);
  }

  @Override public boolean containsValue(final Object value) {
    return backingMap.containsValue(value);
  }

  @Override public V get(final Object key) {
    return backingMap.get(key);
  }

  @Override public synchronized V put(final K key, final V value) {
    Map<K, V> newMap = supplier.get();
    newMap.putAll(this.backingMap);
    V put = newMap.put(key, value);
    setNewMap(newMap);
    return put;
  }

  private void setNewMap(final Map<K, V> newMap) {
    this.backingMap = Collections.unmodifiableMap(newMap);
  }

  @Override public synchronized V remove(final Object key) {
    if (backingMap.isEmpty()) {
      return null;
    }
    Map<K, V> newMap = supplier.get();
    V value = null;
    for (Entry<K, V> entry : backingMap.entrySet()) {
      if (entry.getKey().equals(key)) {
        value = entry.getValue();
        continue;
      }
      newMap.put(entry.getKey(), entry.getValue());
    }
    setNewMap(newMap);
    return value;
  }

  @Override public synchronized void putAll(final Map<? extends K, ? extends V> m) {
    if (m == null || m.isEmpty()) {
      return;
    }
    Map<K, V> newMap = supplier.get();
    newMap.putAll(backingMap);
    newMap.putAll(m);
    setNewMap(newMap);
  }

  @Override public synchronized void clear() {
    if (backingMap.isEmpty()) {
      return;
    }
    backingMap = Collections.emptyMap();
  }

  @Override public Set<K> keySet() {
    //需要针对当前副本做操作,不允许修改副本
    final Map<K, V> snapshot = this.backingMap;
    return new AbstractSet<K>() {
      public Iterator<K> iterator() {
        return new Iterator<K>() {
          private Iterator<Entry<K, V>> i = snapshot.entrySet().iterator();

          public boolean hasNext() {
            return i.hasNext();
          }

          public K next() {
            return i.next().getKey();
          }

          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }

      public int size() {
        return snapshot.size();
      }

      public boolean isEmpty() {
        return snapshot.isEmpty();
      }

      public void clear() {
        //不允在KeySet里许修改
        throw new UnsupportedOperationException();
      }

      public boolean contains(Object k) {
        return snapshot.containsKey(k);
      }
    };
  }

  @Override public Collection<V> values() {
    final Map<K, V> snapshot = this.backingMap;
    return new AbstractCollection<V>() {
      public Iterator<V> iterator() {
        return new Iterator<V>() {
          private Iterator<Entry<K, V>> i = snapshot.entrySet().iterator();

          public boolean hasNext() {
            return i.hasNext();
          }

          public V next() {
            return i.next().getValue();
          }

          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }

      public int size() {
        return snapshot.size();
      }

      public boolean isEmpty() {
        return snapshot.isEmpty();
      }

      public void clear() {
        //不允在KeySet里许修改
        throw new UnsupportedOperationException();
      }

      public boolean contains(Object v) {
        return snapshot.containsValue(v);
      }
    };
  }

  @Override public Set<Entry<K, V>> entrySet() {
    final Map<K, V> snapshot = this.backingMap;
    return new AbstractSet<Entry<K, V>>() {
      final Set<Entry<K, V>> entries = snapshot.entrySet();

      public Iterator<Entry<K, V>> iterator() {
        return new Iterator<Entry<K, V>>() {
          private Iterator<Entry<K, V>> i = entries.iterator();

          public boolean hasNext() {
            return i.hasNext();
          }

          public Entry<K, V> next() {
            return i.next();
          }

          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }

      public int size() {
        return snapshot.size();
      }

      public boolean isEmpty() {
        return snapshot.isEmpty();
      }

      public void clear() {
        //不允在KeySet里许修改
        throw new UnsupportedOperationException();
      }

      public boolean contains(Entry<K, V> entry) {
        return entries.contains(entry);
      }
    };
  }

  @Override public synchronized V putIfAbsent(final K key, final V value) {
    if (containsKey(key)) {
      return get(key);
    }
    put(key, value);
    return null;
  }

  @Override public synchronized boolean remove(final Object key, final Object value) {
    V exists = backingMap.get(key);
    if (Objects.equals(exists, value)) {
      remove(key);
      return true;
    }
    return false;
  }

  @Override public synchronized boolean replace(final K key, final V oldValue, final V newValue) {
    if (Objects.equals(oldValue, newValue)) {
      return false;
    }
    V exists = backingMap.get(key);
    if (!Objects.equals(exists, oldValue)) {
      return false;
    }
    replace(key, newValue);
    return true;
  }

  @Override public synchronized V replace(final K key, final V value) {
    V exists = backingMap.get(key);
    if (Objects.equals(exists, value)) {
      return exists;
    }
    put(key, value);
    return exists;
  }
}
