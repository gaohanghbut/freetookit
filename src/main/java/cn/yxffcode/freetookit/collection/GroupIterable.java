package cn.yxffcode.freetookit.collection;

import java.util.Iterator;

/**
 * 将多个Iterable对象组合成一个Iterable对象
 *
 * @author gaohang on 15/9/21.
 */
public class GroupIterable<E> implements Iterable<E> {

  private final Iterable<? extends Iterable<? extends E>> src;

  protected GroupIterable(Iterable<? extends Iterable<? extends E>> src) {
    this.src = src;
  }

  public static <E> GroupIterable<E> create(Iterable<? extends Iterable<? extends E>> src) {
    return new GroupIterable<>(src);
  }

  @Override public Iterator<E> iterator() {
    return new InnerIterator();
  }

  class InnerIterator extends ImmutableIterator<E> {

    private Iterator<? extends Iterable<? extends E>> iterator;
    private Iterator<? extends E> cur;

    @Override public boolean hasNext() {
      if (cur != null && cur.hasNext()) {
        return true;
      }
      if (iterator == null) {
        this.iterator = src.iterator();
      }
      while (true) {
        if (iterator.hasNext()) {
          cur = iterator.next().iterator();
        } else {
          return false;
        }
        if (cur.hasNext()) {
          return true;
        }
      }
    }

    @Override public E next() {
      return cur.next();
    }

    Iterator<? extends E> current() {
      return cur;
    }
  }
}
