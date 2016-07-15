package cn.yxffcode.freetookit.limit;

import com.google.common.base.Objects;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author gaohang on 7/12/16.
 */
public class InvocationContext {
  private Method method;
  private List<Object> args;

  public InvocationContext(Method method, List<Object> args) {
    this.method = method;
    this.args = args;
  }

  public InvocationContext() {
  }

  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  public List<Object> getArgs() {
    return args;
  }

  public void setArgs(List<Object> args) {
    this.args = args;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    InvocationContext that = (InvocationContext) o;
    return Objects.equal(method, that.method) &&
            Objects.equal(args, that.args);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(method, args);
  }
}
