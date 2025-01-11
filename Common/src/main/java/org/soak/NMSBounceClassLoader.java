package org.soak;

import java.net.URL;
import java.net.URLClassLoader;

public class NMSBounceClassLoader extends URLClassLoader {

    public NMSBounceClassLoader(URL url) {
        super(new URL[]{url}, NMSBounceClassLoader.class.getClassLoader());
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        var clazz = super.findClass(name);
        return clazz;
    }
}
