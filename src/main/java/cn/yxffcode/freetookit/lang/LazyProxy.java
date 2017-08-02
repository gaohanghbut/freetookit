package cn.yxffcode.freetookit.lang;

import com.google.common.reflect.Reflection;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author gaohang on 7/30/17.
 */
public abstract class LazyProxy<T> {

  public static <T> T newInstance(Class<? extends T> type, Supplier<? extends T> targetSupplier) {
    checkNotNull(type);
    final LazyProxy<T> proxy = type.isInterface() ? new JdkLazyProxy<>(type, targetSupplier)
        : new CglibLazyProxy<>(type, targetSupplier);
    return proxy.create();
  }

  private final Class<? extends T> type;

  private final Supplier<? extends T> targetSupplier;

  private T target;

  protected LazyProxy(Class<? extends T> type, Supplier<? extends T> targetSupplier) {
    this.type = type;
    this.targetSupplier = targetSupplier;
  }

  public T create() {
    return doCreateProxy();
  }

  protected abstract T doCreateProxy();

  public Class<? extends T> getType() {
    return type;
  }

  protected Object call(Method serviceMethod, Object[] args) throws Throwable {
    //通过方法调用的参数创建代理对象
    final Class<?> returnType = serviceMethod.getReturnType();
    if (Modifier.isFinal(returnType.getModifiers()) || serviceMethod.getAnnotation(Lazy.class) == null) {
      //不能做lazy
      return serviceMethod.invoke(getTarget(), args);
    }
    if (returnType.isInterface()) {
      //jdk动态代理
      return Reflection.newProxy(returnType, new PojoProxy(serviceMethod));
    }
    //返回代理对象
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(returnType);
    // 回调方法
    enhancer.setCallback(new PojoProxy(serviceMethod));
    // 创建代理对象
    return enhancer.create();
  }

  private T getTarget() {
    if (target == null) {
      synchronized (this) {
        if (target == null) {
          target = targetSupplier.get();
        }
      }
    }
    return target;
  }

  private static final class JdkLazyProxy<T> extends LazyProxy<T> implements InvocationHandler {

    private JdkLazyProxy(Class<? extends T> type, Supplier<? extends T> targetSupplier) {
      super(type, targetSupplier);
    }

    @Override
    protected T doCreateProxy() {
      return Reflection.newProxy(getType(), this);
    }

    @Override
    public Object invoke(Object proxy, Method serviceMethod, Object[] args) throws Throwable {
      return call(serviceMethod, args);
    }
  }

  private static final class CglibLazyProxy<T> extends LazyProxy<T> implements MethodInterceptor {
    private CglibLazyProxy(Class<? extends T> type, Supplier<? extends T> targetSupplier) {
      super(type, targetSupplier);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T doCreateProxy() {
      Enhancer enhancer = new Enhancer();
      enhancer.setSuperclass(getType());
      // 回调方法
      enhancer.setCallback(this);
      // 创建代理对象
      return (T) enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method serviceMethod, Object[] args, MethodProxy methodProxy) throws Throwable {
      return call(serviceMethod, args);
    }
  }

  private final class PojoProxy implements MethodInterceptor, InvocationHandler {
    private Object pojoTarget;
    private final Method serviceMethod;

    private PojoProxy(Method serviceMethod) {
      this.serviceMethod = serviceMethod;
    }

    private Object call(Method method, Object[] args) throws Throwable {
      if (pojoTarget == null) {
        synchronized (this) {
          if (pojoTarget == null) {
            pojoTarget = serviceMethod.invoke(getTarget(), args);
          }
        }
      }
      return method.invoke(pojoTarget, args);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      return call(method, args);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
      return call(method, args);
    }
  }
}
