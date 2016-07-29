package cn.yxffcode.freetookit.automaton;

import cn.yxffcode.freetookit.lang.StringIntSequence;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author gaohang on 15/12/7.
 */
public class DefaultAutomatonTest {

  @Test public void testAutomaton() {
    DefaultAutomaton automaton = new DefaultAutomaton.DictionaryBuilder().addWord(
            new StringIntSequence("hello"))
            .addWord(new StringIntSequence("helle"))
            .addWord(new StringIntSequence("halle"))
            .addWord(new StringIntSequence("halla"))
            .addWord(new StringIntSequence("hallb"))
            .addWord(new StringIntSequence("abc"))
            .addWord(new StringIntSequence("dkk"))
            .addWord(new StringIntSequence("dkkk"))
            .build();
    assertTrue(automaton.run("hello"));
    assertTrue(automaton.run("helle"));
    assertTrue(automaton.run("halle"));
    assertTrue(automaton.run("halla"));
    assertTrue(automaton.run("hallb"));
    assertTrue(automaton.run("abc"));
    assertTrue(automaton.run("dkk"));
    assertTrue(automaton.run("dkkk"));
    assertFalse(automaton.run("abcd"));
    assertFalse(automaton.run("eeee"));
  }
}
