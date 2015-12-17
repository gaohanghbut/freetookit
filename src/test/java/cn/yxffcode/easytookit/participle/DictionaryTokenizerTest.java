package cn.yxffcode.easytookit.participle;

import cn.yxffcode.easytookit.dic.DoubleArrayTrie;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author gaohang on 15/12/12.
 */
public class DictionaryTokenizerTest {

  @Test public void test() {
    List<String> words = Arrays.asList("湖北工", "北京", "大学");
    DoubleArrayTrie dic = DoubleArrayTrie.create(words);
    DictionaryTokenFilter tokenizer = new DictionaryTokenFilter(dic);
    ArrayList<String> tokens = Lists.newArrayList(tokenizer.getMatched("湖北京"));
    System.out.println(tokens);
  }
}
