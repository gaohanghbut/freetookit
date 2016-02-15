package cn.yxffcode.easytookit.collection;

import cn.yxffcode.easytookit.automaton.ACAutomaton;
import cn.yxffcode.easytookit.dic.AlphabetTransformer;
import cn.yxffcode.easytookit.dic.Dictionary;
import cn.yxffcode.easytookit.lang.IntSequence;
import cn.yxffcode.easytookit.lang.StringIntSequence;

/**
 * @author gaohang on 16/1/5.
 */
public class ArrayTrie implements Dictionary {

  private transient final Node root;
  private final int childCountPerNode;
  private final AlphabetTransformer transformer;

  public ArrayTrie(int childCountPerNode, AlphabetTransformer transformer) {
    this.childCountPerNode = childCountPerNode;
    this.root = new Node();
    this.transformer = transformer;
  }

  public void add(String word) {
    Node node = this.root;
    IntSequence seq = new StringIntSequence(word);
    for (int i = 0, j = seq.length(); i < j; i++) {
      int e = seq.element(i);
      if (node.children == null) {
        node.children = new Node[childCountPerNode];
      }
      int wrapped = transformer.wrap(e);
      if (node.children[wrapped] != null) {
        continue;
      }
      Node n = new Node();
      n.value = wrapped;
      if (j == i - 1) {
        //end
        n.end = true;
      }
      node.children[wrapped] = n;
      node = n;
    }
  }

  @Override public boolean match(String word) {
    return false;
  }

  @Override public int startState() {
    return 0;
  }

  @Override public int nextState(int state, int input) {
    return 0;
  }

  @Override public boolean isWordEnded(int state) {
    return false;
  }

  @Override public ACAutomaton toAcAutomaton() {
    return null;
  }

  private static final class Node {
    int value;
    boolean end;
    Node[] children;
  }
}
