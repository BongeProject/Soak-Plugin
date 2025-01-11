package org.soak.plugin.loader.common;

import org.soak.plugin.SoakPluginContainer;
import org.soak.plugin.loader.vanilla.VanillaPluginInjector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

public interface SoakPluginInjector {

    static void injectPlugins(Collection<SoakPluginContainer> containers) {
        try {
            for (var container : containers) {
                VanillaPluginInjector.injectPluginToPlatform(container);
            }
        } catch (NoSuchMethodException e) {
            //load neo forge
            try {
                injectPluginToPlatform("org.soak.plugin.loader.neo.NeoPluginInjector", containers);
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void injectPluginToPlatform(String className, Collection<SoakPluginContainer> containers) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var method = Class.forName(className).getDeclaredMethod("injectPluginToPlatform", Collection.class);
        method.invoke(null, containers);
    }

    static void removePluginFromPlatform(String id) {
        try {
            VanillaPluginInjector.removePluginFromPlatform(id);
        } catch (IllegalAccessException | NoSuchFieldException e) {
        }
    }

    static void removePluginFromPlatform(SoakPluginContainer container) {
        try {
            VanillaPluginInjector.removePluginFromPlatform(container);
        } catch (IllegalAccessException | NoSuchFieldException e) {
        }
    }


}
