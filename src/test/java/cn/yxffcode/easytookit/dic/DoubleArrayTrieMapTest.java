package cn.yxffcode.easytookit.dic;

import org.junit.Test;

/**
 * @author gaohang on 15/12/15.
 */
public class DoubleArrayTrieMapTest {

  @Test
  public void test() {
    DoubleArrayTrieMap<String> map = new DoubleArrayTrieMap<>();
    map.put("limiku", "厘米库");
    map.put("baidu", "百度");

    System.out.println(map.get("limiku"));
    System.out.println(map.get("baidu"));
  }
}
