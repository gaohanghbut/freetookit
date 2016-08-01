package cn.yxffcode.freetookit.math;

import cn.yxffcode.freetookit.lang.Merger;
import com.google.common.base.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 与向量相关的计算
 *
 * @author gaohang on 16/5/17.
 */
public final class Vectors {
  private Vectors() {
  }

  /**
   * 两个向量的内积(点乘)
   *
   * @param x 第一个向量
   * @param w 第二个向量
   * @return 向量的内积
   */
  public static double dot(double[] x, double[] w) {
    checkNotNull(x);
    checkNotNull(w);
    checkArgument(x.length == w.length);
    //计算内积
    return sum(x, w, new Merger<Double>() {
      @Override
      public Double merge(Double left, Double right) {
        return left * right;
      }
    });
  }

  /**
   * 两个向量的内积(点乘)
   *
   * @param x     第一个向量
   * @param y     第二个向量
   * @param xfrom x向量的起始
   * @param yfrom w向量的起始
   * @return 向量的内积
   */
  public static double dot(double[] x, int xfrom, double[] y, int yfrom) {
    checkNotNull(x);
    checkNotNull(y);
    double v = 0;
    for (int i = xfrom, j = yfrom; i < x.length; i++) {
      v += x[i] * y[j];
    }
    return v;
  }

  /**
   * 将向量元素相加
   */
  public static double sum(double[] x, double[] y, Merger<Double> merger) {
    checkNotNull(x);
    checkNotNull(y);
    checkArgument(x.length == y.length);
    double v = 0;
    for (int i = 0; i < x.length; i++) {
      v += merger.merge(x[i], y[i]);
    }
    return v;
  }

  /**
   * 求向量的模
   *
   * @param x 向量
   * @return 向量的模
   */
  public static double len(double[] x) {
    double sum = Maths.sum(x, new Function<Double, Double>() {
      @Override
      public Double apply(Double i) {
        return Math.pow(i, 2);
      }
    });
    return (double) Math.sqrt(sum);
  }

  /**
   * 计算两个向量的余弦值
   *
   * @param x 向量1
   * @param y 向量2
   * @return 余弦
   */
  public static double cos(double[] x, double[] y) {
    checkNotNull(x);
    checkNotNull(y);
    checkArgument(x.length == y.length);
    //求余弦值
    return dot(x, y) / (len(x) * len(y));
  }

  /**
   * 向量与常数相乘
   *
   * @param x 向量
   * @param y 乘量
   */
  public static void multiply(double[] x, double y) {
    checkNotNull(x);
    for (int i = 0; i < x.length; i++) {
      x[i] *= y;
    }
  }

  /**
   * 向量与常数相加
   *
   * @param x 向量
   * @param y 乘量
   */
  public static void add(double[] x, double y) {
    checkNotNull(x);
    for (int i = 0; i < x.length; i++) {
      x[i] += y;
    }
  }

  /**
   * 两个向量的内积(点乘)
   *
   * @param x 第一个向量
   * @param w 第二个向量
   * @return 向量的内积
   */
  public static int dot(int[] x, int[] w) {
    checkNotNull(x);
    checkNotNull(w);
    checkArgument(x.length == w.length);
    //计算内积
    return sum(x, w, new Merger<Integer>() {
      @Override
      public Integer merge(Integer left, Integer right) {
        return left * right;
      }
    });
  }

  /**
   * 将向量元素相加
   */
  public static int sum(int[] x, int[] y, Merger<Integer> merger) {
    checkNotNull(x);
    checkNotNull(y);
    checkArgument(x.length == y.length);
    int v = 0;
    for (int i = 0; i < x.length; i++) {
      v += merger.merge(x[i], y[i]);
    }
    return v;
  }

  /**
   * 求向量的模
   *
   * @param x 向量
   * @return 向量的模
   */
  public static int len(int[] x) {
    int sum = Maths.sum(x, new Function<Integer, Integer>() {
      @Override
      public Integer apply(Integer i) {
        return (int) Math.pow(i, 2);
      }
    });
    return (int) Math.sqrt(sum);
  }

  /**
   * 计算两个向量的余弦值
   *
   * @param x 向量1
   * @param y 向量2
   * @return 余弦
   */
  public static int cos(int[] x, int[] y) {
    checkNotNull(x);
    checkNotNull(y);
    checkArgument(x.length == y.length);
    //求余弦值
    return dot(x, y) / (len(x) * len(y));
  }

  /**
   * 向量与常数相乘
   *
   * @param x 向量
   * @param y 乘量
   */
  public static void multiply(int[] x, int y) {
    checkNotNull(x);
    for (int i = 0; i < x.length; i++) {
      x[i] *= y;
    }
  }

}
