package cn.yxffcode.easytookit.config;

import cn.yxffcode.easytookit.collection.IteratorAdapter;

import java.util.Iterator;
import java.util.Properties;

/**
 * @author gaohang on 15/9/26.
 */
public abstract class AbstractPropertiesConfiguration implements Configuration {
  public Properties getProperties() {
    return getConfig();
  }

  protected abstract Properties getConfig();

  @Override
  public String getString(String key) {
    return getConfig().getProperty(key);
  }

  @Override
  public int getInt(String key) {
    String property = getString(key);
    if (property == null) {
      return 0;
    }
    return Integer.parseInt(property);
  }

  @Override
  public long getLong(String key) {
    String value = getString(key);
    if (value == null) {
      return 0l;
    }
    return Long.parseLong(value);
  }

  @Override
  public boolean getBoolean(String key) {
    String value = getString(key);
    if (value == null) {
      return false;
    }
    return Boolean.parseBoolean(value);
  }

  @Override
  public String getString(String key,
                          String defaultValue
  ) {
    return getConfig().getProperty(key,
            defaultValue);
  }

  @Override
  public Iterator<?> propertyNames() {
    return IteratorAdapter.create(getConfig().propertyNames());
  }


}
