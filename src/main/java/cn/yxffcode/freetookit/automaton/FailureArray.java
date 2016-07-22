package cn.yxffcode.freetookit.automaton;

import cn.yxffcode.freetookit.utils.ArrayUtils;

/**
 * AC自动机的失败指针
 *
 * @author gaohang on 15/12/17.
 */
public class FailureArray {

  public static final int ROOT_FAIL_NODE = -1;

  private int[] failedNodes;

  public FailureArray() {
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
