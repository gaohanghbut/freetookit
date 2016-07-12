package cn.yxffcode.freetookit.collection;

import cn.yxffcode.freetookit.utils.Reflections;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 直接通过Iterable对象实现,每次get的时候都需要遍历Iterable,适用于元素数量非常小的场景,没有提供去重的功能
 *
 * @author gaohang on 7/9/16.
 */
public class DirectIterTransMap<K, V> extends AbstractMap<K, V> implements IterTransMap<K, V> {

  public static <K, V> DirectIterTransMap<K, V> immutableCopyOf(
          Iterable<V> meta, String keyProperty) {
    return new DirectIterTransMap(meta, keyProperty, true);
  }

  public static <K, V> DirectIterTransMap<K, V> newInstance(
          Iterable<V> meta, String keyProperty) {
    return new DirectIterTransMap<>(meta, keyProperty, false);
  }

  private final Iterable<V> meta;
  private int size = -1;
  private Function<V, Entry<K, V>> entryFunction;

  private DirectIterTransMap(Iterable<V> meta, String keyProperty, boolean immutableCopy) {
    checkNotNull(meta);
    checkArgument(!Strings.isNullOrEmpty(keyProperty));
    final String keyProperty1 = keyProperty;
    this.entryFunction = new Function<V, Entry<K, V>>() {

      @Override
      public Entry<K, V> apply(V value) {
        return new Entry<K, V>() {
          @Override
          public K getKey() {
            return (K) Reflections.getField(keyProperty1, value);
          }

          @Override
          public V getValue() {
            return value;
          }

          @Override
          public V setValue(V value) {
            throw new UnsupportedOperationException();
          }
        };
      }
    };
    if (immutableCopy) {
      this.meta = ImmutableList.copyOf(meta);
    } else {
      this.meta = meta;
    }
    if (this.meta instanceof Collection) {
      this.size = ((Collection) this.meta).size();
    }
  }

  @Override
  public Iterable<V> metaData() {
    return meta;
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return new AbstractSet<Entry<K, V>>() {
      @Override
      public Iterator<Entry<K, V>> iterator() {
        return Iterators.transform(meta.iterator(), entryFunction);
      }

      @Override
      public int size() {
        if (size < 0) {
          for (V _ : meta) {
            size++;
          }
        }
        return size;
      }
    };
  }

}
