package cn.yxffcode.freetookit.collection;

import java.util.Enumeration;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 用于{@link Enumeration}到{@link java.util.Iterator}接口适配，{@link java.util.Collections#list(Enumeration)}方法有
 * 相同的功能，但Collections.list方法有O(n)的时间开销
 *
 * @author gaohang on 15/9/26.
 */
public class IteratorAdapter<T> extends ImmutableIterator<T> {

  private final Enumeration<? extends T> delegate;

  private IteratorAdapter(Enumeration<? extends T> delegate) {
    this.delegate = delegate;
  }

  public static <T> IteratorAdapter<T> create(Enumeration<? extends T> enumeration) {
    return new IteratorAdapter<>(checkNotNull(enumeration));
  }

  @Override public boolean hasNext() {
    return delegate.hasMoreElements();
  }

  @Override public T next() {
    return delegate.nextElement();
  }
}
