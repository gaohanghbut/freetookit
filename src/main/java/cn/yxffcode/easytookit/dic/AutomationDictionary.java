package cn.yxffcode.easytookit.dic;

import cn.yxffcode.easytookit.automaton.Automaton;
import cn.yxffcode.easytookit.automaton.DefaultAutomaton;
import cn.yxffcode.easytookit.lang.StringIntsRef;
import com.sun.istack.internal.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 基于自动机的词典
 *
 * @author gaohang on 15/12/10.
 */
public class AutomationDictionary implements Dictionary {

    public static AutomationDictionary create(@NotNull Iterable<String> words) {
        checkNotNull(words);

        DefaultAutomaton.DictionaryBuilder builder = new DefaultAutomaton.DictionaryBuilder();
        for (String word : words) {
            builder.addWord(new StringIntsRef(word));
        }
        return new AutomationDictionary(builder.build());
    }

    private final Automaton automaton;

    private AutomationDictionary(final Automaton automaton) {
        this.automaton = automaton;
    }

    @Override
    public boolean match(@NotNull final String word) {
        checkNotNull(word);
        return automaton.run(word);
    }
}
