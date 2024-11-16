package org.soak.plugin;

import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.bukkit.plugin.Plugin;
import org.soak.exception.NMSUsageException;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.ConsoleHandler;
import java.util.stream.Stream;

public interface SoakManager {

    public static <SM extends SoakManager> SM getManager() {
        return (SM) GlobalSoakData.MANAGER_INSTANCE;
    }

    public static <SM extends SoakManager, V> Optional<V> ifManager(Class<SM> clazz, Function<SM, V> function) {
        if (!clazz.isInstance(GlobalSoakData.MANAGER_INSTANCE)) {
            return Optional.empty();
        }
        return Optional.ofNullable(function.apply((SM) GlobalSoakData.MANAGER_INSTANCE));
    }

    Logger getLogger();

    Stream<SoakPluginContainer> getBukkitContainers();

    PluginContainer getOwnContainer();

    String getMinecraftVersion();

    ConsoleHandler getConsole();

    ArtifactVersion getVersion();

    default SoakPluginContainer getContainer(Plugin plugin) {
        return getBukkitContainers().filter(pc -> pc.getBukkitInstance().equals(plugin)).findAny().orElseThrow(() -> new RuntimeException("A plugin instance was created for " + plugin.getName() + " but no container could be found"));
    }

    default Optional<SoakPluginContainer> getContainer(PluginContainer plugin){
        return getBukkitContainers().filter(pc -> pc.equals(plugin) || pc.getTrueContainer().equals(plugin)).findAny();
    }

    default void displayError(Throwable e, File pluginFile) {
        displayError(e, Map.of("Plugin file", pluginFile.getPath()));
    }

    default void displayError(Throwable e, Plugin plugin, Map.Entry<String, String>... additions) {
        Map<String, String> pluginData = new HashMap<>();
        pluginData.put("Plugin name", plugin.getName());
        for (Map.Entry<String, String> entry : additions) {
            pluginData.put(entry.getKey(), entry.getValue());
        }

        displayError(e, pluginData);
    }

    private void displayError(Throwable e, Map<String, String> pluginData) {
        while (e instanceof InvocationTargetException) {
            e = ((InvocationTargetException) e).getTargetException();
        }
        Throwable readEx = e;
        while (true) {
            if (readEx instanceof InvocationTargetException ex) {
                readEx = ex.getTargetException();
                continue;
            }
            if (readEx instanceof ExceptionInInitializerError ex) {
                readEx = ex.getException();
            }
            if (readEx instanceof RuntimeException runtime) {
                var cause = runtime.getCause();
                if (cause instanceof InvocationTargetException || cause instanceof ExceptionInInitializerError) {
                    readEx = cause;
                    continue;
                }
            }
            break;
        }

        var logger = getLogger();

        logger.error("|------------------------|");
        pluginData.forEach((key, value) -> {
            logger.error("|- " + key + ": " + value);
        });
        logger.error("|- Soak version: " + this.getVersion().toString());
        logger.error("|- Minecraft version: " + Sponge.platform().minecraftVersion().name());
        logger.error("|- Sponge API version: " + Sponge.platform()
                .container(Platform.Component.API)
                .metadata()
                .version());

        if (readEx instanceof ClassCastException) {
            if (readEx.getMessage().contains("org.bukkit.plugin.SimplePluginManager")) {
                logger.error(
                        "|- Common Error Note: Starting on Paper hardfork 1.19.4, SimplePluginManager is being disconnected. This will not be added to soak");
            }
        }
        if (readEx instanceof NMSUsageException nmsUsage) {
            logger.error("|- Common Error Note: A plugin attempted to use NMS which is not supported by soak. This can only be fixed by the plugin developer");
            nmsUsage.getDeveloperNotes().ifPresent(message -> {
                logger.error("|- For Plugin Developer: " + message);
            });
        }
        if (readEx instanceof NoClassDefFoundError || readEx instanceof ClassCastException || readEx instanceof ClassNotFoundException) {
            if (e.getMessage().contains("net/minecraft/") || e.getMessage().contains("net.minecraft.")) {
                logger.error("|- Common Error Note: Error is caused due the crashing plugin using NMS. This is something Soak does not prioritise on fixing.");
                logger.error("   It is up to plugin devs to use the Spigot/Paper API to create a work around for when NMS cannot be used.");
            }
        }

        logger.error("|------------------------|");
        logger.error("Error", e);
    }

}
