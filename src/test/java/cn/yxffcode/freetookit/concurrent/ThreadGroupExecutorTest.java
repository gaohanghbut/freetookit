package cn.yxffcode.freetookit.concurrent;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author gaohang on 7/31/16.
 */
public class ThreadGroupExecutorTest {
  @Test
  public void execute() throws Exception {
    ThreadGroupExecutor exec = new ThreadGroupExecutor(Runtime.getRuntime().availableProcessors());

    for (int i = 0; i < 100; i++) {
      exec.execute(new Runnable() {
        @Override
        public void run() {
          System.out.println(Thread.currentThread());
        }
      });
    }

    exec.shutdown();
    TimeUnit.SECONDS.sleep(5);
  }

  public static void main(String[] args) {
    ThreadGroupExecutor exec = new ThreadGroupExecutor(Runtime.getRuntime().availableProcessors());

    for (int i = 0; i < 100; i++) {
      exec.execute(new Runnable() {
        @Override
        public void run() {
          System.out.println(Thread.currentThread());
        }
      });
    }

    exec.shutdown();
  }
}