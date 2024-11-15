package org.soak.plugin;

import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.metadata.PluginMetadata;

import java.net.URI;
import java.util.Optional;

public class InternalSoakPluginContainer implements SoakPluginContainer {

    private final AbstractSpongePluginMain main;

    InternalSoakPluginContainer(AbstractSpongePluginMain main) {
        this.main = main;
    }

    @Override
    public @NotNull PluginContainer getTrueContainer() {
        return this.main.getOwnContainer();
    }

    @Override
    public @NotNull JavaPlugin getBukkitInstance() {
        return this.main.getPlugin();
    }

    @Override
    public PluginMetadata metadata() {
        return this.getTrueContainer().metadata();
    }

    @Override
    public Logger logger() {
        return this.main.getLogger();
    }

    @Override
    public @NotNull AbstractSpongePluginMain instance() {
        return this.main;
    }

    @Override
    public Optional<URI> locateResource(String relative) {
        return getTrueContainer().locateResource(relative);
    }
}
