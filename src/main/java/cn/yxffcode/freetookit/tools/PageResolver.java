package cn.yxffcode.freetookit.tools;

import cn.yxffcode.freetookit.collection.GroupIterable;
import cn.yxffcode.freetookit.collection.ImmutableIterator;
import cn.yxffcode.freetookit.utils.CollectionUtils;

import java.util.Iterator;
import java.util.List;

/**
 * 用于分页查询或者类似场景
 *
 * @author gaohang on 16/1/13.
 */
public abstract class PageResolver<T> {
  private final int pageSize;

  protected PageResolver(int pageSize) {
    this.pageSize = pageSize;
  }

  public Iterable<T> getAll() {
    return GroupIterable.create(new Iterable<Iterable<T>>() {
      @Override public Iterator<Iterable<T>> iterator() {
        return new ImmutableIterator<Iterable<T>>() {
          private int off = 0;
          private List<T> pageData;
          private boolean doNext = true;

          @Override public boolean hasNext() {
            if (!doNext) {
              return false;
            }
            pageData = nextPage(off);
            if (CollectionUtils.isNotEmpty(pageData)) {
              off += pageData.size();
              if (pageData.size() < pageSize) {
                doNext = false;
              }
              return true;
            }
            return false;
          }

          @Override public Iterable<T> next() {
            return pageData;
          }
        };
      }
    });
  }

  protected abstract List<T> nextPage(int off);
}
