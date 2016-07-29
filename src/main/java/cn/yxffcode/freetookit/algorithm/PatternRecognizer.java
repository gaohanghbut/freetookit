package cn.yxffcode.freetookit.algorithm;

import cn.yxffcode.freetookit.collection.ImmutableIterator;
import cn.yxffcode.freetookit.dic.DoubleArrayTrie;
import cn.yxffcode.freetookit.io.IOStreams;
import cn.yxffcode.freetookit.lang.StringIntSequence;
import com.google.common.base.Throwables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * @author gaohang on 15/12/23.
 */
public class PatternRecognizer {
  private static final int NO_SUCH_STATE = -1;
  private final DoubleArrayTrie dictionary;
  private DoubleArrayTrie.FailureArray failureArray;

  private PatternRecognizer(DoubleArrayTrie dat) {
    this.dictionary = dat;
    this.failureArray = dat.buildFailureArray();
  }

  public static final PatternRecognizer create(String dictionaryPath) {
    try (BufferedReader in = new BufferedReader(new InputStreamReader(PatternRecognizer.class
            .getResourceAsStream(dictionaryPath)))) {

      DoubleArrayTrie dic = DoubleArrayTrie.create(IOStreams.lines(in));
      return new PatternRecognizer(dic);
    } catch (IOException e) {
      Throwables.propagate(e);
      return null;//not happen
    }
  }

  /**
   * 利用AC自动机做模式匹配,最短匹配方式
   */
  public Iterable<String> getMatched(final String sentence) {
    return new Iterable<String>() {
      @Override public Iterator<String> iterator() {
        return new ImmutableIterator<String>() {
          int endState;

          @Override public boolean hasNext() {
            if (endState != 0) {
              return true;
            }
            StringIntSequence intsRef = new StringIntSequence(sentence);

            int len = intsRef.length();
            int state = dictionary.startState();
            int cur = 0;
            while (cur < len) {
              int c = intsRef.element(cur);
              int next = dictionary.nextState(state, c);
              if (next == NO_SUCH_STATE) {
                state = failureArray.getFailNode(state);
                if (state == DoubleArrayTrie.FailureArray.ROOT_FAIL_NODE) {
                  state = dictionary.startState();
                  cur++;
                }
                continue;
              }
              if (dictionary.isWordEnded(next)) {
                endState = next;
                return true;
              }
              cur++;
              state = next;
            }
            return false;
          }

          @Override public String next() {
            String s = dictionary.buildWord(endState);
            endState = 0;
            return s;
          }
        };
      }
    };
  }
}
