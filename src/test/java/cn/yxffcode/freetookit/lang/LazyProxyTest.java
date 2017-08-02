package cn.yxffcode.freetookit.lang;

import org.junit.Test;

import java.util.function.Supplier;

/**
 * @author gaohang on 7/30/17.
 */
public class LazyProxyTest {
  @Test
  public void create_jdk() throws Exception {
    final TestService testService = LazyProxy.newInstance(TestService.class, new Supplier<TestService>() {
      @Override
      public TestService get() {
        return new TestServiceImpl();
      }
    });

    final String name = testService.name();
    System.out.println(name);

    System.out.println("call info");
    final TestBean info = testService.info();
    System.out.println("call info end");
    System.out.println(info.name());
    System.out.println(info);
  }

  @Test
  public void create_cglig() throws Exception {
    final TestService testService = LazyProxy.newInstance(TestServiceImpl.class, new Supplier<TestService>() {
      @Override
      public TestService get() {
        return new TestServiceImpl();
      }
    });

    final String name = testService.name();
    System.out.println(name);

    System.out.println("call info");
    final TestBean info = testService.info();
    System.out.println("call info end");
    System.out.println(info.name());
    System.out.println(info);
  }

}