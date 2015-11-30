package cn.yxffcode.easytookit.spi;

import io.netty.util.internal.chmv8.ConcurrentHashMapV8;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by hang.gao on 2015/6/9.
 */
public final class ExtensionLoaders {
    private ExtensionLoaders() {
    }

    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> extensionLoaderMap =
            new ConcurrentHashMapV8<Class<?>, ExtensionLoader<?>>();

    private static final Object extensionMapLock = new Object();

    public static <T>ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        if (!extensionLoaderMap.containsKey(type)) {
            synchronized (extensionMapLock) {
                if (!extensionLoaderMap.containsKey(type)) {
                    extensionLoaderMap.put(type,
                            new DefaultExtensionLoader<T>(type, Thread.currentThread().getContextClassLoader()));
                }
            }
        }
        return (ExtensionLoader<T>) extensionLoaderMap.get(type);
    }

    private static final class DefaultExtensionLoader<T> implements ExtensionLoader<T> {

        private final ServiceLoader<T> serviceLoader;

        private volatile List<T> cache;

        public DefaultExtensionLoader(Class<T> type, ClassLoader classLoader) {
            serviceLoader = ServiceLoader.load(type, classLoader);
        }

        public List<T> getExtensions() {
            if (cache == null) {
                synchronized (this) {
                    if (cache == null) {
                        List<T> services = new ArrayList<T>();
                        for (T service : serviceLoader) {
                            services.add(service);
                        }
                        cache = services;
                    }
                }
            }
            return cache;
        }

        public T getExtension() {
            return getExtensions().get(0);
        }
    }
}
