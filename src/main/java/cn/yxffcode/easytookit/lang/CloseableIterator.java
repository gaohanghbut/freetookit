package cn.yxffcode.easytookit.lang;

import java.io.Closeable;
import java.util.Iterator;

/**
 * @author gaohang on 15/9/21.
 * @see CloseableIterable
 */
public interface CloseableIterator<E> extends Iterator<E>, Closeable {
    void close();
}
