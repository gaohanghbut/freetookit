package cn.yxffcode.freetookit.collection;

/**
 * @author gaohang on 7/3/16.
 */
public class SingleValueIterator<E> extends ImmutableIterator<E> {
  private final int size;
  private final E elem;
  private int cur;

  public SingleValueIterator(int size, E elem) {
    this.size = size;
    this.elem = elem;
  }

  @Override
  public boolean hasNext() {
    return cur < size;
  }

  @Override
  public E next() {
    return elem;
  }
}
