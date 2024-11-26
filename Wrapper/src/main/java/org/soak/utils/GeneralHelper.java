package org.soak.utils;

import org.mosestream.lamda.ThrowableSupplier;
import org.soak.WrapperManager;
import org.soak.plugin.SoakInternalManager;
import org.soak.plugin.SoakManager;
import org.spongepowered.plugin.PluginContainer;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class GeneralHelper {

    public static <T> T simpleTry(ThrowableSupplier<T, Throwable> supplier) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            throw new RuntimeException("Read error below", e);
        }
    }

    //sometimes Sponge requires a plugin for tracking ... while bukkit doesn't. This is a janky way of getting the plugin
    //ideally use other ways to grab the plugin
    public static PluginContainer fromStackTrace() {
        var soakManager = SoakManager.<WrapperManager>getManager();
        if (soakManager instanceof SoakInternalManager im) {
            return im.getOwnContainer();
        }
        return Arrays.stream(Thread.currentThread().getStackTrace())
                .map(StackTraceElement::getClassName)
                .filter(className -> !className.startsWith("org.soak"))
                .filter(className -> !className.startsWith("org.bukkit"))
                .filter(className -> !className.startsWith("io.papermc"))
                .filter(className -> !className.startsWith("java.lang"))
                .map(className -> SoakManager.getManager().getBukkitSoakContainers().map(soakPluginContainer -> {
                            var context = soakManager.getServer().getSoakPluginManager().getContext(soakPluginContainer.getBukkitInstance());
                            var classLoader = context.loader();
                            try {
                                Class.forName(className, false, classLoader);
                                return soakPluginContainer;
                            } catch (ClassNotFoundException e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull).findAny()
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny()
                .orElseThrow(() -> new RuntimeException("could not find the plugin from the stacktrace"));
    }
}
