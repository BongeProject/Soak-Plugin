package org.soak.plugin.paper.loader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.InputStream;
import java.util.Collection;

public interface FoundClassLoader {

    Class<?> findClass(String name) throws ClassNotFoundException;

    Class<?> loadClass(String name) throws ClassNotFoundException;

    InputStream getResourceAsStream(String name);

    @NotNull
    @UnmodifiableView
    Collection<Class<?>> getClasses();
}
