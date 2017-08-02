package cn.yxffcode.freetookit.lang;

import com.google.common.base.Throwables;

import java.util.concurrent.TimeUnit;

/**
 * @author gaohang on 7/30/17.
 */
public class TestServiceImpl implements TestService {
  @Override
  public String name() {
    return "name";
  }

  @Override
  public TestBean info() {
    System.out.println("wait");
    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException e) {
      Throwables.propagate(e);
    }
    return new TestBean();
  }
}
