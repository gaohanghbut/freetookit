package cn.yxffcode.easytookit.utils;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author gaohang on 15/12/8.
 */
public abstract class MapUtils {
  private MapUtils() {
  }

  public static boolean safeContainsKey(Map<?, ?> map,
                                        Object key
  ) {
    checkNotNull(map);
    try {
      return map.containsKey(key);
    } catch (ClassCastException e) {
      return false;
    } catch (NullPointerException e) {
      return false;
    }
  }

  public static <V> V safeRemove(Map<?, V> map,
                                 Object key
  ) {
    checkNotNull(map);
    try {
      return map.remove(key);
    } catch (ClassCastException e) {
      return null;
    } catch (NullPointerException e) {
      return null;
    }
  }

  public static <V> V safeGet(Map<?, V> map,
                              Object key
  ) {
    checkNotNull(map);
    try {
      return map.get(key);
    } catch (ClassCastException e) {
      return null;
    } catch (NullPointerException e) {
      return null;
    }
  }

}
