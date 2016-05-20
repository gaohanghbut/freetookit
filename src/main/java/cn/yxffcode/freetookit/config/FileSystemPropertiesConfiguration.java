package cn.yxffcode.freetookit.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author gaohang on 15/9/26.
 */
public class FileSystemPropertiesConfiguration extends AbstractPropertiesConfiguration {

  private Properties config;

  private FileSystemPropertiesConfiguration() {
  }

  public static FileSystemPropertiesConfiguration create(String... resources) throws IOException {
    FileSystemPropertiesConfiguration cfg = new FileSystemPropertiesConfiguration();
    cfg.loadConfig(resources);
    return cfg;
  }

  private void loadConfig(String... resources) throws IOException {
    config = new Properties();

    for (String resource : resources) {
      try (FileInputStream fis = new FileInputStream(resource)) {
        config.load(fis);
      }
    }
  }

  @Override protected Properties getConfig() {
    return config;
  }
}
