package cn.yxffcode.easytookit.collection;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author gaohang on 15/8/5.
 */
public final class IterableUtils {
  private IterableUtils() {
  }

  public static <T> Iterable<T> adapt(final Enumeration<T> enumeration) {
    checkNotNull(enumeration);
    return new Iterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return IteratorAdapter.create(enumeration);
      }
    };
  }

  public static <K, V> Map<K, V> nullToEmpty(Map<K, V> src) {
    return src == null ? Collections.<K, V>emptyMap() : src;
  }

  /**
   * 将一个{@link Iterable}对象转换成n个大小为subSize的集合
   */
  public static <T> List<List<T>> split(Iterable<? extends T> src, int subSize) {
    return split(src, subSize, new ArrayListSupplier<T>());
  }

  /**
   * 将一个{@link Iterable}对象转换成n个大小为subSize的集合
   */
  public static <T, C extends Collection<T>> List<C> split(Iterable<? extends T> src, int subSize,
                                                           Supplier<C> supplier) {

    checkNotNull(src);
    checkArgument(subSize > 0);
    checkNotNull(supplier);

    int count = 0;
    List<C> result = Lists.newArrayList();
    C list = supplier.get();
    for (T t : src) {
      list.add(t);
      if (++count == subSize) {
        result.add(list);
        list = supplier.get();
        count = 0;
      }
    }
    if (count != 0) {
      result.add(list);
    }
    return result;
  }

  private static class ArrayListSupplier<T> implements Supplier<List<T>> {
    @Override
    public List<T> get() {
      return Lists.newArrayList();
    }
  }
}
