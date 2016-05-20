package cn.yxffcode.freetookit.logqueue;

import java.io.Closeable;

/**
 * We have to record the failure image resize or image storage,
 *
 * @author gaohang on 15/9/11.
 */
public interface LogQueue<T> extends Closeable {

  /**
   * create a new log file to store logs
   */
  void rotate() throws RotateQueueException;

  /**
   * put an object to the log file
   */
  void offer(T obj);

  /**
   * consume an object from the log file
   */
  T poll();
}
