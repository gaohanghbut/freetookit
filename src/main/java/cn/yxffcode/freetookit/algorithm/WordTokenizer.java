package cn.yxffcode.freetookit.algorithm;

import cn.yxffcode.freetookit.automaton.ACAutomaton;
import cn.yxffcode.freetookit.collection.ImmutableIterator;
import cn.yxffcode.freetookit.dic.DoubleArrayTrie;

import java.util.Iterator;

/**
 * @author gaohang on 15/12/20.
 */
public abstract class WordTokenizer {
  private final DoubleArrayTrie trie;
  private final ACAutomaton acAutomaton;

  protected WordTokenizer(DoubleArrayTrie trie) {
    this.trie = trie;
    this.acAutomaton = trie.toAcAutomaton();
  }

  public Iterable<String> token(String input) {
    return new Iterable<String>() {
      @Override public Iterator<String> iterator() {
        return new ImmutableIterator<String>() {
          @Override public boolean hasNext() {
            return false;
          }

          @Override public String next() {
            return null;
          }
        };
      }
    };
  }
}
