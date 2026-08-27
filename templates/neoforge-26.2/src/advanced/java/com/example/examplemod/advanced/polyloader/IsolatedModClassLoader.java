package com.example.examplemod.advanced.polyloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public final class IsolatedModClassLoader extends URLClassLoader {
    private static final List<String> PARENT_FIRST = List.of(
            "java.",
            "javax.",
            "jdk.",
            "sun.",
            "com.example.examplemod.advanced.polyloader.");

    public IsolatedModClassLoader(URL jar, ClassLoader parent) {
        super(new URL[] {jar}, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> loaded = findLoadedClass(name);
            if (loaded == null) {
                if (parentFirst(name)) {
                    loaded = super.loadClass(name, false);
                } else {
                    try {
                        loaded = findClass(name);
                    } catch (ClassNotFoundException ignored) {
                        loaded = super.loadClass(name, false);
                    }
                }
            }
            if (resolve) {
                resolveClass(loaded);
            }
            return loaded;
        }
    }

    private static boolean parentFirst(String name) {
        for (String prefix : PARENT_FIRST) {
            if (name.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
