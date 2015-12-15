package cn.yxffcode.easytookit.automaton;

import cn.yxffcode.easytookit.lang.IntsRef;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * 轻量级的自动机
 *
 * @author gaohang on 15/12/6.
 */
public class DefaultAutomaton extends Automaton {

  /**
   * 表示当前转换数组的位置
   */
  private int nextTransition;
  private int currentState;

  protected DefaultAutomaton(final int stateCount, final int transitionCount) {
    super(stateCount, transitionCount, 2);
    this.nextTransition = 1;
  }

  private void addTransition(int fromState, int toState, int value) {
    if (currentState != fromState * STATE_WIDTH) {
      currentState = fromState * STATE_WIDTH;
    }
    if (states.length <= fromState * STATE_WIDTH) {
      states = grow(states, STATE_WIDTH);
    }
    if (states[currentState] == TRANSITION_HEAD) {
      states[currentState] = nextTransition;
    }
    //连续的两个元素表示同一个状态,所以使用currentState + 1
    states[currentState + 1]++;

    if (transitions.length <= nextTransition) {
      transitions = grow(transitions, transitionWidth);
    }
    transitions[nextTransition++] = toState;
    transitions[nextTransition++] = value;
  }

  @Override
  protected boolean apply(final int off, final int value) {
    return transitions[off + 1] == value;
  }

  public void finish() {
    accept.set(currentState / STATE_WIDTH);
  }

  public static final class DictionaryBuilder {
    private List<List<Integer>> transitions = Lists.newArrayList();
    private BitSet              accept      = new BitSet();

    {
      transitions.add(new ArrayList<Integer>());
    }

    public DictionaryBuilder addWord(IntsRef ref) {
      int start = DefaultAutomaton.start();
      outer:
      for (int i = 0, j = ref.length(); i < j; i++) {
        List<Integer> trans = transitions.get(start);
        int element = ref.element(i);
        for (int k = 0; k < trans.size(); k += 2) {
          if (element == trans.get(k)) {
            start = trans.get(k + 1);
            continue outer;
          }
        }
        //no transition
        transitions.add(new ArrayList<Integer>());
        trans.add(element);
        trans.add(transitions.size() - 1);
        start = transitions.size() - 1;
      }
      accept.set(start);
      return this;
    }

    public DefaultAutomaton build() {
      int transCount = 0;
      for (List<Integer> trans : transitions) {
        transCount += trans.size();
      }
      DefaultAutomaton automaton = new DefaultAutomaton(transitions.size(), transCount);
      for (int i = 0, j = transitions.size(); i < j; i++) {
        List<Integer> trans = transitions.get(i);
        if (trans.size() == 0) {
          continue;
        }
        for (int k = 0, m = trans.size(); k < m; k += 2) {
          automaton.addTransition(i, trans.get(k + 1), trans.get(k));
        }
      }
      automaton.accept.or(accept);
      automaton.finish();
      return automaton;
    }
  }

  public static final class SimpleBuilder {
    private final DefaultAutomaton automaton;

    public SimpleBuilder(final int stateCount, final int transitionCount) {
      automaton = new DefaultAutomaton(stateCount, transitionCount);
    }

    public SimpleBuilder addTransition(int from, int to, int value) {
      automaton.addTransition(from, to, value);
      return this;
    }

    public SimpleBuilder setFinishedState(int state) {
      automaton.setFinishedState(state);
      return this;
    }

    public DefaultAutomaton build() {
      automaton.finish();
      return automaton;
    }
  }
}
