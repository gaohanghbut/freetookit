package cn.yxffcode.freetookit.collection;

import cn.yxffcode.freetookit.utils.Reflections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 通过对象创建map,需要指定key和value的属性名,Map不可改
 *
 * @author gaohang on 7/16/16.
 */
public class HashBuildingMap<K, V> extends HashMap<K, V> {
  protected HashBuildingMap(Iterable<?> srcObjects, String keyProperty, String valueProperty) {
    checkNotNull(srcObjects);
    for (Object object : srcObjects) {
      K key = (K) Reflections.getField(keyProperty, object);
      V value = (V) Reflections.getField(valueProperty, object);
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
  public V put(K key, V value) {
    throw new UnsupportedOperationException("put is not supported by this map");
  }

}
