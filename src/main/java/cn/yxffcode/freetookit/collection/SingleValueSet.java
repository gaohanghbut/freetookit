package cn.yxffcode.freetookit.collection;

import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.Iterator;

/**
 * 元素为固定的一个值的Set
 *
 * @author gaohang on 7/3/16.
 */
public class SingleValueSet<E> extends AbstractSet<E> {
  private final int size;
  private final E value;

  public SingleValueSet(int size, E value) {
    this.size = size;
    this.value = value;
  }

  @Override
  public Iterator<E> iterator() {
    return new SingleValueIterator<>(size, value);
  }

  @Override
  public int size() {
    return size;
  }
}
