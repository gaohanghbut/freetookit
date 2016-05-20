package cn.yxffcode.freetookit.collection;

import java.util.AbstractQueue;
import java.util.Iterator;

/**
 * @author gaohang on 16/4/22.
 */
public class BinomialQueue<E> extends AbstractQueue<E> {

  private Node<E> root;

  @Override public Iterator<E> iterator() {
    return null;
  }

  @Override public int size() {
    return 0;
  }

  @Override public boolean offer(E e) {
    return false;
  }

  @Override public E poll() {
    return null;
  }

  @Override public E peek() {
    return null;
  }

  private static class Node<E> {
    private E value;
    private Node<E> next;
    private Node<E> child;
  }
}
