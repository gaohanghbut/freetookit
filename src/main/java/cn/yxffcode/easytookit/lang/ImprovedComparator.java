package cn.yxffcode.easytookit.lang;

/**
 * {@link java.util.Comparator#compare(Object, Object)}的返回值不太明确,可使用枚举类型替换
 *
 * @author gaohang on 15/11/30.
 */
public interface ImprovedComparator<T> {
    Compared compare(final T left, final T right);
}
