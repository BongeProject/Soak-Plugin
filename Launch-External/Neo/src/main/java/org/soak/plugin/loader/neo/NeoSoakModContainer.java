package org.soak.plugin.loader.neo;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.plugin.SoakPluginContainer;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.metadata.PluginMetadata;

import java.net.URI;
import java.util.Optional;

public class NeoSoakModContainer extends ModContainer implements SoakPluginContainer {

    private final SoakPluginContainer container;

    NeoSoakModContainer(IModInfo modFile, SoakPluginContainer container) {
        super(modFile);
        this.container = container;
    }

    @Override
    public @NotNull PluginContainer getTrueContainer() {
        return this;
    }

    @Override
    public @NotNull JavaPlugin getBukkitInstance() {
        return this.container.getBukkitInstance();
    }

    @Override
    public PluginMetadata metadata() {
        return this.container.metadata();
    }

    @Override
    public Logger logger() {
        return this.container.logger();
    }

    @Override
    public @NotNull Object instance() {
        return this.container.instance();
    }

    @Override
    public Optional<URI> locateResource(String relative) {
        return this.container.locateResource(relative);
    }

    @Override
    public @Nullable IEventBus getEventBus() {
        return null;
    }
}
