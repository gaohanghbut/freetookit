package cn.yxffcode.freetookit.collection;

/**
 * @author gaohang
 */
public final class SuffixTree {

  private SuffixNode root;
  private Active active;
  private int remainingSuffixCount;
  private End end;
  private char[] input;

  public SuffixTree(char[] input) {
    this.input = new char[input.length + 1];
    System.arraycopy(input, 0, this.input, 0, input.length);
    this.input[input.length] = '$';
  }

  public void build() {
    root = SuffixNode.createNode(1, new End(0));
    root.index = -1;
    active = new Active(root);
    this.end = new End(-1);
    for (int i = 0; i < input.length; i++) {
      startPhase(i);
    }
    setIndexByDfs(root, 0, input.length);
  }

  private void startPhase(int i) {
    SuffixNode lastCreatedInternalNode = null;
    end.end++;
    remainingSuffixCount++;
    while (remainingSuffixCount > 0) {
      if (active.activeLength == 0) {
        if (active.activeNode.child[input[i]] != null) {
          active.activeEdge = active.activeNode.child[input[i]].start;
          active.activeLength++;
          break;
        } else {
          root.child[input[i]] = SuffixNode.createNode(i, end);
          remainingSuffixCount--;
        }
        continue;
      }
      try {
        char ch = nextChar(i);
        if (ch == input[i]) {
          //TODO - Could be wrong here. Do we only do this if when walk down goes past a node or we do it every time.
          if (lastCreatedInternalNode != null) {
            lastCreatedInternalNode.suffixLink = active.activeNode.child[input[active.activeEdge]];
          }
          walkDown(i);
          break;
        }
        SuffixNode node = active.activeNode.child[input[active.activeEdge]];
        int oldStart = node.start;
        node.start = node.start + active.activeLength;
        SuffixNode newInternalNode = SuffixNode.createNode(oldStart, new End(oldStart + active.activeLength - 1));

        SuffixNode newLeafNode = SuffixNode.createNode(i, this.end);

        newInternalNode.child[input[newInternalNode.start + active.activeLength]] = node;
        newInternalNode.child[input[i]] = newLeafNode;
        newInternalNode.index = -1;
        active.activeNode.child[input[newInternalNode.start]] = newInternalNode;

        if (lastCreatedInternalNode != null) {
          lastCreatedInternalNode.suffixLink = newInternalNode;
        }
        lastCreatedInternalNode = newInternalNode;
        newInternalNode.suffixLink = root;

        if (active.activeNode != root) {
          active.activeNode = active.activeNode.suffixLink;
        } else {
          active.activeEdge = active.activeEdge + 1;
          active.activeLength--;
        }
        remainingSuffixCount--;

      } catch (EndOfPathException e) {
        SuffixNode node = active.activeNode.child[input[active.activeEdge]];
        node.child[input[i]] = SuffixNode.createNode(i, end);
        if (active.activeNode != root) {
          active.activeNode = active.activeNode.suffixLink;
        } else {
          active.activeEdge = active.activeEdge + 1;
          active.activeLength--;
        }
        remainingSuffixCount--;
      }
    }
  }

  private void walkDown(int index) {
    SuffixNode node = active.activeNode.child[input[active.activeEdge]];
    if (lengthOf(node) < active.activeLength) {
      active.activeNode = node;
      active.activeLength = active.activeLength - lengthOf(node);
      active.activeEdge = node.child[input[index]].start;
    } else {
      active.activeLength++;
    }
  }

  private char nextChar(int i) throws EndOfPathException {
    SuffixNode node = active.activeNode.child[input[active.activeEdge]];
    if (lengthOf(node) >= active.activeLength) {
      return input[active.activeNode.child[input[active.activeEdge]].start + active.activeLength];
    }
    if (lengthOf(node) + 1 == active.activeLength) {
      if (node.child[input[i]] != null) {
        return input[i];
      }
      throw new EndOfPathException();
    }
    active.activeNode = node;
    active.activeLength = active.activeLength - lengthOf(node) - 1;
    active.activeEdge = active.activeEdge + lengthOf(node) + 1;
    return input[active.activeNode.child[input[active.activeEdge]].start + active.activeLength];
  }

  private int lengthOf(SuffixNode node) {
    return node.end.end - node.start;
  }

  private void setIndexByDfs(SuffixNode root, int val, int size) {
    if (root == null) {
      return;
    }

    val += root.end.end - root.start + 1;
    if (root.index != -1) {
      root.index = size - val;
      return;
    }

    for (SuffixNode node : root.child) {
      setIndexByDfs(node, val, size);
    }
  }


  private static class EndOfPathException extends Exception {
  }


  private static final class SuffixNode {

    private static final int TOTAL = 256;
    private SuffixNode[] child = new SuffixNode[TOTAL];
    private int start;
    private End end;
    private int index;
    private SuffixNode suffixLink;

    private SuffixNode() {
    }

    public static SuffixNode createNode(int start, End end) {
      SuffixNode node = new SuffixNode();
      node.start = start;
      node.end = end;
      return node;
    }

    @Override
    public String toString() {
      StringBuilder buffer = new StringBuilder();
      int i = 0;
      for (SuffixNode node : child) {
        if (node != null) {
          buffer.append((char) i).append(" ");
        }
        i++;
      }
      return "SuffixNode [start=" + start + "]" + " " + buffer.toString();
    }
  }


  private static final class End {
    int end;

    public End(int end) {
      this.end = end;
    }
  }


  private static final class Active {
    private SuffixNode activeNode;
    private int activeEdge;
    private int activeLength;

    private Active(SuffixNode node) {
      activeLength = 0;
      activeNode = node;
      activeEdge = -1;
    }

    @Override
    public String toString() {

      return "Active [activeNode=" + activeNode + ", activeIndex="
              + activeEdge + ", activeLength=" + activeLength
              + "]";
    }
  }

}
