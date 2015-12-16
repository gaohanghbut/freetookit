package cn.yxffcode.easytookit.dic;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author gaohang on 15/12/15.
 */
public class DoubleArrayTrieMapTest {

  @Test public void test() {
    DoubleArrayTrieMap<String> map = new DoubleArrayTrieMap<>();
    map.put("limiku", "厘米库");
    map.put("baidu", "百度");

    Assert.assertEquals("厘米库", map.get("limiku"));
    Assert.assertEquals("百度", map.get("baidu"));
  }
}
