package cn.yxffcode.freetookit.collection;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 针对单线程消费,多线程生产的队列.
 * <p>
 * 因为针对的是单线程消费,所以在{@link #poll()}, {@link #peek()}和{@link #iterator()}方法
 * 都只能在消费线程中运行,它们没有处理多线程消费的冲突
 *
 * @author gaohang on 16/7/29.
 */
public class MpscLinkedQueue<E> extends AbstractQueue<E> {

  private Node<E> head = new Node<>(null, null);
  private volatile Node<E> tail = head;
  private int size = 0;

  private final AtomicReferenceFieldUpdater<MpscLinkedQueue, Node> tailUpdater =
          AtomicReferenceFieldUpdater.newUpdater(MpscLinkedQueue.class, Node.class, "tail");

  @Override public Iterator<E> iterator() {
    //因为针对的是单线程消费,使用带头节点的链表,消费端不会有线程冲突
    return new ImmutableIterator<E>() {
      private Node<E> node = head;

      @Override public boolean hasNext() {
        return node.next != null;
      }

      @Override public E next() {
        node = node.next;
        return node.elem;
      }
    };
  }

  @Override public int size() {
    return size;
  }

  @Override public boolean offer(final E e) {
    checkNotNull(e);
    Node<E> nextTail = new Node<>(e, null);
    Node<E> oldTail = this.tail;
    while (true) {
      if (tailUpdater.compareAndSet(this, oldTail, nextTail)) {
        ++size;
        oldTail.next = nextTail;
        return true;
      }
    }
  }

  @Override public E poll() {
    if (head.next == null) {
      return null;
    }
    //因为针对的是单线程消费,使用带头节点的链表,消费端不会有线程冲突
    head = head.next;
    --size;
    return head.elem;
  }

  @Override public E peek() {
    if (head.next == null) {
      return null;
    }
    return head.elem;
  }

  private static final class Node<E> {
    private E elem;
    private Node<E> next;

    public Node(final E elem, final Node<E> next) {
      this.elem = elem;
      this.next = next;
    }
  }
}
