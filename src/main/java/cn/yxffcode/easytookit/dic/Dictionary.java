package cn.yxffcode.easytookit.dic;

import com.sun.istack.internal.NotNull;

/**
 * 字典的实现
 *
 * @author gaohang on 15/12/9.
 */
public interface Dictionary {

    boolean match(@NotNull  String word);
}
