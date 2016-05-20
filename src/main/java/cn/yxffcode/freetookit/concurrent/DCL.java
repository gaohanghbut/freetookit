package cn.yxffcode.freetookit.concurrent;

import cn.yxffcode.freetookit.lang.Consumer;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 用于替代双重校验锁的代码块
 * 使用双重校验锁:
 * <pre>
 *   {@code
 *      private Map<String, Object> cache = xxx;
 *      public Object getInstance(String key) {
 *        if (!cache.containsKey(key)) {
 *          synchronized(this) {
 *            if (!cache.containsKey(key)) {
 *              //create object and put to cache
 *              xxx
 *            }
 *          }
 *        }
 *      }
 *   }
 * </pre>
 * 使用DCL类,代码将替换成:
 * <pre>
 *     {@code
 *        private Map<String, Object> cache = xxx;
 *        private DCL<String> dcl = DCL.<String>create().check(key -> cache.containsKey(key)).absent(key -> {//create Object and put to cache});
 *
 *        public Object getInstance(String key) {
 *          return dcl.done(key);
 *        }
 *     }
 * </pre>
 *
 * @author gaohang on 15/12/4.
 */
public class DCL<T> {

  private Predicate<T> checker = Predicates.alwaysTrue();
  private Consumer<T> consumer;

  private DCL() {
  }

  public static <T> DCL<T> create() {
    return new DCL<>();
  }

  public void done(T key) {
    if (!checker.apply(key)) {
      synchronized (this) {
        if (!checker.apply(key)) {
          consumer.consume(key);
        }
      }
    }
  }

  public DCL<T> check(Predicate<T> predicate) {
    this.checker = checkNotNull(predicate);
    return this;
  }

  public DCL<T> absent(Consumer<T> consumer) {
    this.consumer = consumer;
    return this;
  }
}
