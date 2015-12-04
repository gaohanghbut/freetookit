package cn.yxffcode.easytookit.collection;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;

/**
 * 只包含一个元素的集合
 *
 * @author gaohang on 15/9/24.
 */
public class SingleCollection<E> extends AbstractList<E> implements Set<E>, Serializable {

    public static <E> Set<E> newSingleSet(E elem) {
        return new SingleCollection<>(elem);
    }

    public static <E> List<E> newSingleList(E elem) {
        return new SingleCollection<>(elem);
    }

    public static <E> Collection<E> newSingleCollection(E elem) {
        return new SingleCollection<>(elem);
    }

    private final E elem;

    private SingleCollection(E elem) {
        this.elem = elem;
    }

    @Override
    public E get(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }
        return elem;
    }

    @Override
    public Spliterator<E> spliterator() {
        return super.spliterator();
    }

    @Override
    public int size() {
        return 1;
    }
}
