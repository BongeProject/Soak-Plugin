package org.soak.plugin.paper.meta;

import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginAwareness;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoadOrder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.plugin.SoakManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SoakPluginMeta implements PluginMeta {

    private final @NotNull String name;
    private final @NotNull String main;
    private final @NotNull PluginLoadOrder loadOrder;
    private final @NotNull String version;
    private final @Nullable String prefix;
    private final @Nullable String description;
    private final @Nullable String apiVersion;
    private final @Nullable String website;
    private final @NotNull Map<String, DependType> pluginDepends = new ConcurrentHashMap<>();
    private final @NotNull List<Permission> permissions = new ArrayList<>();
    private final @NotNull List<String> libraries = new ArrayList<>();

    public SoakPluginMeta(@NotNull SoakPluginMetaBuilder builder) {
        this.name = Objects.requireNonNull(builder.getName(), "'setName' must be used");
        this.main = Objects.requireNonNull(builder.getMain(), "'setMain' must be used");
        this.loadOrder = Objects.requireNonNull(builder.getLoadOrder(), "'setLoadOrder' cannot be null");
        this.version = Objects.requireNonNull(builder.getVersion(), "'setVersion' must be used");
        this.prefix = builder.getPrefix();
        this.description = builder.getDescription();
        this.apiVersion = builder.getApiVersion();
        this.website = builder.getWebsite();
        pluginDepends.putAll(builder.getPluginDepends());
        permissions.addAll(builder.getPermissions());
        this.libraries.addAll(builder.getLibraries());
    }

    public List<String> getLibraries(){
        return Collections.unmodifiableList(this.libraries);
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getMainClass() {
        return this.main;
    }

    @Override
    public @NotNull PluginLoadOrder getLoadOrder() {
        return this.loadOrder;
    }

    @Override
    public @NotNull String getVersion() {
        return this.version;
    }

    @Override
    public @Nullable String getLoggerPrefix() {
        return this.prefix;
    }

    private List<String> getPlugins(DependType type) {
        return this.pluginDepends.entrySet().stream().filter(entry -> entry.getValue().equals(type)).map(Map.Entry::getKey).toList();
    }

    @Override
    public @NotNull List<String> getPluginDependencies() {
        return getPlugins(DependType.LOAD_AFTER);
    }

    @Override
    public @NotNull List<String> getPluginSoftDependencies() {
        return getPlugins(DependType.SOFT_LOAD_AFTER);
    }

    @Override
    public @NotNull List<String> getLoadBeforePlugins() {
        return getPlugins(DependType.SOFT_LOAD_BEFORE);
    }

    @Override
    public @NotNull List<String> getProvidedPlugins() {
        return getPlugins(DependType.INCLUDED);
    }

    @Override
    public @NotNull List<String> getAuthors() {
        return null;
    }

    @Override
    public @NotNull List<String> getContributors() {
        return null;
    }

    @Override
    public @Nullable String getDescription() {
        return this.description;
    }

    @Override
    public @Nullable String getWebsite() {
        return this.website;
    }

    @Override
    public @NotNull List<Permission> getPermissions() {
        return this.permissions;
    }

    @Override
    public @NotNull PermissionDefault getPermissionDefault() {
        return PermissionDefault.FALSE;
    }

    @Override
    public @Nullable String getAPIVersion() {
        return this.apiVersion;
    }

    public PluginDescriptionFile toDescription() {
        String id = this.getName().toLowerCase().replaceAll(" ", "_");
        String name = this.getName();
        List<String> providers = new LinkedList<>();
        String main = this.getMainClass();
        String classLoaderOf = "";
        List<String> depends = this.getPluginDependencies();
        List<String> softDepends = this.getPluginSoftDependencies();
        List<String> loadBefore = this.getLoadBeforePlugins();
        String version = this.getVersion();
        Map<String, Map<String, Object>> commands = new HashMap<>();
        String description = this.getDescription();
        List<String> authors = this.getAuthors();
        List<String> contributors = this.getContributors();
        String website = this.getWebsite();
        String prefix = id;
        PluginLoadOrder order = PluginLoadOrder.STARTUP;
        List<Permission> permissions = new LinkedList<>();
        PermissionDefault permissionDefault = PermissionDefault.OP;
        Set<PluginAwareness> awareness = new HashSet<>();
        String apiVersion = SoakManager.getManager().getMinecraftVersion();
        List<String> libraries = new LinkedList<>();

        return new PluginDescriptionFile(id, name, providers, main, classLoaderOf, depends, softDepends, loadBefore, version, commands, description, authors, contributors, website, prefix, order, permissions, permissionDefault, awareness, apiVersion, libraries);

    }
}
