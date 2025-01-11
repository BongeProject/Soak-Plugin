//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.bukkit.plugin.java;

import com.destroystokyo.paper.utils.PaperPluginLogger;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import net.kyori.adventure.util.Services;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginBase;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.plugin.SoakManager;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class JavaPlugin extends PluginBase {
    private boolean isEnabled = false;
    private PluginLoader loader = null;
    private Server server = null;
    private File file = null;
    private PluginDescriptionFile description = null;
    private PluginMeta pluginMeta = null;
    private File dataFolder = null;
    private ClassLoader classLoader = null;
    private boolean naggable = true;
    private FileConfiguration newConfig = null;
    private File configFile = null;
    private Logger logger = null;

    public JavaPlugin() {
        ClassLoader var2 = this.getClass().getClassLoader();
        if (var2 instanceof ConfiguredPluginClassLoader configuredPluginClassLoader) {
            configuredPluginClassLoader.init(this);
        }
    }

    /**
     * @deprecated
     */
    @Deprecated(
            forRemoval = true
    )
    protected JavaPlugin(@NotNull JavaPluginLoader loader, @NotNull PluginDescriptionFile description, @NotNull File dataFolder, @NotNull File file) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        if (classLoader instanceof PluginClassLoader) {
            throw new IllegalStateException("Cannot use initialization constructor at runtime");
        } else {
            this.init(loader, loader.server, description, dataFolder, file, classLoader);
        }
    }

    public static <T extends JavaPlugin> @NotNull T getPlugin(@NotNull Class<T> clazz) {
        Preconditions.checkArgument(clazz != null, "Null class cannot have a plugin");
        if (!JavaPlugin.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("" + clazz + " does not extend " + JavaPlugin.class);
        } else {
            ClassLoader cl = clazz.getClassLoader();
            if (cl instanceof ConfiguredPluginClassLoader) {
                ConfiguredPluginClassLoader configuredPluginClassLoader = (ConfiguredPluginClassLoader) cl;
                JavaPlugin plugin = configuredPluginClassLoader.getPlugin();
                if (plugin == null) {
                    throw new IllegalStateException("Cannot get plugin for " + clazz + " from a static initializer");
                } else {
                    return clazz.cast(plugin);
                }
            } else {
                return (T) SoakManager.getManager().getBukkitSoakContainers().findAny().orElseThrow(() -> new RuntimeException("Cannot get main SoakPluginContainer")).getBukkitInstance();
            }
        }
    }

    public static @NotNull JavaPlugin getProvidingPlugin(@NotNull Class<?> clazz) {
        Preconditions.checkArgument(clazz != null, "Null class cannot have a plugin");
        ClassLoader cl = clazz.getClassLoader();
        if (cl instanceof ConfiguredPluginClassLoader configuredPluginClassLoader) {
            JavaPlugin plugin = configuredPluginClassLoader.getPlugin();
            return plugin;
        } else {
            return SoakManager.getManager().getBukkitSoakContainers().findAny().orElseThrow(() -> new RuntimeException("Cannot get main SoakPluginContainer")).getBukkitInstance();
        }
    }

    public final @NotNull File getDataFolder() {
        return this.dataFolder;
    }

    /**
     * @deprecated
     */
    @Deprecated(
            forRemoval = true
    )
    public final @NotNull PluginLoader getPluginLoader() {
        return this.loader;
    }

    public final @NotNull Server getServer() {
        return this.server;
    }

    public final boolean isEnabled() {
        return this.isEnabled;
    }

    @Internal
    public final void setEnabled(boolean enabled) {
        if (this.isEnabled != enabled) {
            this.isEnabled = enabled;
            if (this.isEnabled) {
                this.onEnable();
            } else {
                this.onDisable();
            }
        }

    }

    protected @NotNull File getFile() {
        return this.file;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public final @NotNull PluginDescriptionFile getDescription() {
        return this.description;
    }

    public final @NotNull PluginMeta getPluginMeta() {
        return this.pluginMeta;
    }

    public @NotNull FileConfiguration getConfig() {
        if (this.newConfig == null) {
            this.reloadConfig();
        }

        return this.newConfig;
    }

    protected final @Nullable Reader getTextResource(@NotNull String file) {
        InputStream in = this.getResource(file);
        return in == null ? null : new InputStreamReader(in, Charsets.UTF_8);
    }

    public void reloadConfig() {
        this.newConfig = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream defConfigStream = this.getResource("config.yml");
        if (defConfigStream != null) {
            this.newConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
        }
    }

    public void saveConfig() {
        try {
            this.getConfig().save(this.configFile);
        } catch (IOException var2) {
            IOException ex = var2;
            this.logger.log(Level.SEVERE, "Could not save config to " + this.configFile, ex);
        }

    }

    public void saveDefaultConfig() {
        if (!this.configFile.exists()) {
            this.saveResource("config.yml", false);
        }

    }

    public void saveResource(@NotNull String resourcePath, boolean replace) {
        if (resourcePath != null && !resourcePath.equals("")) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = this.getResource(resourcePath);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + this.file);
            } else {
                File outFile = new File(this.dataFolder, resourcePath);
                int lastIndex = resourcePath.lastIndexOf(47);
                File outDir = new File(this.dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (outFile.exists() && !replace) {
                        Logger var10000 = this.logger;
                        Level var10001 = Level.WARNING;
                        String var10002 = outFile.getName();
                        var10000.log(var10001, "Could not save " + var10002 + " to " + outFile + " because " + outFile.getName() + " already exists.");
                    } else {
                        OutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[1024];

                        int len;
                        while ((len = in.read(buf)) > 0) {
                            ((OutputStream) out).write(buf, 0, len);
                        }

                        ((OutputStream) out).close();
                        in.close();
                    }
                } catch (IOException var10) {
                    IOException ex = var10;
                    this.logger.log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
                }

            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }

    public @Nullable InputStream getResource(@NotNull String filename) {
        try {
            URL url = this.getClassLoader().getResource(filename);
            if (url == null) {
                return null;
            } else {
                URLConnection connection = url.openConnection();
                connection.setUseCaches(false);
                return connection.getInputStream();
            }
        } catch (IOException var4) {
            return null;
        }
    }

    protected final @NotNull ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public final void init(@NotNull PluginLoader loader, @NotNull Server server, @NotNull PluginDescriptionFile description, @NotNull File dataFolder, @NotNull File file, @NotNull ClassLoader classLoader) {
        this.init(server, description, dataFolder, file, classLoader, description, PaperPluginLogger.getLogger(description));
        this.pluginMeta = description;
    }

    public final void init(@NotNull Server server, @NotNull PluginDescriptionFile description, @NotNull File dataFolder, @NotNull File file, @NotNull ClassLoader classLoader, @Nullable PluginMeta configuration, @NotNull Logger logger) {
        this.loader = JavaPlugin.DummyPluginLoaderImplHolder.INSTANCE;
        this.server = server;
        this.file = file;
        this.description = description;
        this.dataFolder = dataFolder;
        this.classLoader = classLoader;
        this.configFile = new File(dataFolder, "config.yml");
        this.pluginMeta = configuration;
        this.logger = logger;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return false;
    }

    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

    public @Nullable PluginCommand getCommand(@NotNull String name) {
        String alias = name.toLowerCase(Locale.ENGLISH);
        PluginCommand command = this.getServer().getPluginCommand(alias);
        if (command == null || command.getPlugin() != this) {
            Server var10000 = this.getServer();
            String var10001 = this.description.getName().toLowerCase(Locale.ENGLISH);
            command = var10000.getPluginCommand(var10001 + ":" + alias);
        }

        return command != null && command.getPlugin() == this ? command : null;
    }

    public void onLoad() {
    }

    public void onDisable() {
    }

    public void onEnable() {
    }

    public @Nullable ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, @Nullable String id) {
        return null;
    }

    public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull String worldName, @Nullable String id) {
        return null;
    }

    public final boolean isNaggable() {
        return this.naggable;
    }

    public final void setNaggable(boolean canNag) {
        this.naggable = canNag;
    }

    public @NotNull Logger getLogger() {
        return this.logger;
    }

    public @NotNull String toString() {
        return this.description.getFullName();
    }

    private static class DummyPluginLoaderImplHolder {
        private static final PluginLoader INSTANCE = (PluginLoader) Services.service(PluginLoader.class).orElseThrow();

        private DummyPluginLoaderImplHolder() {
        }
    }
}
