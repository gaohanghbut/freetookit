package cn.yxffcode.freetookit.algorithm;

import java.util.Arrays;

import static cn.yxffcode.freetookit.lang.Lang._int_;
import static cn.yxffcode.freetookit.lang.Lang.where;
import static cn.yxffcode.freetookit.math.Vectors.dot;

/**
 * PLA(Perceptron-Learning-Algorithm)
 * <p>
 * {@link #training(int[], double[][])}方法用作对输入样本做训练,得到{@link #weight}数组
 * <p>
 * {@link #predict(double[])}方法可用通过输入的特征向量计算出向量表示的数据属于哪个分类
 *
 * @author gaohang on 16/5/17.
 */
public class Perceptron {

  /**
   * eta参数,介于0和1之间
   */
  private final float eta;
  /**
   * 每次训练迭代的次数
   */
  private final int niter;

  /**
   * 训练后得到的权重模型
   */
  private double[] weight;
  private int[] errors;

  public Perceptron(float eta, int niter, int futuresCount) {
    this.eta = eta;
    this.niter = niter;
    this.errors = new int[niter];
    this.weight = new double[futuresCount + 1];
  }

  /**
   * 样本训练
   *
   * @param y 训练样本中每个样本的值(1/-1)
   * @param x 训练样本集中每个样本的特征向量
   */
  public Perceptron training(int[] y, double[][] x) {
    Arrays.fill(this.errors, 0);
    Arrays.fill(this.weight, 0);
    for (int n = 0; n < niter; n++) {
      int error = 0;
      for (int i = 0; i < y.length; i++) {
        double update = this.eta * (y[i] - predict(x[i]));
        this.weight[0] += update;
        for (int j = 1; j < weight.length; j++) {
          this.weight[j] += update * x[i][j - 1];
        }
        error += _int_(update != 0d);
      }
      this.errors[n] = error;
    }
    return this;
  }

  public int predict(double[] x) {
    //计算内积
    double v = dot(x, 0, weight, 1) + weight[0];
    return where(v >= 0d, 1, -1);
  }

  public double[] getWeight() {
    return weight;
  }
}
