package cn.yxffcode.freetookit.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author gaohang on 15/9/26.
 */
public class GenericPropertiesConfiguration extends AbstractPropertiesConfiguration {

  private Properties config;

  private GenericPropertiesConfiguration() {
  }

  public static GenericPropertiesConfiguration create(InputStream... inputStreams)
          throws IOException {
    checkNotNull(inputStreams);
    GenericPropertiesConfiguration config = new GenericPropertiesConfiguration();
    config.init(inputStreams);
    return config;
  }

  private void init(InputStream... inputStreams) throws IOException {
    config = new Properties();
    for (InputStream inputStream : inputStreams) {
      config.load(inputStream);
    }
  }

  @Override protected Properties getConfig() {
    return config;
  }
}
