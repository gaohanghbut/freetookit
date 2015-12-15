package cn.yxffcode.easytookit.participle;

import cn.yxffcode.easytookit.collection.ImmutableIterator;
import cn.yxffcode.easytookit.dic.Dictionary;
import cn.yxffcode.easytookit.lang.IntArrayStringBuilder;
import cn.yxffcode.easytookit.lang.StringIntsRef;

import java.util.Iterator;

/**
 * 基于词典的最短匹配过虑出关键词
 *
 * @author gaohang on 15/12/12.
 */
public class DictionaryTokenFilter implements WordTokenFilter {

  private final Dictionary dictionary;

  public DictionaryTokenFilter(final Dictionary dictionary) {
    this.dictionary = dictionary;
  }

  @Override
  public Iterator<String> token(final String sentence) {
    return new ImmutableIterator<String>() {

      private IntArrayStringBuilder appender;
      private StringIntsRef intsRef;
      private int cur;

      @Override
      public boolean hasNext() {
        if (appender == null) {
          appender = new IntArrayStringBuilder();
          intsRef = new StringIntsRef(sentence);
        } else {
          appender.clear();
        }

        int len   = intsRef.length();
        int state = dictionary.startState();
        while (cur < len) {
          int c    = intsRef.element(cur++);
          int next = dictionary.nextState(state, c);
          if (next == - 1) {
            appender.clear();
            state = dictionary.startState();
            continue;
          }
          appender.append(c);
          if (dictionary.isWordEnded(next)) {
            return true;
          }
          state = next;
        }
        return false;
      }

      @Override
      public String next() {
        return appender.toString();
      }
    };
  }
}
