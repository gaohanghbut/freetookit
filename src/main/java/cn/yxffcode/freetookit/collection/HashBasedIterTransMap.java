package cn.yxffcode.freetookit.collection;

import cn.yxffcode.freetookit.utils.Reflections;
import com.google.common.collect.ImmutableList;

import java.util.HashMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 基于HashMap实现将Iterable对象转换成Map
 *
 * @author gaohang on 16/7/9.
 */
public class HashBasedIterTransMap<K, V> extends HashMap<K, V> implements IterTransMap<K, V> {
  public static <K, V> HashBasedIterTransMap<K, V> immutableCopyOf(
                                            Iterable<V> meta, String keyProperty) {
    return new HashBasedIterTransMap<>(meta, keyProperty, true);
  }

  public static <K, V> HashBasedIterTransMap<K, V> newInstance(
                                            Iterable<V> meta, String keyProperty) {
    return new HashBasedIterTransMap<>(meta, keyProperty, false);
  }

  private final Iterable<V> meta;

  private HashBasedIterTransMap(Iterable<V> meta, String keyProperty, boolean immutableCopy) {
    checkNotNull(meta);
    if (immutableCopy) {
      this.meta = ImmutableList.copyOf(meta);
    } else {
      this.meta = meta;
    }
    for (V value : this.meta) {
      K key = (K) Reflections.getField(keyProperty, value);
      put(key, value);
    }
  }

  @Override public Iterable<V> metaData() {
    return meta;
  }
}