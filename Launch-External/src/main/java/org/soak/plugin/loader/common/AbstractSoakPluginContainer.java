package org.soak.plugin.loader.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.soak.plugin.SoakPluginContainer;
import org.spongepowered.api.event.Order;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.metadata.PluginMetadata;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class AbstractSoakPluginContainer implements SoakPluginContainer {

    private final File bukkitPluginFile;
    private final SoakPluginMetadata pluginMetadata;
    //private final URLClassLoader loader;
    private final Logger logger;
    private final JavaPlugin plugin;
    private final SoakPluginWrapper mainInstance;

    public AbstractSoakPluginContainer(File bukkitPluginFile, JavaPlugin plugin, Order order) {
        this.bukkitPluginFile = bukkitPluginFile;
        this.plugin = plugin;
        this.logger = LogManager.getLogger(plugin.getName());
        this.pluginMetadata = SoakPluginMetadata.fromPlugin(plugin);

        //temp
        this.mainInstance = new SoakPluginWrapper(this, order);
    }

    public File getPluginFile() {
        return this.bukkitPluginFile;
    }

    @Override
    public PluginMetadata metadata() {
        return this.pluginMetadata;
    }

    @Override
    public Logger logger() {
        return this.logger;
    }

    @Override
    public @NotNull PluginContainer getTrueContainer() {
        return this;
    }

    @Override
    public @NotNull JavaPlugin getBukkitInstance() {
        return this.plugin;
    }

    @Override
    public SoakPluginWrapper instance() {
        return this.mainInstance;
    }

    @Override
    public Optional<URI> locateResource(String path) {
        boolean exists = this.plugin.getResource(path) != null;
        if (!exists) {
            return Optional.empty();
        }

        URI localPath = this.bukkitPluginFile.toURI();
        URI ret = null;
        try {
            ret = localPath.relativize(new URI(path));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(ret);
    }

    /*@Override
    public Optional<URI> locateResource(URI relative) {
        boolean exists = this.plugin.getResource(relative.getPath()) != null;
        if (!exists) {
            return Optional.empty();
        }

        URI localPath = this.bukkitPluginFile.toURI();
        URI ret = localPath.relativize(relative);
        return Optional.of(ret);
    }*/
}
