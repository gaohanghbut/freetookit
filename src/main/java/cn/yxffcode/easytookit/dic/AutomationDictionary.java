package cn.yxffcode.easytookit.dic;

import cn.yxffcode.easytookit.automaton.Automaton;
import cn.yxffcode.easytookit.automaton.DefaultAutomaton;
import cn.yxffcode.easytookit.lang.StringIntSequence;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 基于自动机的词典
 *
 * @author gaohang on 15/12/10.
 */
public class AutomationDictionary implements Dictionary {

  private final Automaton automaton;

  private AutomationDictionary(final Automaton automaton) {
    this.automaton = automaton;
  }

  public static AutomationDictionary create(Iterable<String> words) {
    checkNotNull(words);

    DefaultAutomaton.DictionaryBuilder builder = new DefaultAutomaton.DictionaryBuilder();
    for (String word : words) {
      builder.addWord(new StringIntSequence(word));
    }
    return new AutomationDictionary(builder.build());
  }

  @Override public boolean match(final String word) {
    checkNotNull(word);
    return automaton.run(word);
  }

  @Override public int startState() {
    return Automaton.start();
  }

  @Override public int nextState(final int state, final int input) {
    return automaton.step(state, input);
  }

  @Override public boolean isWordEnded(final int state) {
    return automaton.isFinished(state);
  }
}
