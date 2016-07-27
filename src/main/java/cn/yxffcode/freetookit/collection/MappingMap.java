package cn.yxffcode.freetookit.collection;

import com.google.common.base.Throwables;

import java.lang.reflect.Field;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 用于为对象提供基于Map的访问方式.
 * <p>
 * 此类的{@link #entrySet()}方法的开销会略大,因为此类的作用是spring-jdbc用来获取sql中的参数,重要的是Map.get方法的调用,
 * 为了降低复杂度,只是简单的实现了entrySet方法
 *
 * @author gaohang on 7/23/16.
 */
public class MappingMap extends AbstractMap<String, Object> {

  public static MappingMap fromNotNull(Object obj) {
    checkNotNull(obj);
    return new MappingMap(obj);
  }

  private int size;
  private final Object backObject;
  private KeySet keySet;
  private ValueCollection values;

  private MappingMap(Object obj) {
    Class<?> type = obj.getClass();
    while (type != Object.class) {
      Field[] fields = type.getDeclaredFields();
      size += fields.length;
      type = type.getSuperclass();
    }
    this.backObject = obj;
  }

  @Override
  public Object get(Object key) {
    checkNotNull(key);
    checkArgument(key instanceof String);
    try {
      Field field = backObject.getClass().getDeclaredField((String) key);
      return getFieldValue(field);
    } catch (NoSuchFieldException ignore) {//eturn null
      return null;
    } catch (IllegalAccessException e) {
      Throwables.propagate(e);
      return null;//never happened
    }
  }

  private Object getFieldValue(Field field) throws IllegalAccessException {
    field.setAccessible(true);
    return field.get(backObject);
  }

  @Override
  public Set<String> keySet() {
    if (keySet == null) {
      keySet = new KeySet();
    }
    return keySet;
  }

  @Override
  public Collection<Object> values() {
    if (values == null) {
      values = new ValueCollection();
    }
    return values;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public Set<Entry<String, Object>> entrySet() {
    return new AbstractSet<Entry<String, Object>>() {
      @Override
      public Iterator<Entry<String, Object>> iterator() {
        return new Iterator<Entry<String, Object>>() {
          private Iterator<Field> iterator;
          private Class<?> type;

          @Override
          public boolean hasNext() {
            if (type == null) {
              type = backObject.getClass();
            }
            if (iterator == null) {
              iterator = Arrays.asList(type.getDeclaredFields()).iterator();
            }
            if (iterator.hasNext()) {
              return true;
            }
            if (type == Object.class) {
              return false;
            }

            type = type.getSuperclass();
            iterator = null;

            return hasNext();
          }

          @Override
          public Entry<String, Object> next() {
            final Field field = iterator.next();
            return new Entry<String, Object>() {
              @Override
              public String getKey() {
                return field.getName();
              }

              @Override
              public Object getValue() {
                try {
                  return getFieldValue(field);
                } catch (IllegalAccessException e) {
                  Throwables.propagate(e);
                  return null;
                }
              }

              @Override
              public Object setValue(Object value) {
                throw new UnsupportedOperationException();
              }

              @Override
              public boolean equals(Object o) {
                throw new UnsupportedOperationException();
              }
            };
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }

      @Override
      public int size() {
        return size;
      }
    };
  }

  private class KeySet extends AbstractSet<String> {
    @Override
    public Iterator<String> iterator() {
      return new Iterator<String>() {
        private Iterator<Field> iterator;
        private Class<?> type;

        @Override
        public boolean hasNext() {
          if (type == null) {
            type = backObject.getClass();
          }
          if (iterator == null) {
            iterator = Arrays.asList(type.getDeclaredFields()).iterator();
          }
          if (iterator.hasNext()) {
            return true;
          }
          if (type == Object.class) {
            return false;
          }

          type = type.getSuperclass();
          iterator = null;

          return hasNext();
        }

        @Override
        public String next() {
          return iterator.next().getName();
        }

        @Override
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    }

    @Override
    public int size() {
      return size;
    }
  }

  private class ValueCollection extends AbstractCollection<Object> {
    @Override
    public Iterator<Object> iterator() {
      return new Iterator<Object>() {
        private Iterator<Field> iterator;
        private Class<?> type;

        @Override
        public boolean hasNext() {
          if (type == null) {
            type = backObject.getClass();
          }
          if (iterator == null) {
            iterator = Arrays.asList(type.getDeclaredFields()).iterator();
          }
          if (iterator.hasNext()) {
            return true;
          }
          if (type == Object.class) {
            return false;
          }

          type = type.getSuperclass();
          iterator = null;

          return hasNext();
        }

        @Override
        public Object next() {
          try {
            return getFieldValue(iterator.next());
          } catch (IllegalAccessException e) {
            Throwables.propagate(e);
            return null;//never happened
          }
        }

        @Override
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    }

    @Override
    public int size() {
      return size;
    }
  }
}
