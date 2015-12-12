package cn.yxffcode.easytookit.lang;

import com.google.common.collect.Maps;

import java.util.Comparator;
import java.util.Map;

/**
 * 基于索引顺序的比较器
 *
 * @author gaohang on 15/11/30.
 */
public abstract class IndexBasedComparator<ID, T> implements Comparator<T> {

    private final Map<ID, Integer> indexes;

    private final boolean reverse;

    public IndexBasedComparator(final Iterable<? extends ID> src) {
        this(src, false);
    }

    public IndexBasedComparator(final Iterable<? extends ID> src, final boolean reverse) {
        this.reverse = reverse;
        indexes = Maps.newHashMap();
        int index = 0;
        for (ID id : src) {
            indexes.put(id, index++);
        }
    }


    @Override
    public int compare(final T left, final T right) {
        if (! reverse) {
            return indexes.get(getId(left))
                          .compareTo(indexes.get(getId(right)));
        } else {
            return indexes.get(getId(right))
                          .compareTo(indexes.get(getId(left)));
        }
    }

    protected abstract ID getId(T elem);

}
