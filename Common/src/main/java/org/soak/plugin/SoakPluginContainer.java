package org.soak.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.soak.plugin.paper.meta.SoakPluginMeta;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.regex.Pattern;

public interface SoakPluginContainer extends PluginContainer {

    @NotNull PluginContainer getTrueContainer();

    @NotNull JavaPlugin getBukkitInstance();

    @NotNull Object instance();

    default void downloadLibraries() {
        Sponge.asyncScheduler().executor(getTrueContainer()).execute(() -> {
            var meta = (SoakPluginMeta) getBukkitInstance().getPluginMeta();
            var libraries = meta.getLibraries();
            libraries.forEach(library -> {
                String[] split = library.split(":", 3);
                String group = split[0].replaceAll(Pattern.quote("."), "/");
                String id = split[1];
                String version = split[2];

                String path = group + "/" + id + "/" + version + "/" + id + "-" + version + ".jar";
                File destination = new File("soakLibraries/" +path);
                if (destination.exists()) {
                    return;
                }

                String urlPath = "https://repo1.maven.org/maven2/" + path;
                getTrueContainer().logger().info("Downloading library from " + urlPath);
                try {
                    Files.createDirectories(destination.getParentFile().toPath());
                    var url = new URI(urlPath).toURL();
                    Files.copy(url.openStream(), destination.toPath());
                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }

            });
        });
    }
}
