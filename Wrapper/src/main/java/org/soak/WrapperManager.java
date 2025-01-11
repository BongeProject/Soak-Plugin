package org.soak;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.soak.io.SoakServerProperties;
import org.soak.plugin.SoakManager;
import org.soak.utils.SoakMemoryStore;
import org.soak.wrapper.SoakServer;

import java.util.Collection;

public interface WrapperManager extends SoakManager {

    default SoakServer getServer() {
        return (SoakServer) Bukkit.getServer();
    }

    @Override
    default String getMinecraftVersion() {
        return getServer().getMinecraftVersion();
    }

    SoakMemoryStore getMemoryStore();

    SoakServerProperties getServerProperties();

    Collection<Command> getBukkitCommands(Plugin plugin);

    boolean shouldMaterialListUseModded();
}
