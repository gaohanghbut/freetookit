package cn.yxffcode.easytookit.automaton;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 轻量级的自动机
 *
 * @author gaohang on 15/12/6.
 */
public class Automaton {

    /**
     * 表示未知状态,如果不能识别输入,则返回此状态
     */
    private static final int UNKNOWN_STATE    = - 1;
    /**
     * 起始状态
     */
    private static final int INIT_STATE       = 0;
    /**
     * 状态的宽度,表示一个状态占{@link #states}数组中的元素个数
     */
    private static final int STATE_WIDTH      = 2;
    /**
     * 一次转换的宽度
     */
    private static final int TRANSITION_WIDTH = 2;
    /**
     * {@link #transitions}数组中,第一个元素用来表示没有转换,所有状态的初始值都指向transition[0]
     */
    private static final int TRANSITION_HEAD  = 0;

    /**
     * 状态数组,一个状态由两个元素表示,第一个元素表示该状态的出度在{@link #transitions}数组中的起始位置,
     * 第二个元素表示状态的出度的数量
     */
    private int[]  states;
    /**
     * 转换数组,下标与{@link #states}数组相关,表示当前状态,值表示下一个状态,一个转换由数组中的两个元素表示,
     * 第一个元素表示下一个状态,第二个元素表示节点的输出
     */
    private int[]  transitions;
    /**
     * 是否是终止状态,使用bitset取代数组
     */
    private BitSet accept;
    /**
     * 表示当前转换数组的位置
     */
    private int    nextTransition;
    private int    currentState;

    private Automaton(int stateCount,
                      int transitionCount) {
        this.states = new int[STATE_WIDTH * stateCount + STATE_WIDTH];
        this.transitions = new int[TRANSITION_WIDTH * transitionCount + TRANSITION_WIDTH];
        this.accept = new BitSet(stateCount);
        this.nextTransition = 1;
    }

    private void addTransition(int fromState,
                               int toState,
                               int value) {
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
            transitions = grow(transitions, TRANSITION_WIDTH);
        }
        transitions[nextTransition++] = toState;
        transitions[nextTransition++] = value;
    }

    private int[] grow(int[] src,
                       int growSize) {
        int[] tmp = new int[src.length + growSize];
        System.arraycopy(src, 0, tmp, 0, src.length);
        return tmp;
    }

    public int step(int current,
                    int value) {
        int pos = current * 2;
        for (int off = states[pos], end = states[pos + 1] * TRANSITION_WIDTH + off;
             off < end; off += TRANSITION_WIDTH) {
            if (transitions[off + 1] == value) {
                return transitions[off];
            }
        }
        return UNKNOWN_STATE;
    }

    public void finish() {
        accept.set(currentState / STATE_WIDTH);
    }

    public static int start() {
        return INIT_STATE;
    }

    public static int unknown() {
        return UNKNOWN_STATE;
    }

    public boolean isFinished(int state) {
        return accept.get(state);
    }

    public boolean run(final int[] word) {
        checkNotNull(word);
        return run(new IntIterator() {
            private int cur = 0;

            @Override
            public boolean hasNext() {
                return cur < word.length;
            }

            @Override
            public int next() {
                return word[cur++];
            }
        });
    }

    public boolean run(final char[] word) {
        checkNotNull(word);
        return run(new IntIterator() {
            private int cur;

            @Override
            public boolean hasNext() {
                return cur < word.length;
            }

            @Override
            public int next() {
                return word[cur++];
            }
        });
    }

    public boolean run(final String word) {
        checkNotNull(word);
        return run(word.toCharArray());
    }

    public boolean run(final IntIterator values) {
        checkNotNull(values);
        int state = start();
        while (values.hasNext()) {
            int input     = values.next();
            int nextState = step(state, input);
            if (nextState == unknown()) {
                return false;
            }
            state = nextState;
        }
        return isFinished(state);
    }

    public void setFinishedState(int state) {
        accept.set(state);
    }

    public static final class DictionaryBuilder {
        private List<List<Integer>> transitions = Lists.newArrayList();

        {
            transitions.add(new ArrayList<Integer>());
        }

        public DictionaryBuilder addWord(IntsRef ref) {
            int start = Automaton.start();
            outer:
            for (int i = 0, j = ref.length(); i < j; i++) {
                List<Integer> trans   = transitions.get(start);
                int           element = ref.element(i);
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
            return this;
        }

        public Automaton build() {
            int transCount = 0;
            for (List<Integer> trans : transitions) {
                transCount += trans.size();
            }
            Automaton automaton = new Automaton(transitions.size(), transCount);
            for (int i = 0, j = transitions.size(); i < j; i++) {
                List<Integer> trans = transitions.get(i);
                if (trans.size() == 0) {
                    automaton.setFinishedState(i);
                    continue;
                }
                for (int k = 0, m = trans.size(); k < m; k += 2) {
                    automaton.addTransition(i, trans.get(k + 1), trans.get(k));
                }
            }
            automaton.finish();
            return automaton;
        }
    }

    public static final class SimpleBuilder {
        private final Automaton automaton;

        public SimpleBuilder(final int stateCount,
                             final int transitionCount) {
            automaton = new Automaton(stateCount, transitionCount);
        }

        public SimpleBuilder addTransition(int from,
                                           int to,
                                           int value) {
            automaton.addTransition(from, to, value);
            return this;
        }

        public SimpleBuilder setFinishedState(int state) {
            automaton.setFinishedState(state);
            return this;
        }

        public Automaton build() {
            automaton.finish();
            return automaton;
        }
    }
}
