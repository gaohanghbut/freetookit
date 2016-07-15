package cn.yxffcode.freetookit.limit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标识一个方法,表示被标识的方法在同一时刻不能使用某些相同的参数做重复调用,参数由{@link #value()}指定
 *
 * @author gaohang on 7/12/16.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Unrepeatable {

  /**
   * 用于做重复调用判断的参数下标
   */
  int[] value() default {};
}
