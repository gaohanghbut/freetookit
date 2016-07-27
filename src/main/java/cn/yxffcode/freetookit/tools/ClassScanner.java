package cn.yxffcode.freetookit.tools;

import com.google.common.base.Predicate;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

/**
 * 类扫描器，可以从一个包中扫描类
 *
 * @author gaohang on 15/7/29.
 */
public interface ClassScanner {

  List<Class<?>> doScan(String basePackage, @NotNull Predicate<Class<?>> predicate)
          throws IOException, ClassNotFoundException;
}
