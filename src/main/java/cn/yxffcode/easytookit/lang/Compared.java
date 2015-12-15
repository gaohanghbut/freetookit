package cn.yxffcode.easytookit.lang;

/**
 * 用枚举来替代{@link java.util.Comparator#compare(Object, Object)}方法返回值(-1, 0, 1)
 *
 * @author gaohang on 15/11/30.
 */
public enum Compared {
  /**
   * 表示大于
   */
  GREATER {
    @Override
    public int getComparatorResult() {
      return 1;
    }
  },
  /**
   * 表示等于
   */
  EQUAL {
    @Override
    public int getComparatorResult() {
      return 0;
    }
  },
  /**
   * 表示小于
   */
  SMALLER {
    @Override
    public int getComparatorResult() {
      return -1;
    }
  };

  public abstract int getComparatorResult();
}
