package cn.yxffcode.easytookit.dic;

import cn.yxffcode.easytookit.algorithm.PrefixSearcher;
import cn.yxffcode.easytookit.lang.AbstractIterable;
import cn.yxffcode.easytookit.lang.ReverseStringBuilder;
import com.google.common.base.Predicate;
import com.google.common.collect.TreeTraverser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * 普通的字典树实现,同时实现前缀搜索
 *
 * @author gaohang on 16/5/17.
 */
public class GenericTrie implements PrefixSearcher {

  public static final GenericTrie EMPTY = new GenericTrie();

  private final Node root;
  private final boolean readOnly;

  public GenericTrie() {
    this(new Node((char) 0, null));
  }

  private GenericTrie(Node root, boolean readOnly) {
    this.root = root;
    this.readOnly = readOnly;
  }

  private GenericTrie(Node root) {
    this(root, false);
  }

  /**
   * 添加词条
   */
  public void add(String word) {
    checkState(!readOnly, "This trie is read only.");
    checkArgument(!isNullOrEmpty(word));
    Node node = this.root;
    for (int i = 0, j = word.length(); i < j; i++) {
      char c = word.charAt(i);
      Node child = node.children.get(c);
      if (child != null) {
        node = child;
        continue;
      }
      //create new node.
      Node newNode = new Node(c, node);
      node.children.put(c, newNode);
      node = newNode;
    }
    //mark the end.
    if (node != root) {
      node.end = true;
    }
  }

  /**
   * 通过前缀获取子树
   */
  public GenericTrie subTrie(String prefix) {
    if (prefix == null) {
      return this;
    }

    Node node = root;
    for (int i = 0, j = prefix.length(); i < j; i++) {
      char c = prefix.charAt(i);
      Node child = node.children.get(c);
      if (child == null) {
        //前缀匹配失败,没有搜索结果
        return EMPTY;
      }
      node = child;
    }
    //返回以node为根的子树
    if (node == root) {
      return this;
    }
    return new GenericTrie(node, true);
  }

  @Override public Iterable<String> search(final String prefix) {

    return new AbstractIterable<String>() {
      private Iterator<Node> endNodes = null;
      /**
       * 不能直接用{@link GenericTrie#root},查找到的是整个Trie的子树
       * @see #ensureInit()
       */
      private Node trieRoot = null;

      @Override protected boolean hasNext() {
        ensureInit();
        return endNodes.hasNext();
      }

      @Override protected String next() {
        Node node = endNodes.next();
        ReverseStringBuilder sb = new ReverseStringBuilder();
        while (node != trieRoot) {
          sb.append(node.elem);
          node = node.parent;
        }
        return prefix + sb.toString();
      }

      private void ensureInit() {
        if (endNodes != null) {
          return;
        }
        GenericTrie subTrie = subTrie(prefix);
        //执行结果的拼接,使用Iterable实现lazy
        TreeTraverser<Node> treeTraverser = new TreeTraverser<Node>() {
          @Override public Iterable<Node> children(Node root) {
            return root.children.values();
          }
        };
        this.endNodes = treeTraverser.breadthFirstTraversal(subTrie.root)
                                                  .filter(EndNodePredicate.INSTANCE).iterator();
        this.trieRoot = subTrie.root;
      }
    };
  }

  private enum EndNodePredicate implements Predicate<Node> {
    INSTANCE;

    @Override public boolean apply(Node node) {
      return node.end;
    }
  }


  private static final class Node {
    private final char elem;
    private final Map<Character, Node> children;
    private final Node parent;
    private boolean end;

    private Node(char elem, Node parent) {
      this.elem = elem;
      this.parent = parent;
      this.children = new HashMap<>();
    }
  }
}
