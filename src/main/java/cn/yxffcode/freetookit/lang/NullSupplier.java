package cn.yxffcode.freetookit.lang;

import com.google.common.base.Supplier;

/**
 * @author gaohang on 8/14/16.
 */
public class NullSupplier<T> implements Supplier<T> {

  public static <T>NullSupplier<T> getInstance() {
    return (NullSupplier<T>) Holder.INSTANCE;
  }

  @Override
  public T get() {
    return null;
  }

  private static final class Holder {
    private static final NullSupplier<?> INSTANCE = new NullSupplier<>();
  }

  private NullSupplier() {
  }
}
