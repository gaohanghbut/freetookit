package cn.yxffcode.freetookit.collection;

import cn.yxffcode.freetookit.lang.AbstractIterable;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author gaohang on 8/4/16.
 */
public class ListDiff<T> {

  public static <T> ListDiff<T> fromNotNull(List<T> original) {
    checkNotNull(original);
    return new ListDiff<>(original);
  }

  private final List<T> original;

  private ListDiff(List<T> original) {
    this.original = original;
  }

  public Iterable<T> notIn(final List<T> elems) {
    checkNotNull(elems);
    if (elems.isEmpty()) {
      return Lists.newArrayList(original);
    }
    if (elems.size() >= original.size()) {
      return Collections.emptyList();
    }
    return notIn(Sets.newHashSet(elems));
  }

  public Iterable<T> notIn(final Set<T> elems) {
    checkNotNull(elems);
    if (elems.isEmpty()) {
      return Lists.newArrayList(original);
    }
    if (elems.size() >= original.size()) {
      return Collections.emptyList();
    }
    return Iterables.filter(original, new Predicate<T>() {
      @Override
      public boolean apply(T elem) {
        return !elems.contains(elem);
      }
    });
  }
}
