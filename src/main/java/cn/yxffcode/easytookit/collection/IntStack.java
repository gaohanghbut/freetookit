package cn.yxffcode.easytookit.collection;

/**
 * @author gaohang on 15/11/18.
 */
public class IntStack {

    private static final int INIT_SIZE = 10;
    private int[] stack;
    private int   top;

    public IntStack() {
        this(INIT_SIZE);
    }

    public IntStack(int initSize) {
        stack = new int[initSize];
    }

    public void push(int value) {
        if (top == stack.length) {
            int[] desc = new int[stack.length * 2];
            System.arraycopy(stack,
                             0,
                             desc,
                             0,
                             top);
            this.stack = desc;
        }

        stack[top++] = value;
    }

    public int poll() {
        int value = stack[top - 1];
        top--;
        return value;
    }

    public int peak() {
        return stack[top - 1];
    }

    public boolean isEmpty() {
        return top == 0;
    }
}
