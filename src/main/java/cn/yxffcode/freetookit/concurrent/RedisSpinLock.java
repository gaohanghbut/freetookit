package cn.yxffcode.freetookit.concurrent;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * 利用redis实现的分布式锁
 *
 * @author gaohang on 7/16/16.
 */
public class RedisSpinLock implements DistributeLock {
  public Builder newBuilder(JedisPool jedisPool, String lockName) {
    return new Builder(jedisPool, lockName);
  }

  private final JedisPool jedisPool;
  private final String lockName;
  private final String lockHolder;
  private final int expireSeconds;
  private final long lockFailedWaiting;
  private final String localhost;

  protected RedisSpinLock(JedisPool jedisPool, String lockName, int expireSeconds, long lockFailedWaiting) {
    this.jedisPool = jedisPool;
    this.lockName = lockName;
    this.expireSeconds = expireSeconds;
    this.lockFailedWaiting = lockFailedWaiting;
    this.lockHolder = lockName + ".holder";
    try {
      localhost = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void lock() {
    try {
      for (; ; ) {
        if (tryLock()) {
          return;
        }
        if (lockFailedWaiting > 0 && lockFailedWaiting < Long.MAX_VALUE) {
          TimeUnit.MILLISECONDS.sleep(lockFailedWaiting);
        }
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean tryLock() {
    Jedis jedis = jedisPool.getResource();
    Pipeline pipeline = null;
    try {
      //检查锁是否已经被自己持有,如果是,则返回true,表示可重入
      String holder = jedis.get(lockHolder);
      if (holder != null && lockHolder.equals(holder)) {
        return true;
      }
      Long locked = jedis.incr(lockName);
      if (locked == 1L) {
        //lock success
        pipeline = jedis.pipelined();
        pipeline.set(lockHolder, localhost);
        pipeline.expire(lockName, expireSeconds);
        pipeline.expire(lockHolder, expireSeconds);
        return true;
      }
    } finally {
      if (pipeline != null) {
        pipeline.sync();
      }
      jedisPool.returnResource(jedis);
    }
    return false;
  }

  @Override
  public void unlock() {
    Jedis jedis = jedisPool.getResource();
    try {
      jedis.del(lockName);
    } finally {
      jedisPool.returnResource(jedis);
    }
  }

  public static class Builder {
    private final JedisPool jedisPool;
    private final String lockName;
    private int expireSeconds;
    private long lockFailedWaiting;

    private Builder(JedisPool jedisPool, String lockName) {
      this.jedisPool = jedisPool;
      this.lockName = lockName;
    }

    public Builder setExpireSeconds(int expireSeconds) {
      this.expireSeconds = expireSeconds;
      return this;
    }

    public Builder setLockFailedWaiting(long lockFailedWaiting) {
      this.lockFailedWaiting = lockFailedWaiting;
      return this;
    }

    public RedisSpinLock create() {
      return new RedisSpinLock(jedisPool, lockName, expireSeconds, lockFailedWaiting);
    }
  }
}
