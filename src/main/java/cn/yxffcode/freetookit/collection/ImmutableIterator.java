package cn.yxffcode.freetookit.collection;

import java.util.Iterator;

/**
 * 不可修改的Iterator，不支持remove方法
 *
 * @author gaohang on 15/9/24.
 */
public abstract class ImmutableIterator<E> implements Iterator<E> {
  @Override public void remove() {
    throw new UnsupportedOperationException();
  }
}
