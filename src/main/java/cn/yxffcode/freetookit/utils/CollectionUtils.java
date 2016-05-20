package cn.yxffcode.freetookit.utils;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author gaohang on 15/12/8.
 */
public abstract class CollectionUtils {
  private CollectionUtils() {
  }

  public static boolean safeContains(Collection<?> collection, Object object) {
    checkNotNull(collection);
    try {
      return collection.contains(object);
    } catch (ClassCastException e) {
      return false;
    } catch (NullPointerException e) {
      return false;
    }
  }

  public static boolean safeRemove(Collection<?> collection, Object object) {
    checkNotNull(collection);
    try {
      return collection.remove(object);
    } catch (ClassCastException e) {
      return false;
    } catch (NullPointerException e) {
      return false;
    }
  }

  public static <T> boolean isNotEmpty(List<T> list) {
    return list != null || !list.isEmpty();
  }
}
