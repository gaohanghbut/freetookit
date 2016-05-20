package cn.yxffcode.freetookit.dic;

import cn.yxffcode.freetookit.lang.IntSequence;
import cn.yxffcode.freetookit.lang.StringIntSequence;
import cn.yxffcode.freetookit.utils.ArrayUtils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author gaohang on 15/12/15.
 */
public class DoubleArrayTrieMap<V> extends DoubleArrayTrie {
  private static final int DEFAULT_VALUE_SIZE = 10;

  private Object[] values;

  public DoubleArrayTrieMap() {
    values = new Object[DEFAULT_VALUE_SIZE];
  }

  public DoubleArrayTrieMap(final AlphabetTransformer alphabetTransformer) {
    super(alphabetTransformer);
    values = new Object[DEFAULT_VALUE_SIZE];
  }

  public void put(String key, V value) {
    int state = add(key);
    if (state >= values.length) {
      values = ArrayUtils.grow(values, Object.class, state * 2);
    }
    values[state] = value;
  }

  public V get(String key) {
    checkNotNull(key);
    IntSequence intSequence = new StringIntSequence(key + END_INPUT);
    int s = startState();
    for (int i = 0, j = intSequence.length(); i < j; i++) {
      int c = intSequence.element(i);
      int t = nextState(s, c);
      if (t == NO_SUCH_STATE) {
        return null;
      }
      s = t;
    }
    //不需要担心数组越界,如果会发生数组越界,则key的匹配不会成功
    return (V) values[s];
  }

}
