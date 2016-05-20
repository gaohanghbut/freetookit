package cn.yxffcode.freetookit.collection;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author gaohang on 15/9/29.
 */
public final class ListUtils {
  private ListUtils() {
  }

  public static <E> List<? extends E> subList(List<? extends E> src, int offset, int length) {
    checkNotNull(src);
    if ((offset == 0 && src.size() <= length) || src.size() == 0) {
      return src;
    }

    //需要检查length和offset的值,防止IndexOutOfBoundsException异常
    if (offset >= src.size()) {
      return Collections.emptyList();
    }
    if (length + offset >= src.size()) {
      return src.subList(offset, src.size());
    }
    return src.subList(offset, length + offset);
  }
}
