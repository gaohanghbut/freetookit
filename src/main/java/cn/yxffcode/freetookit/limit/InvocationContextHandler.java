package cn.yxffcode.freetookit.limit;

/**
 * @author gaohang on 7/12/16.
 */
public interface InvocationContextHandler {

  /**
   * save the invocation context,if failed,it represent that the method is called repeat at one time
   *
   * @return if success,return true. else return false.
   */
  boolean set(InvocationContext invocationContext);

  void remove(InvocationContext invocationContext);
}
