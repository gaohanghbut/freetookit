package cn.yxffcode.freetookit.utils;

import com.google.common.base.Joiner;

/**
 * @author gaohang on 16/5/25.
 */
public final class Joiners {
  private Joiners() {
  }

  private static final Joiner LINE = Joiner.on('\n');

  public static String join(String... lines) {
    return LINE.join(lines);
  }
}
