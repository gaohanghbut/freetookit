package cn.yxffcode.freetookit.algorithm;

import java.util.TreeSet;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author gaohang on 16/5/11.
 */
public class TreePrefixSearcher implements PrefixSearcher {

  private final TreeSet<String> data;

  public TreePrefixSearcher(TreeSet<String> data) {
    this.data = data;
  }

  @Override public Iterable<String> search(String prefix) {
    checkArgument(!isNullOrEmpty(prefix));
    //取出查询词条的上界
    int l = prefix.length();
    String max = prefix.substring(0, l - 1) + ((char) (prefix.charAt(l - 1) + 1));
    return data.subSet(prefix, max);
  }
}
