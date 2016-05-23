package cn.yxffcode.freetookit.lang;

/**
 * @author gaohang on 16/5/23.
 */
public final class Lang {
  private Lang() {
  }

  /**
   * 取代三元运算符
   */
  public static int where(boolean condition, int t, int f) {
    return condition ? t : f;
  }

  /**
   * 将布尔转型成int
   */
  public static int _int_(boolean bool) {
    return where(bool, 1, 0);
  }

  /**
   * 将double转型成int
   */
  public static int _int_(double v) {
    return (int) v;
  }
}
