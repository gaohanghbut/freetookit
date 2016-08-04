package cn.yxffcode.freetookit.collection;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

/**
 * 表示多个{@link Collection}的一个视图，不能调用{@link GroupCollection#add(Object)}等方法对Collection做修改
 *
 * @author gaohang on 15/8/21.
 */
public class GroupCollection<E> extends AbstractCollection<E> {

  private Collection<? extends Collection<E>> collections;
  private int size;

  private GroupCollection(Collection<? extends Collection<E>> collections) {
    this.collections = collections;
    for (Collection<? extends E> collection : collections) {
      size += collection.size();
    }
  }

  public static <T> GroupCollection<T> create(Collection<? extends Collection<T>> collections) {
    return new GroupCollection<>(collections);
  }

  @Override
  public Iterator<E> iterator() {
    return (Iterator<E>) GroupIterable.create(collections).iterator();
  }

  @Override
  public int size() {
    return size;
  }
}
