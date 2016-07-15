package cn.yxffcode.freetookit.concurrent;

/**
 * 表示分布式锁的接口
 *
 * @author gaohang on 7/16/16.
 */
public interface DistributeLock {

  void lock();

  boolean tryLock();

  void unlock();
}
