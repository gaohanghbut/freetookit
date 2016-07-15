package cn.yxffcode.freetookit.collection;

import cn.yxffcode.freetookit.utils.Reflections;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 基于TreeMap实现将Iterable对象转换成Map
 *
 * @author gaohang on 16/7/9.
 */
public class TreeBasedIterTransMap<K, V> extends TreeMap<K, V> implements IterTransMap<K, V> {
  public static <K, V> TreeBasedIterTransMap<K, V> immutableCopyOf(
          Iterable<V> meta, String keyProperty) {
    return new TreeBasedIterTransMap<>(meta, keyProperty, true);
  }

  public static <K, V> TreeBasedIterTransMap<K, V> newInstance(
          Iterable<V> meta, String keyProperty) {
    return new TreeBasedIterTransMap<>(meta, keyProperty, false);
  }

  private final Iterable<V> meta;

  private TreeBasedIterTransMap(Iterable<V> meta, String keyProperty, boolean immutableCopy) {
    checkNotNull(meta);
    checkArgument(!Strings.isNullOrEmpty(keyProperty));
    if (immutableCopy) {
      this.meta = ImmutableList.copyOf(meta);
    } else {
      this.meta = meta;
    }
    for (V value : this.meta) {
      K key = (K) Reflections.getField(keyProperty, value);
      super.put(key, value);
    }
  }

  @Override
  public V remove(Object key) {
    throw new UnsupportedOperationException("put is not supported by this map");
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException("put is not supported by this map");
  }

  @Override
  public Set<Map.Entry<K, V>> entrySet() {
    return Collections.unmodifiableSet(super.entrySet());
  }

  @Override
  public Iterable<V> metaData() {
    return meta;
  }

  @Override
  public V put(K key, V value) {
    throw new UnsupportedOperationException("put is not supported by this map");
  }
}
