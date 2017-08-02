package cn.yxffcode.freetookit.concurrent;

import java.util.concurrent.BlockingQueue;

/**
 * @author gaohang on 10/23/16.
 */
public final class Looper {

  private static final ThreadLocal<Looper> LOOPER_THREAD_LOCAL = new ThreadLocal<>();

  private final BlockingQueue<?> messageQueue;
  private final MessageHandler handler;

  private Looper(BlockingQueue<?> messageQueue, MessageHandler handler) {
    this.messageQueue = messageQueue;
    this.handler = handler;
  }

  public static void prepare(BlockingQueue<?> messageQueue, MessageHandler handler) {
    if (LOOPER_THREAD_LOCAL.get() != null) {
      throw new IllegalStateException("The Looper has already been prepared.");
    }

    final Looper looper = new Looper(messageQueue, handler);
    LOOPER_THREAD_LOCAL.set(looper);
  }

  public static Looper myLopper() {
    return LOOPER_THREAD_LOCAL.get();
  }

  public static void loop() {
    final Looper looper = myLopper();
    if (looper == null) {
      throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
    }
    final BlockingQueue<?> queue = looper.messageQueue;
    for (; ; ) {
      final Object message = queue.poll();
      if (message == LooperController.STOP) {
        break;
      }
      looper.handler.process(message);
    }

  }
}
