package cn.yxffcode.easytookit.automaton;

import cn.yxffcode.easytookit.collection.IntIterator;

import java.util.BitSet;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 轻量级的自动机
 *
 * @author gaohang on 15/12/6.
 */
public abstract class Automaton {

  /**
   * 状态的宽度,表示一个状态占{@link #states}数组中的元素个数
   */
  protected static final int STATE_WIDTH = 2;
  /**
   * {@link #transitions}数组中,第一个元素用来表示没有转换,所有状态的初始值都指向transition[0]
   */
  protected static final int TRANSITION_HEAD = 0;
  /**
   * 表示未知状态,如果不能识别输入,则返回此状态
   */
  private static final int UNKNOWN_STATE = -1;
  /**
   * 起始状态
   */
  private static final int INIT_STATE = 0;
  /**
   * 一次转换的宽度
   */
  protected final int transitionWidth;
  /**
   * 状态数组,一个状态由两个元素表示,第一个元素表示该状态的出度在{@link #transitions}数组中的起始位置,
   * 第二个元素表示状态的出度的数量
   */
  protected int[] states;
  /**
   * 转换数组,下标与{@link #states}数组相关,表示当前状态,值表示下一个状态,一个转换可以由数组中多个
   * 第一个元素表示下一个状态,第二个元素表示节点的输出
   */
  protected int[] transitions;
  /**
   * 是否是终止状态,使用bitset取代数组
   */
  protected BitSet accept;

  protected Automaton(final int stateCount, final int transitionCount, final int transitionWidth) {
    this.transitionWidth = transitionWidth;
    this.states = new int[STATE_WIDTH * stateCount + STATE_WIDTH];
    this.transitions = new int[this.transitionWidth * transitionCount + this.transitionWidth];
    this.accept = new BitSet(stateCount);
  }

  public static int start() {
    return INIT_STATE;
  }

  public static int unknown() {
    return UNKNOWN_STATE;
  }

  protected final int[] grow(int[] src, int growSize) {
    int[] tmp = new int[src.length + growSize];
    System.arraycopy(src, 0, tmp, 0, src.length);
    return tmp;
  }

  public final boolean run(final int[] word) {
    checkNotNull(word);
    return run(new IntIterator() {
      private int cur = 0;

      @Override public boolean hasNext() {
        return cur < word.length;
      }

      @Override public int next() {
        return word[cur++];
      }
    });
  }

  public final boolean run(final IntIterator values) {
    checkNotNull(values);
    int state = start();
    while (values.hasNext()) {
      int input = values.next();
      int nextState = step(state, input);
      if (nextState == unknown()) {
        return false;
      }
      state = nextState;
    }
    return isFinished(state);
  }

  public final int step(int current, int value) {
    int pos = current * 2;
    for (int off = states[pos], end = states[pos + 1] * transitionWidth + off; off < end; off += transitionWidth) {
      if (apply(off, value)) {
        return transitions[off];
      }
    }
    return UNKNOWN_STATE;
  }

  public final boolean isFinished(int state) {
    return accept.get(state);
  }

  protected abstract boolean apply(final int off, final int value);

  public final boolean run(final String word) {
    checkNotNull(word);
    return run(word.toCharArray());
  }

  public final boolean run(final char[] word) {
    checkNotNull(word);
    return run(new IntIterator() {
      private int cur;

      @Override public boolean hasNext() {
        return cur < word.length;
      }

      @Override public int next() {
        return word[cur++];
      }
    });
  }

  public final void setFinishedState(int state) {
    accept.set(state);
  }

}
