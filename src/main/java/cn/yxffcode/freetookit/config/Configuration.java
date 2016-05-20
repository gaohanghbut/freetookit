package cn.yxffcode.freetookit.config;

import java.util.Iterator;

/**
 * @author gaohang on 15/9/26.
 */
public interface Configuration {
  String getString(String key);

  int getInt(String key);

  long getLong(String key);

  boolean getBoolean(String key);

  String getString(String key, String defaultValue);

  Iterator<?> propertyNames();

}
