package org.soak.plugin.loader.common;

import org.soak.plugin.SoakPluginContainer;
import org.soak.plugin.loader.vanilla.VanillaPluginInjector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface SoakPluginInjector {

    static void injectPlugin(SoakPluginContainer container) {
        try {
            VanillaPluginInjector.injectPluginToPlatform(container);
        } catch (NoSuchMethodException e) {
            //load neo forge
            try {
                injectPluginToPlatform("org.soak.plugin.loader.neo.NeoPluginInjector", container);
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void injectPluginToPlatform(String className, SoakPluginContainer container) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var method = Class.forName(className).getDeclaredMethod("injectPluginToPlatform", SoakPluginContainer.class);
        method.invoke(null, container);
    }

    static void removePluginFromPlatform(String id) {
        try {
            VanillaPluginInjector.removePluginFromPlatform(id);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    static void removePluginFromPlatform(SoakPluginContainer container) {
        try {
            VanillaPluginInjector.removePluginFromPlatform(container);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }


}
