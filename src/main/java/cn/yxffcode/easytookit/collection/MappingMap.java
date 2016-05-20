package cn.yxffcode.easytookit.collection;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 将对象的属性作为key,属性值作为value
 *
 * @author gaohang on 16/4/15.
 */
public class MappingMap extends AbstractMap<String, Object> {

  private static final KeyResolver FIELD_NAME_KEY = new KeyResolver() {
    @Override public String getKey(Field field) {
      return field.getName();
    }
  };
  private final Map<String, Object> table;

  private MappingMap(Object bean, KeyResolver keyResolver) {
    Map<String, Object> table = new HashMap<>();
    Class<?> type = bean.getClass();
    while (type != Object.class) {
      Field[] fields = type.getDeclaredFields();
      for (Field field : fields) {
        field.setAccessible(true);
        try {
          String key = keyResolver.getKey(field);
          if (Strings.isNullOrEmpty(key)) {
            continue;
          }
          table.put(key, field.get(bean));
        } catch (IllegalAccessException e) {
          Throwables.propagate(e);
        }
      }
      type = type.getSuperclass();
    }
    this.table = Collections.unmodifiableMap(table);
  }

  public static MappingMap create(Object bean, KeyResolver keyResolver) {
    return new MappingMap(bean, keyResolver);
  }

  public static MappingMap create(Object bean) {
    return create(bean, FIELD_NAME_KEY);
  }

  @Override public Set<Entry<String, Object>> entrySet() {
    return table.entrySet();
  }

  public interface KeyResolver {
    String getKey(Field field);
  }
}
