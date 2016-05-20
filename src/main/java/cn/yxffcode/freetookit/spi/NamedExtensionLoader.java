package cn.yxffcode.freetookit.spi;

import java.util.Map;

/**
 * @author gaohang on 15/12/4.
 */
public interface NamedExtensionLoader<T> {
  Map<String, T> getExtensions();

  T getExtension(String name);
}
