package org.soak.plugin.loader.common;

import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.plugin.SoakPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.metadata.model.PluginContributor;
import org.spongepowered.plugin.metadata.model.PluginDependency;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class SpongeJavaPlugin implements Plugin {

    private final PluginContainer container;

    public SpongeJavaPlugin(@NotNull PluginContainer container) {
        this.container = container;
    }

    public static PluginDescriptionFile createDescription(PluginContainer container) {
        String id = container.metadata().id();
        String name = container.metadata().name().orElse(id);
        List<String> providers = new LinkedList<>();
        String main = container.metadata().entrypoint();
        String classLoaderOf = "";
        List<String> depends = container
                .metadata()
                .dependencies()
                .stream()
                .filter(pluginDependency -> !pluginDependency.optional())
                .filter(pluginDependency -> pluginDependency.loadOrder() == PluginDependency.LoadOrder.AFTER)
                .map(PluginDependency::id)
                .toList();
        List<String> softDepends = container
                .metadata()
                .dependencies()
                .stream()
                .filter(PluginDependency::optional)
                .filter(pluginDependency -> pluginDependency.loadOrder() == PluginDependency.LoadOrder.AFTER)
                .map(PluginDependency::id)
                .toList();
        List<String> loadBefore = container
                .metadata()
                .dependencies()
                .stream()
                .filter(pluginDependency -> pluginDependency.loadOrder() == PluginDependency.LoadOrder.BEFORE)
                .map(PluginDependency::id)
                .toList();
        String version = container.metadata().version().toString();
        Map<String, Map<String, Object>> commands = new HashMap<>();
        String description = container.metadata().description().orElse(null);
        List<String> authors = container.metadata().contributors().stream().filter(pc -> pc.description().isPresent()).map(PluginContributor::name).toList();
        List<String> contributors = container.metadata().contributors().stream().filter(pc -> pc.description().isEmpty()).map(PluginContributor::name).toList();
        String website = container.metadata().links().homepage().map(URL::toString).orElse(null);
        String prefix = id;
        PluginLoadOrder order = PluginLoadOrder.STARTUP;
        List<Permission> permissions = new LinkedList<>();
        PermissionDefault permissionDefault = PermissionDefault.OP;
        Set<PluginAwareness> awareness = new HashSet<>();
        String apiVersion = SoakPlugin.server().getMinecraftVersion();
        List<String> libraries = new LinkedList<>();

        return new PluginDescriptionFile(id, name, providers, main, classLoaderOf, depends, softDepends, loadBefore, version, commands, description, authors, contributors, website, prefix, order, permissions, permissionDefault, awareness, apiVersion, libraries);
    }

    @Override
    public @NotNull File getDataFolder() {
        return Sponge.configManager().pluginConfig(this.container).directory().toFile();
    }

    @Override
    public @NotNull PluginDescriptionFile getDescription() {
        return createDescription(this.container);
    }

    @Override
    public @NotNull PluginMeta getPluginMeta() {
        return getDescription();
    }

    @Override
    public @NotNull FileConfiguration getConfig() {
        throw new RuntimeException("A plugin attempted to read a sponge plugins config .... why is it accessing another plugins config????");
    }

    @Override
    public @Nullable InputStream getResource(@NotNull String s) {
        return this.container.openResource(s).orElse(null);
    }

    @Override
    public void saveConfig() {

    }

    @Override
    public void saveDefaultConfig() {

    }

    @Override
    public void saveResource(@NotNull String s, boolean b) {

    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public @NotNull PluginLoader getPluginLoader() {
        throw new RuntimeException("A Bukkit plugin attempted to access a Sponge's plugin loader");
    }

    @Override
    public @NotNull Server getServer() {
        return SoakPlugin.server();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public boolean isNaggable() {
        return false;
    }

    @Override
    public void setNaggable(boolean b) {

    }

    @Override
    public @Nullable ChunkGenerator getDefaultWorldGenerator(@NotNull String s, @Nullable String s1) {
        return null;
    }

    @Override
    public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull String s, @Nullable String s1) {
        return null;
    }

    @Override
    public java.util.logging.@NotNull Logger getLogger() {
        throw new RuntimeException("A bukkit plugin attempted to access a Sponge plugins logger");
    }

    @Override
    public @NotNull String getName() {
        return this.getDescription().getName();
    }

    @Override
    public @NotNull LifecycleEventManager<Plugin> getLifecycleManager() {
        throw NotImplementedException.createByLazy(JavaPlugin.class, "getLifecycleManager");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return Collections.emptyList();
    }
}
