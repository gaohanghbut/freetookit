package cn.yxffcode.freetookit.algorithm;

import java.util.Iterator;

/**
 * @author gaohang on 7/24/16.
 */
public interface WordTokenFilter {
  Iterator<String> getMatched(String sentence);

  boolean match(String source);
}
