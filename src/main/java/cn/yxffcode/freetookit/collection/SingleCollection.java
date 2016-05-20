package cn.yxffcode.freetookit.collection;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 只包含一个元素的集合
 * <p>
 * FIXME:同时实现List和Set接口也许不好
 *
 * @author gaohang on 15/9/24.
 */
public class SingleCollection<E> extends AbstractList<E> implements Set<E>, Serializable {

  private final E elem;

  private SingleCollection(E elem) {
    this.elem = elem;
  }

  public static <E> Set<E> newSingleSet(E elem) {
    return new SingleCollection<>(elem);
  }

  public static <E> List<E> newSingleList(E elem) {
    return new SingleCollection<>(elem);
  }

  public static <E> Collection<E> newSingleCollection(E elem) {
    return new SingleCollection<>(elem);
  }

  @Override public E get(int index) {
    if (index != 0) {
      throw new IndexOutOfBoundsException();
    }
    return elem;
  }

  @Override public int size() {
    return 1;
  }
}
