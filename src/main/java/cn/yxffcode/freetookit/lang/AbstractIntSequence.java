package cn.yxffcode.freetookit.lang;

import com.google.common.primitives.Ints;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author gaohang on 15/12/7.
 */
public abstract class AbstractIntSequence implements IntSequence {

  protected final int offset;
  protected final int length;

  protected AbstractIntSequence(final int offset, final int length) {
    this.offset = offset;
    this.length = length;
  }

  @Override public int compareTo(final IntSequence o) {
    checkNotNull(o);
    for (int i = 0; i < this.length() && i < o.length(); i++) {
      int compared = Ints.compare(this.element(i), o.element(i));
      if (compared != 0) {
        return compared;
      }
    }
    //前缀完全相等,则长度大的更大
    return Ints.compare(this.length(), o.length());
  }

  @Override public int length() {
    return length;
  }

}
