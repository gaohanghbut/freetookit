package cn.yxffcode.easytookit.dic;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author gaohang on 15/12/10.
 */
public class DoubleArrayTrieTest {

  @Test
  public void test() {
    {
      List<String> words = Arrays.asList("limiku", "limika", "limikb", "limikc", "likla", "limlb", "mimik");
      DoubleArrayTrie trie = DoubleArrayTrie.create(words);

      for (String word : words) {
        assertTrue(trie.match(word));
      }
      //不存在的不能匹配成功
      assertFalse(trie.match("limi"));
    }
    {
      List<String> words = Arrays.asList("厘米网", "厘米库", "厘米百", "厘米米", "去哪儿", "百度");
      DoubleArrayTrie trie = DoubleArrayTrie.create(words);

      for (String word : words) {
        assertTrue(trie.match(word));
      }
      assertFalse(trie.match("厘"));
    }
    {
      List<String> words = Arrays.asList("qunar", "去哪儿");
      DoubleArrayTrie trie = DoubleArrayTrie.create(words);

      for (String word : words) {
        assertTrue(trie.match(word));
      }

    }
  }
}
