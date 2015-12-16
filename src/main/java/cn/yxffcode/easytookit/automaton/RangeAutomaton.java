package cn.yxffcode.easytookit.automaton;

/**
 * 输入是一个范围的自动机
 *
 * @author gaohang on 15/12/7.
 */
public class RangeAutomaton extends Automaton {
  private int currentState;
  private int nextTransition;

  private RangeAutomaton(final int stateCount, final int transitionCount) {
    super(stateCount, transitionCount, 3);
    this.nextTransition = 1;
  }

  private void addTransition(int fromState, int toState, int min, int max) {
    if (currentState != fromState * STATE_WIDTH) {
      currentState = fromState * STATE_WIDTH;
    }
    if (states.length <= fromState * STATE_WIDTH) {
      states = grow(states, STATE_WIDTH);
    }
    if (states[currentState] == TRANSITION_HEAD) {
      states[currentState] = nextTransition;
    }
    states[currentState + 1]++;

    if (transitions.length <= nextTransition) {
      transitions = grow(transitions, transitionWidth);
    }
    transitions[nextTransition++] = toState;
    transitions[nextTransition++] = min;
    transitions[nextTransition++] = max;
  }

  public void finish() {
    accept.set(currentState / STATE_WIDTH);
  }

  @Override protected boolean apply(final int off, final int value) {
    int min = transitions[off + 1];
    int max = transitions[off + 2];
    return min <= value && max <= value;
  }

  public static final class Builder {
    private final RangeAutomaton automaton;

    public Builder(final int stateCount, final int transitionCount) {
      automaton = new RangeAutomaton(stateCount, transitionCount);
    }

    public Builder addTransition(int from, int to, int min, int max) {
      automaton.addTransition(from, to, min, max);
      return this;
    }

    public Builder setFinishedState(int state) {
      automaton.setFinishedState(state);
      return this;
    }

    public RangeAutomaton build() {
      automaton.finish();
      return automaton;
    }
  }
}
