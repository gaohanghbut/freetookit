package cn.yxffcode.freetookit.collection;

import java.util.AbstractList;

/**
 * 元素为固定的一个值的List
 *
 * @author gaohang on 7/3/16.
 */
public class SingleValueList<E> extends AbstractList<E> {
  private final int size;
  private final E value;

  public SingleValueList(int size, E value) {
    this.size = size;
    this.value = value;
  }

  @Override
  public E get(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException(Integer.toString(index));
    }
    return value;
  }

  @Override
  public int size() {
    return size;
  }
}
