package cn.yxffcode.freetookit.automaton;

import cn.yxffcode.freetookit.utils.ArrayUtils;

/**
 * AC自动机,此处的实现只用于记录失败指针
 *
 * @author gaohang on 15/12/17.
 */
public class ACAutomaton {

  public static final int ROOT_FAIL_NODE = -1;

  private int[] failedNodes;

  public ACAutomaton() {
    this.failedNodes = new int[10];
  }

  public void addFailNode(int srcNode, int failedNode) {
    if (srcNode >= failedNodes.length) {
      failedNodes = ArrayUtils.grow(failedNodes, 2 * srcNode);
    }
    failedNodes[srcNode] = failedNode;
  }

  public int getFailNode(int srcNode) {
    return failedNodes[srcNode];
  }
}
