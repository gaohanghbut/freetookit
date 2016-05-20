package cn.yxffcode.freetookit.collection;

import cn.yxffcode.freetookit.lang.IntIterable;

import java.io.Serializable;

/**
 * @author gaohang on 15/11/18.
 */
public class IntStack implements IntIterable, Serializable {

  private static final long serialVersionUID = -7535273734926642719L;
  private static final int INIT_SIZE = 10;

  private int[] stack;
  private int top;

  public IntStack() {
    this(INIT_SIZE);
  }

  public IntStack(int initSize) {
    stack = new int[initSize];
  }

  public void push(int value) {
    if (top == stack.length) {
      int[] desc = new int[stack.length * 2];
      System.arraycopy(stack, 0, desc, 0, top);
      this.stack = desc;
    }

    stack[top++] = value;
  }

  public int poll() {
    int value = stack[top - 1];
    top--;
    return value;
  }

  public int element(int index) {
    return stack[index];
  }

  public int length() {
    return top;
  }

  public int peak() {
    return stack[top - 1];
  }

  public boolean isEmpty() {
    return top == 0;
  }

  @Override public IntIterator iterator() {
    return new IntIterator() {
      private int cur;

      @Override public boolean hasNext() {
        return cur < top;
      }

      @Override public int next() {
        return stack[cur++];
      }
    };
  }

  @Override public String toString() {
    StringBuilder appender = new StringBuilder();
    appender.append("[");
    for (int i = 0, j = top - 1; i < j; i++) {
      appender.append(stack[i]).append(',').append(' ');
    }
    if (top - 1 >= 0) {
      appender.append(stack[top - 1]);
    }
    return appender.append("]").toString();
  }
}
