package cn.yxffcode.freetookit.spi;

import cn.yxffcode.freetookit.concurrent.DCL;
import cn.yxffcode.freetookit.lang.Consumer;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static cn.yxffcode.freetookit.io.IOStreams.lines;
import static cn.yxffcode.freetookit.io.IOStreams.toBufferedReader;
import static cn.yxffcode.freetookit.utils.Reflections.defaultConstruct;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Collections.unmodifiableMap;

/**
 * Created by hang.gao on 2015/6/9.
 */
public final class ExtensionLoaders {
  private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> extensionLoaderMap =
          new ConcurrentHashMap<>();
  private static final ConcurrentMap<Class<?>, NamedExtensionLoader<?>> namedExtensionLoaderMap =
          new ConcurrentHashMap<>();
  private static final Object extensionMapLock = new Object();
  private static final Object namedExtensionMapLock = new Object();

  private ExtensionLoaders() {
  }

  public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
    if (!extensionLoaderMap.containsKey(type)) {
      synchronized (extensionMapLock) {
        if (!extensionLoaderMap.containsKey(type)) {
          extensionLoaderMap.put(type, new DefaultExtensionLoader<T>(type, Thread.currentThread()
                  .getContextClassLoader()));
        }
      }
    }
    return (ExtensionLoader<T>) extensionLoaderMap.get(type);
  }

  public static <T> NamedExtensionLoader<T> getNamedExtensionLoader(Class<T> type) {
    if (!namedExtensionLoaderMap.containsKey(type)) {
      synchronized (namedExtensionMapLock) {
        if (!namedExtensionLoaderMap.containsKey(type)) {
          namedExtensionLoaderMap.put(type, new DefaultNamedExtensionLoader<>(type,
                  Thread.currentThread().getContextClassLoader()));
        }
      }
    }
    return (NamedExtensionLoader<T>) namedExtensionLoaderMap.get(type);
  }

  private static final class DefaultExtensionLoader<T> implements ExtensionLoader<T> {

    private final ServiceLoader<T> serviceLoader;

    private volatile List<T> cache;

    public DefaultExtensionLoader(Class<T> type, ClassLoader classLoader) {
      serviceLoader = ServiceLoader.load(type, classLoader);
    }

    public List<T> getExtensions() {
      //            for jdk8:
      //            DCL.create()
      //               .check(v -> this.cache != null)
      //               .absent(v -> this.cache = doLoad())
      //               .done(null);
      DCL.create().check(new Predicate<Object>() {
        @Override
        public boolean apply(final Object input) {
          return cache != null;
        }
      }).absent(new Consumer<Object>() {
        @Override
        public void consume(final Object elem) {
          cache = doLoad();
        }
      }).done(null);
      return cache;
    }

    private List<T> doLoad() {
      List<T> services = new ArrayList<T>();
      for (T service : serviceLoader) {
        services.add(service);
      }
      return services;
    }

    public T getExtension() {
      return getExtensions().get(0);
    }
  }


  private static final class DefaultNamedExtensionLoader<T> implements NamedExtensionLoader<T> {
    private static final String PREFIX = "META-INF/services/";
    private static final Splitter NAMED_IMPL_SPLITTER = Splitter.on('=').trimResults();

    private final Map<String, T> cache;

    private DefaultNamedExtensionLoader(final Class<T> type, final ClassLoader classLoader) {
      HashMap<String, T> cache = Maps.newHashMap();
      try (BufferedReader in = toBufferedReader(
              classLoader.getSystemResourceAsStream(PREFIX + type
                      .getName()))) {
        for (String line : lines(in)) {
          List<String> extension = NAMED_IMPL_SPLITTER.splitToList(line);
          checkState(extension.size() == 2, "SPI描述文件格式错误,使用key=value的方式表示, %s", line);
          cache.put(extension.get(0), defaultConstruct(type));
        }
      } catch (IOException e) {
        Throwables.propagate(e);
      }
      this.cache = unmodifiableMap(cache);
    }


    @Override
    public Map<String, T> getExtensions() {
      return cache;
    }

    @Override
    public T getExtension(final String name) {
      return getExtensions().get(name);
    }
  }
}
