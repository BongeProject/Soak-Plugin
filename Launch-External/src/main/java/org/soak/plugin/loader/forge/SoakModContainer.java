package org.soak.plugin.loader.forge;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.soak.plugin.SoakPluginContainer;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.metadata.PluginMetadata;

import java.net.URI;
import java.util.Optional;

public class SoakModContainer extends ModContainer implements SoakPluginContainer {

    private final SoakPluginContainer container;

    SoakModContainer(SoakPluginContainer container) {
        super(new SoakModInfo(container));
        this.contextExtension = () -> null; //gets the language config
        this.modLoadingStage = ModLoadingStage.COMMON_SETUP;
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
    public boolean matches(Object o) {
        System.out.println("Matches: " + o);
        return false;
    }

    @Override
    public Object getMod() {
        return this.instance();
    }
}
