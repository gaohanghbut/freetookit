package cn.yxffcode.easytookit.collection;

import cn.yxffcode.easytookit.dic.AlphabetTransformer;
import cn.yxffcode.easytookit.lang.StringIntSequence;

/**
 * @author gaohang on 16/1/8.
 */
public class LinkedTrie {

  private final Node root;
  private final AlphabetTransformer transformer;

  public LinkedTrie(AlphabetTransformer transformer) {
    this.transformer = transformer;
    this.root = new Node();
  }

  public void add(String word) {
    StringIntSequence seq = new StringIntSequence(word);
    Node node = this.root;
    for (int i = 0, j = seq.length(); i < j; i++) {
      int e = seq.element(i);
      int wrapped = transformer.wrap(e);
      if (node.child == null) {
        node.child = new Node();
        node.child.value = wrapped;
        node = node.child;
      } else {
        Node head = node.child;
        if (head.value == wrapped) {
          node = head;
          continue;
        } else if (head.value > wrapped) {
          Node n = new Node();
          n.value = wrapped;
          n.next = head;
          node.child = n;
          node = n;
        } else {
          while (head.next != null) {
            if (head.next.value == wrapped) {
              head = head.next;
              continue;
            }
            if (head.next.value > wrapped) {
              break;
            } else {
              head = head.next;
            }
          }
          //head.next > wrapped
          Node n = new Node();
          n.value = wrapped;
          n.next = head.next;
          head.next = n;
          node = n;
        }

      }

    }
  }

  private static final class Node {
    int value;
    Node next;
    Node child;
  }
}
