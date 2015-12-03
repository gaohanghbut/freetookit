package cn.yxffcode.easytookit.lang;

import java.io.Closeable;

/**
 * 可被关闭的{@link Iterable}对象，可用于基于某种资源（如ResultSet）的迭代。
 *
 * <pre>
 *     {@code
 *          try (CloseableIterable<E> iter = xxx) {
 *              for (E elem : iter) {
 *                  ……
 *              }
 *          }
 *     }
 * </pre>
 *
 * @author gaohang on 15/9/21.
 */
public interface CloseableIterable<E> extends Iterable<E>, Closeable {

    /**
     * 关闭资源
     */
    void close();
}
