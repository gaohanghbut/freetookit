package cn.yxffcode.easytookit.lang;

import cn.yxffcode.easytookit.collection.ImmutableIterator;

import java.util.Iterator;

/**
 * @author gaohang on 16/5/20.
 */
public abstract class AbstractIterable<T> implements Iterable<T> {
  @Override public Iterator<T> iterator() {
    return new ImmutableIterator<T>() {
      @Override public boolean hasNext() {
        return AbstractIterable.this.hasNext();
      }

      @Override public T next() {
        return AbstractIterable.this.next();
      }
    };
  }

  protected abstract boolean hasNext();

  protected abstract T next();
}
