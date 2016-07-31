package cn.yxffcode.freetookit.concurrent;

import cn.yxffcode.freetookit.collection.MpscLinkedQueue;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author gaohang on 7/31/16.
 */
public class ThreadGroupExecutor extends AbstractExecutorService {

  private static final Logger LOGGER = Logger.getLogger(ThreadGroupExecutor.class.getName());

  private volatile boolean shutdownNow;
  private volatile boolean shutdown;

  private final Worker[] workers;
  private final AtomicInteger currentWorker;

  /**
   * 默认使用的线程数量为cpu核心数
   */
  public ThreadGroupExecutor() {
    this(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
  }

  /**
   * @param nthread 线程数量
   */
  public ThreadGroupExecutor(int nthread) {
    this(nthread, Executors.defaultThreadFactory());
  }

  /**
   * @param nthread       线程数量
   * @param threadFactory 线程工厂
   */
  public ThreadGroupExecutor(int nthread, ThreadFactory threadFactory) {
    checkNotNull(threadFactory);
    this.workers = new Worker[nthread];
    this.currentWorker = new AtomicInteger(0);

    //init threads
    for (int i = 0; i < workers.length; i++) {
      workers[i] = new Worker();
      threadFactory.newThread(workers[i]).start();
    }
  }

  @Override
  public void shutdown() {
    this.shutdown = true;
  }

  @Override
  public List<Runnable> shutdownNow() {
    this.shutdown = true;
    this.shutdownNow = true;

    List<Runnable> remains = Lists.newArrayList();
    for (Worker worker : workers) {
      remains.addAll(worker.tasks);
    }
    return remains;
  }

  @Override
  public boolean isShutdown() {
    return shutdown;
  }

  @Override
  public boolean isTerminated() {
    return shutdown;
  }

  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    long nanos = unit.toNanos(timeout);
    LockHolder.getSingleton().lock.lock();
    try {
      for (; ; ) {
        if (isTerminated()) {
          return true;
        } else if (nanos <= 0) {
          return false;
        } else {
          nanos = LockHolder.getSingleton().termination.awaitNanos(nanos);
        }
      }
    } finally {
      LockHolder.getSingleton().lock.unlock();
    }
  }

  @Override
  public void execute(Runnable command) {
    if (shutdown) {
      throw new RejectedExecutionException("executor had been shutdown");
    }
    checkNotNull(command);

    int idx = currentWorker.incrementAndGet();
    if (idx < 0) {
      idx -= Integer.MIN_VALUE;
    }
    workers[idx % workers.length].add(command);
  }

  private final class Worker implements Runnable {

    private MpscLinkedQueue<Runnable> tasks = new MpscLinkedQueue<>();

    public void add(Runnable task) {
      tasks.offer(task);
    }

    @Override
    public void run() {
      while (!shutdownNow) {
        Runnable task = tasks.poll();
        if (task == null && shutdown) {
          return;
        }
        if (task != null) {
          try {
            task.run();
          } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "task invoke failed", e);
          }
        }
      }
    }
  }

  private static final class LockHolder {

    private static volatile LockHolder INSTANCE;

    public static LockHolder getSingleton() {
      if (INSTANCE == null) {
        synchronized (LockHolder.class) {
          if (INSTANCE == null) {
            INSTANCE = new LockHolder();
          }
        }
      }
      return INSTANCE;
    }

    /**
     * 用于{@link #awaitTermination(long, TimeUnit)}方法的锁
     */
    private final Lock lock = new ReentrantLock();

    private final Condition termination = lock.newCondition();

  }
}
