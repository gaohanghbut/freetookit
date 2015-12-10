package cn.yxffcode.easytookit.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author gaohang on 15/9/27.
 */
public class ClasspathPropertiesConfiguration extends AbstractPropertiesConfiguration {

    private Properties config;

    private ClasspathPropertiesConfiguration() {
    }

    public static ClasspathPropertiesConfiguration create(String... resources) throws IOException {
        ClasspathPropertiesConfiguration cfg = new ClasspathPropertiesConfiguration();
        cfg.loadConfig(resources);
        return cfg;
    }

    private void loadConfig(String... resources) throws IOException {
        config = new Properties();

        for (String resource : resources) {
            try (InputStream fis = ClasspathPropertiesConfiguration.class.getResourceAsStream(resource)) {
                config.load(fis);
            }
        }
    }

    @Override
    protected Properties getConfig() {
        return config;
    }
}
