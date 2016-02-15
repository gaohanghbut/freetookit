package cn.yxffcode.easytookit.collection;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Iterators;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static cn.yxffcode.easytookit.utils.StringUtils.equalsIgnoreCase;

/**
 * key为字符串的Map,在对String是否相等做判断时使用的是{@link String#equalsIgnoreCase(String)}.
 * 使用代理实现,优势是实现方式简单,缺点是会带来较多的临时对象(垃圾对象)
 *
 * @author gaohang on 15/12/2.
 */
@Deprecated
public class IgnoreCaseStringMap<V> extends AbstractMap<String, V> implements Serializable, Cloneable {

  private static final long serialVersionUID = -5567524254918911681L;

  private Map<StringHolder, V> delegate;
  private Set<String> keySet;
  private Set<Entry<String, V>> entrySet;

  public IgnoreCaseStringMap() {
    this.delegate = new HashMap<>();
  }

  @Override public int size() {
    return delegate.size();
  }

  @Override public boolean isEmpty() {
    return delegate.isEmpty();
  }

  @Override public boolean containsValue(final Object value) {
    return delegate.containsValue(value);
  }

  @Override public boolean containsKey(final Object key) {
    return delegate.containsKey(new StringHolder((String) key));
  }

  @Override public V get(final Object key) {
    return delegate.get(new StringHolder((String) key));
  }

  public V put(final String key, final V value) {
    return delegate.put(new StringHolder(key), value);
  }

  @Override public V remove(final Object key) {
    return delegate.remove(new StringHolder((String) key));
  }

  public void putAll(final Map<? extends String, ? extends V> m) {
    for (Entry<? extends String, ? extends V> en : m.entrySet()) {
      put(en.getKey(), en.getValue());
    }
  }

  @Override public void clear() {
    delegate.clear();
  }

  @Override public Set<String> keySet() {
    return this.keySet != null ? this.keySet : (this.keySet = new KeySetWrapper(delegate.keySet()));
  }

  @Override public Collection<V> values() {
    return delegate.values();
  }

  @Override public Set<Entry<String, V>> entrySet() {
    return this.entrySet != null ?
        this.entrySet :
        (entrySet = new EntrySetWrapper(delegate.entrySet()));
  }

  private void writeObject(java.io.ObjectOutputStream s) throws IOException {
    s.writeObject(delegate);
  }

  private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
    this.delegate = (Map<StringHolder, V>) s.readObject();
  }

  private static final class StringHolder implements Serializable {
    private final String value;

    private StringHolder(final String value) {
      this.value = value;
    }

    @Override public int hashCode() {
      return Objects.hashCode(value);
    }

    @Override public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      final StringHolder that = (StringHolder) o;
      return equalsIgnoreCase(this.value, that.value);
    }
  }


  private static final class KeySetWrapper extends AbstractSet<String> {
    private final Set<StringHolder> delegate;

    private KeySetWrapper(final Set<StringHolder> delegate) {
      this.delegate = delegate;
    }

    @Override public Iterator<String> iterator() {
      return Iterators.transform(delegate.iterator(), StringHolderStringTransformer.INSTANCE);
    }

    @Override public int size() {
      return delegate.size();
    }

    private enum StringHolderStringTransformer implements Function<StringHolder, String> {
      INSTANCE;

      @Override public String apply(final StringHolder input) {
        return input.value;
      }
    }


  }


  private static final class EntrySetWrapper<V> extends AbstractSet<Entry<String, V>> {

    private final Set<Entry<StringHolder, V>> delegate;

    private EntrySetWrapper(final Set<Entry<StringHolder, V>> delegate) {
      this.delegate = delegate;
    }

    @Override public Iterator<Entry<String, V>> iterator() {
      return Iterators.transform(delegate.iterator(), new EntryTransformer<V>());
    }

    @Override public int size() {
      return delegate.size();
    }

    private static final class TransformEntry<V> implements Map.Entry<String, V> {

      private final Map.Entry<StringHolder, V> delegate;

      private TransformEntry(final Entry<StringHolder, V> delegate) {
        this.delegate = delegate;
      }

      @Override public String getKey() {
        return delegate.getKey().value;
      }

      @Override public V getValue() {
        return delegate.getValue();
      }

      @Override public V setValue(final V value) {
        return delegate.setValue(value);
      }
    }


    private static class EntryTransformer<V> implements Function<Entry<StringHolder, V>, Entry<String, V>> {
      @Override public Entry<String, V> apply(final Entry<StringHolder, V> input) {
        return new TransformEntry<V>(input);
      }
    }
  }
}
