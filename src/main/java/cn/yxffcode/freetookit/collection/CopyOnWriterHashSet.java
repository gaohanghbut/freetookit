package cn.yxffcode.freetookit.collection;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * @author gaohang on 16/8/1.
 */
public class CopyOnWriterHashSet<K> implements Set<K> {
  private final CopyOnWriterMap<K, Boolean> backingMap = new CopyOnWriterMap<>();

  @Override public int size() {
    return backingMap.size();
  }

  @Override public boolean isEmpty() {
    return backingMap.isEmpty();
  }

  @Override public boolean contains(final Object o) {
    return backingMap.containsKey(o);
  }

  @Override public Iterator<K> iterator() {
    return backingMap.keySet().iterator();
  }

  @Override public Object[] toArray() {
    return backingMap.keySet().toArray();
  }

  @Override public <T> T[] toArray(final T[] a) {
    return backingMap.keySet().toArray(a);
  }

  @Override public synchronized boolean add(final K k) {
    return backingMap.put(k, Boolean.TRUE);
  }

  @Override public synchronized boolean remove(final Object o) {
    return backingMap.remove(o);
  }

  @Override public boolean containsAll(final Collection<?> c) {
    return backingMap.keySet().containsAll(c);
  }

  @Override public synchronized boolean addAll(final Collection<? extends K> c) {
    if (c == null || c.isEmpty()) {
      return false;
    }
    HashMap<K, Boolean> map = Maps.newHashMapWithExpectedSize(c.size());
    for (K k : c) {
      map.put(k, Boolean.TRUE);
    }
    backingMap.putAll(map);
    return true;
  }

  // TODO: 16/8/1 性能优化
  @Override public synchronized boolean retainAll(final Collection<?> c) {
    if (c == null || c.isEmpty()) {
      return false;
    }
    boolean r = false;
    for (K k : backingMap.keySet()) {
      if (c.contains(k)) {
        r = r || backingMap.remove(k);
      }
    }
    return r;
  }

  // TODO: 16/8/1 性能优化
  @Override public synchronized boolean removeAll(final Collection<?> c) {
    boolean r = false;
    for (Object k : c) {
      r = r || backingMap.remove(k);
    }
    return r;
  }

  @Override public synchronized void clear() {
    backingMap.clear();
  }
}
