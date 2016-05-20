package cn.yxffcode.easytookit.collection;

import cn.yxffcode.easytookit.io.IOStreams;
import cn.yxffcode.easytookit.utils.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author gaohang on 16/5/20.
 */
public class GenericTrieTest {

  @Test
  public void test() throws IOException {
    GenericTrie trie = new GenericTrie();

    try (BufferedReader in = new BufferedReader(new InputStreamReader(GenericTrieTest.class
                                              .getResourceAsStream("/dic/cet4.dic")))) {
      for (String line : IOStreams.lines(in)) {
        if (!StringUtils.isBlank(line)) {
          trie.add(line.trim());
        }
      }
    }

    //do search
    Iterable<String> results = trie.search("fr");
    for (String result : results) {
      System.out.println(result);
    }
  }
}
