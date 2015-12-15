package cn.yxffcode.easytookit.lang;

import java.util.Comparator;

/**
 * 作为{@link ImprovedComparator}接口到{@link Comparator}接口的适配器
 *
 * @author gaohang on 15/11/30.
 */
public class ComparatorAdapter<T> implements Comparator<T> {

  private final ImprovedComparator<? super T> delegate;

  public ComparatorAdapter(final ImprovedComparator<? super T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public int compare(final T left,
                     final T right
                    ) {
    return delegate.compare(left, right)
                   .getComparatorResult();
  }
}
