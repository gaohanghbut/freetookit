package cn.yxffcode.easytookit.algorithm;

import cn.yxffcode.easytookit.automaton.ACAutomaton;
import cn.yxffcode.easytookit.collection.ImmutableIterator;
import cn.yxffcode.easytookit.dic.Dictionary;
import cn.yxffcode.easytookit.lang.IntArrayStringBuilder;
import cn.yxffcode.easytookit.lang.IntSequence;
import cn.yxffcode.easytookit.lang.StringIntSequence;

import java.util.Iterator;

/**
 * 基于词典的最短匹配过虑出关键词
 *
 * @author gaohang on 15/12/12.
 */
public class ACAutomatonTokenFilter implements WordTokenFilter {

  private static final int NO_SUCH_STATE = -1;
  private final Dictionary dictionary;
  private ACAutomaton acAutomaton;

  public ACAutomatonTokenFilter(final Dictionary dictionary) {
    this.dictionary = dictionary;
  }

  @Override public Iterator<String> getMatched(final String sentence) {
    return new ImmutableIterator<String>() {

      private IntArrayStringBuilder appender;
      private IntSequence intsRef;
      private int cur;

      @Override public boolean hasNext() {
        if (acAutomaton == null) {
          acAutomaton = dictionary.toAcAutomaton();
        }
        if (appender == null) {
          appender = new IntArrayStringBuilder();
          intsRef = new StringIntSequence(sentence);
        } else {
          appender.clear();
        }

        int len = intsRef.length();
        int state = dictionary.startState();
        while (cur < len) {
          int c = intsRef.element(cur);
          int next = dictionary.nextState(state, c);
          if (next == NO_SUCH_STATE) {
            appender.clear();
            state = dictionary.startState();
            continue;
          }
          appender.append(c);
          cur++;
          if (dictionary.isWordEnded(next)) {
            return true;
          }
          state = next;
        }
        return false;
      }

      @Override public String next() {
        return appender.toString();
      }
    };
  }

  public boolean match(String source) {
    return getMatched(source).hasNext();
  }
}
