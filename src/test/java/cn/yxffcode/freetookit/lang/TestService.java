package cn.yxffcode.freetookit.lang;

/**
 * @author gaohang on 7/30/17.
 */
public interface TestService {

  String name();

  @Lazy
  TestBean info();
}
