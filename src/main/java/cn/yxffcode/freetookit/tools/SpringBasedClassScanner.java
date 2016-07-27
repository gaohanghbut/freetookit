package cn.yxffcode.freetookit.tools;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * 默认实现的类扫描器，使用Spring
 *
 * @author gaohang on 15/7/29.
 * @see ResourcePatternResolver
 */
public class SpringBasedClassScanner implements ClassScanner {

  private static final String CLASS_PATTERN = "**/*.class";
  private static final Pattern PACKAGE_TO_DIRECTORY = Pattern.compile("\\.");

  private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

  private SpringBasedClassScanner() {
  }

  public static SpringBasedClassScanner getInstance() {
    return Holder.INSTANCE;
  }

  @Override
  public List<Class<?>> doScan(@NotNull String basePackage, @NotNull Predicate<Class<?>> predicate)
          throws IOException, ClassNotFoundException {

    checkArgument(isNotBlank(basePackage));
    checkNotNull(predicate);

    String baseUrl = resolveBasePackage(basePackage);
    String resourcePattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + baseUrl + "/" + CLASS_PATTERN;

    Resource[] resources = resourcePatternResolver.getResources(resourcePattern);

    List<Class<?>> classes = Lists.newArrayList();
    for (Resource resource : resources) {
      if (!resource.isReadable()) {
        continue;
      }
      int index = resource.getFilename().lastIndexOf('.');
      String simpleClassName = resource.getFilename().substring(0, index);
      String className = basePackage + "." + simpleClassName;
      Class<?> markedClass = Thread.currentThread().getContextClassLoader().loadClass(className);
      if (predicate.apply(markedClass)) {
        classes.add(markedClass);
      }
    }

    return classes;
  }

  private String resolveBasePackage(String basePackage) {
    return PACKAGE_TO_DIRECTORY.matcher(basePackage).replaceAll("/");
  }

  private static final class Holder {
    private static final SpringBasedClassScanner INSTANCE = new SpringBasedClassScanner();
  }
}
