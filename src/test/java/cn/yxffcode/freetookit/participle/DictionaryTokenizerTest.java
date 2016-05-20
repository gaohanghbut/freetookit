package cn.yxffcode.freetookit.participle;

import cn.yxffcode.freetookit.algorithm.DictionaryTokenFilter;
import cn.yxffcode.freetookit.dic.AutomatonDictionary;
import cn.yxffcode.freetookit.io.IOStreams;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author gaohang on 15/12/12.
 */
public class DictionaryTokenizerTest {

  @Test public void test() {
    try (BufferedReader in = new BufferedReader(new InputStreamReader(
                                              DictionaryTokenizerTest.class.getResourceAsStream(
                                                                                        "/dic/chinese-standard.dic")))) {
      AutomatonDictionary dic = AutomatonDictionary.create(IOStreams.lines(in));
      DictionaryTokenFilter tokenizer = new DictionaryTokenFilter(dic);
      ArrayList<String> tokens =
                                                Lists.newArrayList(tokenizer.getMatched("大连绍杰车行245895麻烦看一下今天有没有T+0额度谢谢"));
      System.out.println(tokens);
    } catch (IOException e) {
      Throwables.propagate(e);
    }
  }
}
