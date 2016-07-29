package cn.yxffcode.freetookit.dic;

import cn.yxffcode.freetookit.io.IOStreams;
import com.google.common.base.Stopwatch;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author gaohang on 15/12/10.
 */
public class DoubleArrayTrieTest {

  @Test public void test() {
    {
      List<String> words = Arrays.asList("limiku", "limika", "limikb",
              "limikc",
              "likla",
              "limlb",
              "mimik");
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

  public void testDict() throws IOException {
    DoubleArrayTrie doubleArrayTrie = new DoubleArrayTrie();

    List<String> words = new ArrayList<>(4001);
    try (BufferedReader in = new BufferedReader(new InputStreamReader(DoubleArrayTrieTest.class
            .getResourceAsStream("/dic/cet4.dic")))) {
      for (String line : IOStreams.lines(in)) {
        words.add(line.trim());
      }
    }
    Stopwatch stopwatch = Stopwatch.createStarted();
    for (String word : words) {
      doubleArrayTrie.add(word);
    }
    stopwatch.stop();
    System.out.println("build trie:" + stopwatch.elapsed(TimeUnit.MILLISECONDS));

    stopwatch = Stopwatch.createStarted();
    for (int i = 0; i < 10000; i++) {
      for (String word : words) {
        if (!doubleArrayTrie.match(word)) {
          throw new RuntimeException(word);
        }
      }
    }
    stopwatch.stop();
    System.out.println("match:" + stopwatch.elapsed(TimeUnit.MILLISECONDS));
  }
}
