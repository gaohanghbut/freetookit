package cn.yxffcode.freetookit.limit;

/**
 * @author gaohang on 7/12/16.
 */
public interface InvocationContextHandler {

  /**
   * save the invocation context
   *
   * @return if success,return true. else return false.
   */
  boolean set(InvocationContext invocationContext);

  void remove(InvocationContext invocationContext);
}
