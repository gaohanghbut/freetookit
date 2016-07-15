package cn.yxffcode.freetookit.limit;

import cn.yxffcode.freetookit.collection.ConcurrentSet;
import com.google.common.reflect.AbstractInvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 防重复调用代理
 *
 * @author gaohang on 7/12/16.
 */
public final class UnrepeatableInvokeProxy extends AbstractInvocationHandler {

  private final InvocationContextHandler invocationContextHandler;
  private final Object target;

  private UnrepeatableInvokeProxy(Object target, InvocationContextHandler invocationContextHandler) {
    this.target = checkNotNull(target);
    this.invocationContextHandler = checkNotNull(invocationContextHandler);
  }

  @Override
  protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
    Unrepeatable unrepeatable = method.getAnnotation(Unrepeatable.class);
    if (unrepeatable == null) {
      return method.invoke(target, args);
    }
    int[] indexes = unrepeatable.value();
    InvocationContext invocationContext;
    if (indexes == null || indexes.length == 0) {
      invocationContext = new InvocationContext(method, Collections.emptyList());
    } else if (indexes.length == 1) {
      invocationContext = new InvocationContext(method, Collections.singletonList(args[indexes[0]]));
    } else {
      List<Object> params = new ArrayList<>(indexes.length);
      for (int index : indexes) {
        params.add(args[index]);
      }
      invocationContext = new InvocationContext(method, params);
    }
    if (!invocationContextHandler.set(invocationContext)) {
      throw new MultiInvocationException("method:" + method.toGenericString() + " can not be invokeed repeatable");
    }
    try {
      return method.invoke(target, args);
    } finally {
      invocationContextHandler.remove(invocationContext);
    }
  }

  public static Object getProxy(Object target, InvocationContextHandler invocationContextHandler) {
    return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
            new UnrepeatableInvokeProxy(target, invocationContextHandler));
  }

  public static Object getProxy(Object target) {
    return getProxy(target, new InvocationContextHandler() {
      private final ConcurrentSet<InvocationContext> contexts = ConcurrentSet.create();

      @Override
      public boolean set(InvocationContext invocationContext) {
        return contexts.addIfAbsent(invocationContext);
      }

      @Override
      public void remove(InvocationContext invocationContext) {
        contexts.remove(invocationContext);
      }
    });
  }
}
