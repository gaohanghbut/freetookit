package cn.yxffcode.freetookit.math;

import com.google.common.base.Function;

import java.util.AbstractList;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author gaohang on 16/5/17.
 */
public final class Maths {
  private Maths() {
  }

  /**
   * 将数组的元素增加一个增量
   *
   * @param x     需要相加
   * @param from  需要增加的起始位置
   * @param to    需要增加的结束位置
   * @param delta 增量
   */
  public static void iterAdd(int[] x, int from, int to, int delta) {
    checkNotNull(x);
    checkArgument(from >= 0 && to <= x.length && to >= from);
    if (delta == 0) {
      return;
    }
    for (int i = from; i < to; i++) {
      x[i] += delta;
    }
  }

  public static void iterAdd(int[] x, int delta) {
    iterAdd(x, 0, x.length, delta);
  }

  /**
   * 对整形数组生成一个可迭代对象
   *
   * @param x    数组
   * @param from 迭代的起始
   * @param to   迭代的终止
   * @return 可迭代的对象
   */
  public static Iterable<Integer> iter(final int[] x, final int from, final int to) {
    checkNotNull(x);
    checkArgument(from >= 0 && to >= from && to <= x.length);
    if (from == to) {
      return Collections.emptyList();
    }
    return new AbstractList<Integer>() {
      @Override public Integer get(int index) {
        return x[from + index];
      }

      @Override public int size() {
        return to - from;
      }
    };
  }

  /**
   * 加权求和
   *
   * @param x    数组
   * @param func 加权函数
   * @return 求和结果
   */
  public static int sum(double[] x, Function<Double, Double> func) {
    checkNotNull(x);
    checkNotNull(func);
    int v = 0;
    for (Double i : x) {
      v += func.apply(i);
    }
    return v;
  }

  /**
   * 加权求和
   *
   * @param x    数组
   * @param func 加权函数
   * @return 求和结果
   */
  public static int sum(int[] x, Function<Integer, Integer> func) {
    checkNotNull(x);
    checkNotNull(func);
    int v = 0;
    for (Integer i : x) {
      v += func.apply(i);
    }
    return v;
  }
}
