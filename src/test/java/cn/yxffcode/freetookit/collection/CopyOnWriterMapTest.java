package cn.yxffcode.freetookit.collection;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author gaohang on 16/8/1.
 */
public class CopyOnWriterMapTest {

  private void fillMap(final CopyOnWriterMap<String, Integer> map) {
    for (int i = 0; i < 100; i++) {
      map.put(Integer.toString(i), i);
    }
  }

  @Test
  public void testSize() throws Exception {
    CopyOnWriterMap<String, Integer> map = new CopyOnWriterMap<>();
    fillMap(map);
    assertEquals(map.size(), 100);
  }

  @Test
  public void testIsEmpty() throws Exception {
    CopyOnWriterMap<String, Integer> map = new CopyOnWriterMap<>();
    assertEquals(map.size(), 0);
    fillMap(map);
    assertEquals(map.size(), 100);
  }

  @Test
  public void testContainsKey() throws Exception {
    CopyOnWriterMap<String, Integer> map = new CopyOnWriterMap<>();
    fillMap(map);
    assertEquals(map.containsKey("1"), true);
    assertEquals(map.containsKey("10000"), false);
  }

  @Test
  public void testContainsValue() throws Exception {
    CopyOnWriterMap<String, Integer> map = new CopyOnWriterMap<>();
    fillMap(map);
    assertEquals(map.containsValue(1), true);
    assertEquals(map.containsValue(10000), false);
  }

  @Test
  public void testPut() throws Exception {
    final CopyOnWriterMap<String, Integer> map = new CopyOnWriterMap<>();
    fillMap(map);
    Thread reader = new Thread(new Runnable() {
      @Override public void run() {
        for (Map.Entry<String, Integer> en : map.entrySet()) {
          assertFalse(en.getValue() >= 10000);
        }
      }
    });
    reader.setDaemon(false);
    Thread writer = new Thread(new Runnable() {
      @Override public void run() {
        for (int i = 0; i < 100; i++) {
          map.put(Integer.toString(i + 10000), i + 10000);
        }
      }
    });
    writer.setDaemon(false);
    reader.start();
    writer.start();
  }

  @Test
  public void testRemove() throws Exception {

  }

  @Test
  public void testPutAll() throws Exception {

  }

  @Test
  public void testClear() throws Exception {

  }

  @Test
  public void testKeySet() throws Exception {

  }

  @Test
  public void testValues() throws Exception {

  }

  @Test
  public void testEntrySet() throws Exception {

  }

  @Test
  public void testPutIfAbsent() throws Exception {

  }

  @Test
  public void testRemove1() throws Exception {

  }

  @Test
  public void testReplace() throws Exception {

  }

  @Test
  public void testReplace1() throws Exception {

  }
}
