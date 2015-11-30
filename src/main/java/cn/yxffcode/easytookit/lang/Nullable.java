package cn.yxffcode.easytookit.lang;

/**
 * 表示一个对象可为null。
 * <p/>
 * 使用一个具体的对象来表示null，当方法需要返回null里，可返回空对象，用于避免NPE
 *
 * @author gaohang on 15/9/25.
 */
public interface Nullable {
    boolean isNull();
}
