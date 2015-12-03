/*
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
/**
 * Copyright (C) 2009-2013 Typesafe Inc. <http://www.typesafe.com>
 */
package cn.yxffcode.easytookit.concurrent;

import cn.yxffcode.easytookit.collection.ReadOnlyIterator;
import cn.yxffcode.easytookit.utils.PlatformDependent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * A lock-free concurrent single-consumer multi-producer {@link Queue}.
 * It allows multiple producer threads to perform the following operations simultaneously:
 * <ul>
 * <li>{@link #offer(Object)}, {@link #add(Object)}, {@link #addAll(Collection)}</li>
 * <li>{@link #isEmpty()}</li>
 * </ul>
 * .. while only one consumer thread is allowed to perform the following operations exclusively:
 * <ul>
 * <li>{@link #poll()} and {@link #remove()}</li>
 * <li>{@link #element()}, {@link #peek()}</li>
 * <li>{@link #remove(Object)}, {@link #removeAll(Collection)}, and {@link #retainAll(Collection)}</li>
 * <li>{@link #clear()}</li> {@link #}
 * <li>{@link #iterator()}</li>
 * <li>{@link #toArray()} and {@link #toArray(Object[])}</li>
 * <li>{@link #contains(Object)} and {@link #containsAll(Collection)}</li>
 * <li>{@link #size()}</li>
 * </ul>
 * <p/>
 * <strong>The behavior of this implementation is undefined if you perform the operations for a consumer thread only
 * from multiple threads.</strong>
 * <p/>
 * The initial implementation is based on:
 * <ul>
 * <li><a href="http://netty.io/s/mpsc-1024c">Non-intrusive MPSC node based queue</a> from 1024cores.net</li>
 * <li><a href="http://netty.io/s/mpsc-akka">AbstractNodeQueue</a> from Akka</li>
 * </ul>
 * and adopted padded head node changes from:
 * <ul>
 * <li><a href="http://netty.io/s/mpsc-rxjava">MpscPaddedQueue</a> from RxJava</li>
 * </ul>
 * data structure modified to avoid false sharing between head and tail Ref as per implementation of MpscLinkedQueue
 * on <a href="https://github.com/JCTools/JCTools">JCTools project</a>.
 */
public final class MpscLinkedQueue<E> extends MpscLinkedQueueTailRef<E> implements Queue<E> {

    private static final long serialVersionUID = - 1878402552271506449L;

    long p00, p01, p02, p03, p04, p05, p06, p07;
    long p30, p31, p32, p33, p34, p35, p36, p37;

    // offer() occurs at the tail of the linked list.
    // poll() occurs at the head of the linked list.
    //
    // Resulting layout is:
    //
    //   head --next--> 1st element --next--> 2nd element --next--> ... tail (last element)
    //
    // where the head is a dummy node whose value is null.
    //
    // offer() appends a new node next to the tail using AtomicReference.getAndSet()
    // poll() removes head from the linked list and promotes the 1st element to the head,
    // setting its value to null if possible.
    //
    // Also note that this class extends AtomicReference for the "tail" slot (which is the one that is appended to)
    // since Unsafe does not expose XCHG operation intrinsically.
    public MpscLinkedQueue() {
        MpscLinkedQueueNode<E> tombstone = new DefaultNode<E>(null);
        setHeadRef(tombstone);
        setTailRef(tombstone);
    }

    /**
     * Returns the node right next to the head, which contains the first element of this queue.
     */
    private MpscLinkedQueueNode<E> peekNode() {
        MpscLinkedQueueNode<E> head = headRef();
        MpscLinkedQueueNode<E> next = head.next();
        if (next == null && head != tailRef()) {
            // if tail != head this is not going to change until consumer makes progress
            // we can avoid reading the head and just spin on next until it shows up
            //
            // See https://github.com/akka/akka/pull/15596
            do {
                next = head.next();
            } while (next == null);
        }
        return next;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean offer(E value) {
        if (value == null) {
            throw new NullPointerException("value");
        }

        final MpscLinkedQueueNode<E> newTail;
        if (value instanceof MpscLinkedQueueNode) {
            newTail = (MpscLinkedQueueNode<E>) value;
            newTail.setNext(null);
        } else {
            newTail = new DefaultNode<E>(value);
        }

        MpscLinkedQueueNode<E> oldTail = getAndSetTailRef(newTail);
        oldTail.setNext(newTail);
        return true;
    }

    @Override
    public E poll() {
        final MpscLinkedQueueNode<E> next = peekNode();
        if (next == null) {
            return null;
        }

        // next becomes a new head.
        MpscLinkedQueueNode<E> oldHead = headRef();
        // Similar to 'headRef.node = next', but slightly faster (storestore vs loadstore)
        // See: http://robsjava.blogspot.com/2013/06/a-faster-volatile.html
        // See: http://psy-lob-saw.blogspot.com/2012/12/atomiclazyset-is-performance-win-for.html
        lazySetHeadRef(next);

        // Break the linkage between the old head and the new head.
        oldHead.unlink();

        return next.clearMaybe();
    }

    @Override
    public E peek() {
        final MpscLinkedQueueNode<E> next = peekNode();
        if (next == null) {
            return null;
        }
        return next.value();
    }

    @Override
    public int size() {
        int                    count = 0;
        MpscLinkedQueueNode<E> n     = peekNode();
        for (; ; ) {
            // If value == null it means that clearMaybe() was called on the MpscLinkedQueueNode.
            if (n == null || n.value() == null) {
                break;
            }
            MpscLinkedQueueNode<E> next = n.next();
            if (n == next) {
                break;
            }
            n = next;
            if (++ count == Integer.MAX_VALUE) {
                // Guard against overflow of integer.
                break;
            }
        }
        return count;
    }

    @Override
    public boolean isEmpty() {
        return headRef() == tailRef();
    }

    @Override
    public boolean contains(Object o) {
        MpscLinkedQueueNode<E> n = peekNode();
        for (; ; ) {
            if (n == null) {
                break;
            }
            E value = n.value();
            // If value == null it means that clearMaybe() was called on the MpscLinkedQueueNode.
            if (value == null) {
                return false;
            }
            if (value == o) {
                return true;
            }
            MpscLinkedQueueNode<E> next = n.next();
            if (n == next) {
                break;
            }
            n = next;
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new ReadOnlyIterator<E>(toList().iterator());
    }

    @Override
    public boolean add(E e) {
        if (offer(e)) {
            return true;
        }
        throw new IllegalStateException("queue full");
    }

    @Override
    public E remove() {
        E e = poll();
        if (e != null) {
            return e;
        }
        throw new NoSuchElementException();
    }

    @Override
    public E element() {
        E e = peek();
        if (e != null) {
            return e;
        }
        throw new NoSuchElementException();
    }

    private List<E> toList(int initialCapacity) {
        return toList(new ArrayList<E>(initialCapacity));
    }

    private List<E> toList() {
        return toList(new ArrayList<E>());
    }

    private List<E> toList(List<E> elements) {
        MpscLinkedQueueNode<E> n = peekNode();
        for (; ; ) {
            if (n == null) {
                break;
            }
            E value = n.value();
            if (value == null) {
                break;
            }
            if (! elements.add(value)) {
                // Seems like there is no space left, break here.
                break;
            }
            MpscLinkedQueueNode<E> next = n.next();
            if (n == next) {
                break;
            }
            n = next;
        }
        return elements;
    }

    @Override
    public Object[] toArray() {
        return toList().toArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        return toList(a.length).toArray(a);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (! contains(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null) {
            throw new NullPointerException("c");
        }
        if (c == this) {
            throw new IllegalArgumentException("c == this");
        }

        boolean modified = false;
        for (E e : c) {
            add(e);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        while (poll() != null) {
            continue;
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        for (E e : this) {
            out.writeObject(e);
        }
        out.writeObject(null);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        final MpscLinkedQueueNode<E> tombstone = new DefaultNode<E>(null);
        setHeadRef(tombstone);
        setTailRef(tombstone);

        for (; ; ) {
            @SuppressWarnings("unchecked")
            E e = (E) in.readObject();
            if (e == null) {
                break;
            }
            add(e);
        }
    }

    private static final class DefaultNode<T> extends MpscLinkedQueueNode<T> {

        private T value;

        DefaultNode(T value) {
            this.value = value;
        }

        @Override
        public T value() {
            return value;
        }

        @Override
        protected T clearMaybe() {
            T value = this.value;
            this.value = null;
            return value;
        }
    }
}
abstract class MpscLinkedQueueHeadRef<E> extends MpscLinkedQueuePad0<E> implements Serializable {

    private static final long serialVersionUID = 8467054865577874285L;

    @SuppressWarnings("rawtypes")
    private static final AtomicReferenceFieldUpdater<MpscLinkedQueueHeadRef, MpscLinkedQueueNode> UPDATER;

    static {
        @SuppressWarnings("rawtypes")
        AtomicReferenceFieldUpdater<MpscLinkedQueueHeadRef, MpscLinkedQueueNode> updater;
        updater = PlatformDependent.newAtomicReferenceFieldUpdater(MpscLinkedQueueHeadRef.class, "headRef");
        if (updater == null) {
            updater = AtomicReferenceFieldUpdater.newUpdater(
                    MpscLinkedQueueHeadRef.class, MpscLinkedQueueNode.class, "headRef");
        }
        UPDATER = updater;
    }

    private transient volatile MpscLinkedQueueNode<E> headRef;

    protected final MpscLinkedQueueNode<E> headRef() {
        return headRef;
    }

    protected final void setHeadRef(MpscLinkedQueueNode<E> headRef) {
        this.headRef = headRef;
    }

    protected final void lazySetHeadRef(MpscLinkedQueueNode<E> headRef) {
        UPDATER.lazySet(this, headRef);
    }
}

abstract class MpscLinkedQueueNode<T> {

    @SuppressWarnings("rawtypes")
    private static final AtomicReferenceFieldUpdater<MpscLinkedQueueNode, MpscLinkedQueueNode> nextUpdater;

    static {
        @SuppressWarnings("rawtypes")
        AtomicReferenceFieldUpdater<MpscLinkedQueueNode, MpscLinkedQueueNode> u;

        u = PlatformDependent.newAtomicReferenceFieldUpdater(MpscLinkedQueueNode.class, "next");
        if (u == null) {
            u = AtomicReferenceFieldUpdater.newUpdater(MpscLinkedQueueNode.class, MpscLinkedQueueNode.class, "next");
        }
        nextUpdater = u;
    }

    @SuppressWarnings("unused")
    private volatile MpscLinkedQueueNode<T> next;

    final MpscLinkedQueueNode<T> next() {
        return next;
    }

    final void setNext(final MpscLinkedQueueNode<T> newNext) {
        // Similar to 'next = newNext', but slightly faster (storestore vs loadstore)
        // See: http://robsjava.blogspot.com/2013/06/a-faster-volatile.html
        nextUpdater.lazySet(this, newNext);
    }

    public abstract T value();

    /**
     * Sets the element this node contains to {@code null} so that the node can be used as a tombstone.
     */
    protected T clearMaybe() {
        return value();
    }

    /**
     * Unlink to allow GC'ed
     */
    void unlink() {
        setNext(null);
    }
}

abstract class MpscLinkedQueuePad0<E> {
    long p00, p01, p02, p03, p04, p05, p06, p07;
    long p30, p31, p32, p33, p34, p35, p36, p37;
}
abstract class MpscLinkedQueuePad1<E> extends MpscLinkedQueueHeadRef<E> {

    private static final long serialVersionUID = 2886694927079691637L;

    long p00, p01, p02, p03, p04, p05, p06, p07;
    long p30, p31, p32, p33, p34, p35, p36, p37;
}
abstract class MpscLinkedQueueTailRef<E> extends MpscLinkedQueuePad1<E> {

    private static final long serialVersionUID = 8717072462993327429L;

    @SuppressWarnings("rawtypes")
    private static final AtomicReferenceFieldUpdater<MpscLinkedQueueTailRef, MpscLinkedQueueNode> UPDATER;

    static {
        @SuppressWarnings("rawtypes")
        AtomicReferenceFieldUpdater<MpscLinkedQueueTailRef, MpscLinkedQueueNode> updater;
        updater = PlatformDependent.newAtomicReferenceFieldUpdater(MpscLinkedQueueTailRef.class, "tailRef");
        if (updater == null) {
            updater = AtomicReferenceFieldUpdater.newUpdater(
                    MpscLinkedQueueTailRef.class, MpscLinkedQueueNode.class, "tailRef");
        }
        UPDATER = updater;
    }

    private transient volatile MpscLinkedQueueNode<E> tailRef;

    protected final MpscLinkedQueueNode<E> tailRef() {
        return tailRef;
    }

    protected final void setTailRef(MpscLinkedQueueNode<E> tailRef) {
        this.tailRef = tailRef;
    }

    @SuppressWarnings("unchecked")
    protected final MpscLinkedQueueNode<E> getAndSetTailRef(MpscLinkedQueueNode<E> tailRef) {
        // LOCK XCHG in JDK8, a CAS loop in JDK 7/6
        return (MpscLinkedQueueNode<E>) UPDATER.getAndSet(this, tailRef);
    }
}
