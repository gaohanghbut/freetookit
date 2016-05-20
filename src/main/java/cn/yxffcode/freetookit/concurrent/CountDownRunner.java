package cn.yxffcode.freetookit.concurrent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author gaohang on 16/2/10.
 */
public final class CountDownRunner {
  private final AtomicInteger count;
  private final Runnable task;
  private final AtomicBoolean finished = new AtomicBoolean(false);

  public CountDownRunner(int count, Runnable task) {
    checkArgument(count > 0);
    checkNotNull(task);
    this.task = task;
    this.count = new AtomicInteger(count);
  }

  public void countDown() {
    checkState(!finished.get());

    for (; ; ) {
      if (finished.get()) {
        return;
      }
      int cur = count.get();
      int v = cur - 1;

      boolean cas = count.compareAndSet(cur, v);
      if (cas) {
        if (v == 0) {
          task.run();
        }
        return;
      }
    }
  }
}
