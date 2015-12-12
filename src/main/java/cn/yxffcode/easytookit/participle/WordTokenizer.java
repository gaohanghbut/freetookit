package cn.yxffcode.easytookit.participle;

import java.util.Iterator;

/**
 * @author gaohang on 15/12/12.
 */
public interface WordTokenizer {
    Iterator<String> token(String sentence);
}
