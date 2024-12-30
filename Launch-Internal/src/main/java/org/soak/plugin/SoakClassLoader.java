package org.soak.plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.soak.plugin.paper.loader.FoundClassLoader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.LinkedTransferQueue;

public class SoakClassLoader extends URLClassLoader implements FoundClassLoader {

    private final Collection<Class<?>> classes = new LinkedTransferQueue<>();

    public SoakClassLoader(URL[] urls, ClassLoader loader) {
        super(urls, loader);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        var clazz = super.loadClass(name);
        this.classes.add(clazz);
        return clazz;
    }

    @Override
    public @NotNull @UnmodifiableView Collection<Class<?>> getClasses() {
        return Collections.unmodifiableCollection(this.classes);
    }
}
