package cn.yxffcode.freetookit.collection;

import cn.yxffcode.freetookit.lang.IntIterable;

import java.io.Serializable;

/**
 * @author gaohang on 15/11/18.
 */
public class IntArrayList implements IntIterable, Serializable {

  private static final long serialVersionUID = -7535273734926642719L;
  private static final int INIT_SIZE = 10;

  private int[] data;
  private int size;

  public IntArrayList() {
    this(INIT_SIZE);
  }

  public IntArrayList(int initSize) {
    data = new int[initSize];
  }

  public void add(int value) {
    if (size == data.length) {
      int[] desc = new int[data.length * 2];
      System.arraycopy(data, 0, desc, 0, size);
      this.data = desc;
    }

    data[size++] = value;
  }

  public int get() {
    if (size == 0) {
      throw new ArrayIndexOutOfBoundsException("size is 0");
    }
    return data[0];
  }

  public int get(int index) {
    if (index < 0 || index >= size) {
      throw new ArrayIndexOutOfBoundsException(Integer.toString(index));
    }
    return data[index];
  }

  public int element(int index) {
    return data[index];
  }

  public int length() {
    return size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  @Override public IntIterator iterator() {
    return new IntIterator() {
      private int cur;

      @Override public boolean hasNext() {
        return cur < size;
      }

      @Override public int next() {
        return data[cur++];
      }
    };
  }

  @Override public String toString() {
    StringBuilder appender = new StringBuilder();
    appender.append("[");
    for (int i = 0, j = size - 1; i < j; i++) {
      appender.append(data[i]).append(',').append(' ');
    }
    if (size - 1 >= 0) {
      appender.append(data[size - 1]);
    }
    return appender.append("]").toString();
  }
}
