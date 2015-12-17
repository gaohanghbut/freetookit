package cn.yxffcode.easytookit.participle;

import java.util.Iterator;

/**
 * 关键词过虑
 *
 * @author gaohang on 15/12/12.
 */
public interface WordTokenFilter {
  Iterator<String> getMatched(String sentence);
}
