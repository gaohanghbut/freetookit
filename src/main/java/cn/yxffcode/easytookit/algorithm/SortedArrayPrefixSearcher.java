package cn.yxffcode.easytookit.algorithm;

import java.util.AbstractList;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Collections.emptyList;

/**
 * @author gaohang on 16/5/11.
 */
public class SortedArrayPrefixSearcher implements PrefixSearcher {

  private final String[] data;

  public SortedArrayPrefixSearcher(String[] sortedArray) {
    this.data = sortedArray;
  }

  @Override public Iterable<String> search(String prefix) {
    checkArgument(!isNullOrEmpty(prefix));
    //查找prefix
    final int from = bsearch(0, data.length, prefix);
    if (from >= data.length) {
      return emptyList();
    }
    //取出查询词条的上界
    int l = prefix.length();
    String max = prefix.substring(0, l - 1) + ((char) (prefix.charAt(l - 1) + 1));
    final int to = bsearch(from, data.length, max);
    if (from == to) {
      return emptyList();
    }
    //不要使用ArrayList,避免创建ArrayList时的O(n)时间开销
    return new AbstractList<String>() {
      @Override public String get(int index) {
        return data[from + index];
      }

      @Override public int size() {
        return to - from;
      }
    };
  }

  /**
   * 二分查找
   */
  private int bsearch(int start, int end, String elem) {
    if (start == end) {
      //没找到也返回下标,此时下标大于end的都是比查找元素大的
      return end;
    }
    //取中间数做比较
    int middle = (start + end) / 2;
    int c = data[middle].compareTo(elem);
    if (c == 0) {
      return middle;
    } else if (c > 0) {
      //middle更大,取前一半
      return bsearch(start, middle, elem);
    } else {
      //middle更小,取后一半
      return bsearch(middle + 1, end, elem);
    }
  }

}
