package cn.yxffcode.easytookit.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gaohang on 15/9/11.
 */
public class NamedThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);

    private final ThreadGroup   group;
    private final AtomicInteger threadNumber;
    private final String        namePrefix;
    private final String        threadName;
    private final AtomicInteger threadSuffix;

    public NamedThreadFactory(String threadName) {
        this.threadName = threadName;
        this.threadSuffix = new AtomicInteger();
        this.threadNumber = new AtomicInteger(1);
        SecurityManager s = System.getSecurityManager();
        this.group = (s != null) ?
                     s.getThreadGroup() :
                     Thread.currentThread()
                           .getThreadGroup();
        this.namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group,
                              r,
                              namePrefix + threadNumber.getAndIncrement(),
                              0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        t.setName(threadName + threadSuffix.getAndIncrement());
        return t;
    }
}
