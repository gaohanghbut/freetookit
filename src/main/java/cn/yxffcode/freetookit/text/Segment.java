package cn.yxffcode.freetookit.text;

import com.google.common.base.Objects;

/**
 * @author gaohang on 8/16/16.
 */
class Segment {
  private final String text;
  private final boolean variable;

  Segment(String text, boolean variable) {
    this.text = text;
    this.variable = variable;
  }

  public String getText() {
    return text;
  }

  public boolean isVariable() {
    return variable;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
            .add("text", text)
            .add("variable", variable)
            .toString();
  }
}
