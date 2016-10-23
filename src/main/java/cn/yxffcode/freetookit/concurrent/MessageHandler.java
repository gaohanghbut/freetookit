package cn.yxffcode.freetookit.concurrent;

/**
 * @author gaohang on 10/23/16.
 */
public interface MessageHandler {
  void process(Object msg);
}
