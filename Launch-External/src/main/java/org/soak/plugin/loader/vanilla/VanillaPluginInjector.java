package org.soak.plugin.loader.vanilla;

import org.jetbrains.annotations.NotNull;
import org.soak.plugin.SoakPluginContainer;
import org.soak.utils.ReflectionHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.plugin.PluginContainer;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class VanillaPluginInjector {
    public static void injectPluginToPlatform(SoakPluginContainer container) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        PluginManager manager = Sponge.pluginManager();
        ReflectionHelper.runMethod(manager, "addPlugin", container);
    }

    public static void removePluginFromPlatform(@NotNull String id) throws NoSuchFieldException, IllegalAccessException {
        removePluginFromPlatform(Sponge.pluginManager().plugin(id).map(container -> (SoakPluginContainer) container).orElseThrow(() -> new RuntimeException("Id is not a soak plugin")));
    }

    public static void removePluginFromPlatform(@NotNull SoakPluginContainer container) throws NoSuchFieldException, IllegalAccessException {
        PluginManager manager = Sponge.pluginManager();
        Map<String, PluginContainer> plugins = ReflectionHelper.getField(manager, "plugins");
        List<PluginContainer> sortedPlugins = ReflectionHelper.getField(manager, "sortedPlugins");
        Map<Object, PluginContainer> instanceToPlugins = ReflectionHelper.getField(manager, "instancesToPlugins");

        var pluginsToRemove = plugins.entrySet().stream().filter(entry -> entry.getValue().equals(container)).map(Map.Entry::getKey).toList();
        if (pluginsToRemove.isEmpty()) {
            throw new RuntimeException("Could not find soak plugin from 'plugins'");
        }
        for (var pluginToRemove : pluginsToRemove) {
            int previousSize = plugins.size();
            plugins.remove(pluginToRemove);
            if (plugins.size() == previousSize) {
                throw new RuntimeException("Could not remove soak plugin from 'plugins'");
            }
        }

        if (!sortedPlugins.remove(container)) {
            throw new RuntimeException("Could not remove soak plugin from 'sortedPlugins'");
        }

        var instancesToRemove = instanceToPlugins.entrySet().stream().filter(entry -> entry.getValue().equals(container)).map(Map.Entry::getKey).toList();
        if (instancesToRemove.isEmpty()) {
            throw new RuntimeException("Could not find soak plugin from 'instancesToPlugins'");
        }
        for (var instance : instancesToRemove) {
            var previousSize = instanceToPlugins.size();
            instanceToPlugins.remove(instance);
            if (previousSize == instanceToPlugins.size()) {
                throw new RuntimeException("Could not remove soak plugin from 'instancesToPlugins'");
            }
        }

    }
}
