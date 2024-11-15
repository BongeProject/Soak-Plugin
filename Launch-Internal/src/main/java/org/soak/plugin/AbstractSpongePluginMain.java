package org.soak.plugin;

import io.papermc.paper.plugin.configuration.PluginMeta;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.Compatibility;
import org.soak.WrapperManager;
import org.soak.io.SoakServerProperties;
import org.soak.plugin.paper.loader.SoakPluginClassLoader;
import org.soak.plugin.paper.meta.SoakPluginMetaBuilder;
import org.soak.utils.SoakMemoryStore;
import org.soak.wrapper.v1_19_R4.NMSBounceSoakServer;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Function;
import java.util.logging.ConsoleHandler;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

public class AbstractSpongePluginMain implements SoakInternalManager, WrapperManager {

    private static final ArtifactVersion soakVersion = new DefaultArtifactVersion("0.0.1");
    final Collection<Command> commands = new LinkedTransferQueue<>();
    final InternalSoakPluginContainer soakPluginContainer = new InternalSoakPluginContainer(this);
    final PluginContainer container;
    private final Function<ClassLoader, Class<? extends JavaPlugin>> loaderToMain;
    private final String[] pathToPlugins;
    private final Logger logger;
    private final Compatibility compatibility;
    private final SoakServerProperties serverProperties = new SoakServerProperties();
    private final ConsoleHandler consoleHandler = new ConsoleHandler();
    private final SoakMemoryStore memoryStore = new SoakMemoryStore();
    @Nullable
    private URLClassLoader loader;

    @Nullable
    private JavaPlugin plugin;

    @Deprecated
    public AbstractSpongePluginMain(Function<ClassLoader, Class<? extends JavaPlugin>> loaderToMain, Logger logger, PluginContainer container) {
        this(loaderToMain, logger, container, new String[0]);
    }

    public AbstractSpongePluginMain(Function<ClassLoader, Class<? extends JavaPlugin>> loaderToMain, Logger logger, PluginContainer container, String... pathsToPlugins) {
        if (pathsToPlugins.length == 0) {
            throw new IllegalStateException("'PathsToPlugins' needs to be filled");
        }
        this.loaderToMain = loaderToMain;
        this.pathToPlugins = pathsToPlugins;
        this.logger = logger;
        this.container = container;
        this.compatibility = new Compatibility();
        GlobalSoakData.MANAGER_INSTANCE = this;
        Bukkit.setServer(new NMSBounceSoakServer(Sponge::server));
        Sponge.eventManager().registerListeners(container, new SoakWrapperListener(this));
    }

    @CheckReturnValue
    public static Function<ClassLoader, Class<? extends JavaPlugin>> fromName(@NotNull String name) {
        return (classLoader) -> {
            try {
                return (Class<? extends JavaPlugin>) Class.forName(name, true, classLoader);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
    }

    void loadPlugin() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<File> copiedFiles = new LinkedList<>();
        for (var path : this.pathToPlugins) {
            try {
                var inputStream = this.container.openResource(path).orElseThrow(() -> new RuntimeException("Could not find '" + path + "'"));
                var location = new File("temp/soak/" + path);
                location.deleteOnExit();
                location.getParentFile().mkdirs();
                Files.copy(inputStream, location.toPath(), StandardCopyOption.REPLACE_EXISTING);
                copiedFiles.add(location);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        loader = new URLClassLoader(copiedFiles.stream().map(file -> {
            try {
                var url = file.toURI().toURL();
                this.container.logger().info("Loading Bukkit file of '" + url.toString() + "'");
                return url;
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }).toArray(URL[]::new), AbstractSpongePluginMain.class.getClassLoader());

        for (var file : copiedFiles) {
            try (ZipFile zip = new ZipFile(file)) {
                var entries = zip.entries();
                while (entries.hasMoreElements()) {
                    var entry = entries.nextElement();
                    if (entry.isDirectory()) {
                        continue;
                    }
                    if (!entry.getName().endsWith(".class")) {
                        continue;
                    }
                    String name = entry.getName();
                    name = name.substring(0, name.length() - 6);
                    name = name.replaceAll("/", ".");
                    try {
                        loader.loadClass(name);
                    } catch (Throwable e) {
                        this.container.logger().warn("Could not load '" + name + "'", e);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Class<? extends JavaPlugin> javaPluginClass = this.loaderToMain.apply(loader);
        plugin = javaPluginClass.getConstructor().newInstance();
        SoakPluginClassLoader.setupPlugin(plugin, this.container.metadata().id(), getPluginFile(), new File(getPluginFolder(), "config.yml"), getPluginFolder(), this::getPluginMeta, () -> loader);
        this.commands.addAll(PluginCommandYamlParser.parse(plugin));
    }

    private File getPluginFile() {
        return new File("temp/soak/" + this.pathToPlugins[0]);

    }

    private PluginMeta getPluginMeta() {
        if (this.pathToPlugins.length == 1) {
            if (this.loader == null) {
                throw new RuntimeException("getPluginMeta was called before the classloader");
            }
            var inputStream = this.loader.getResourceAsStream("plugin.yml");
            if (inputStream == null) {
                inputStream = this.loader.getResourceAsStream("paper-plugin.yml");
            }
            if (inputStream != null) {
                try {
                    return new PluginDescriptionFile(inputStream);
                } catch (InvalidDescriptionException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return new SoakPluginMetaBuilder().from(this.container).setMain(getPlugin().getClass().getTypeName()).build();
    }

    private File getPluginFolder() {
        //correct path for sponge
        //return Sponge.configManager().pluginConfig(this.container).directory().toFile();

        //bukkits path -> plugins hard code this path
        File folder = new File("plugins/" + container.metadata().name().orElseGet(() -> container.metadata().id()));

        folder.mkdirs();

        return folder;
    }

    public JavaPlugin getPlugin() {
        if (this.plugin == null) {
            throw new IllegalStateException("getPlugin was called before the plugin was loaded");
        }
        return this.plugin;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public Stream<SoakPluginContainer> getBukkitContainers() {
        return Stream.of(this.soakPluginContainer);
    }

    @Override
    public PluginContainer getOwnContainer() {
        return this.container;
    }

    @Override
    public String getMinecraftVersion() {
        return Sponge.platform().minecraftVersion().toString();
    }

    @Override
    public SoakMemoryStore getMemoryStore() {
        return this.memoryStore;
    }

    @Override
    public SoakServerProperties getServerProperties() {
        return this.serverProperties;
    }

    @Override
    public Collection<Command> getBukkitCommands(Plugin plugin) {
        if (!plugin.equals(this.getPlugin())) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(this.commands);
    }

    @Override
    public ConsoleHandler getConsole() {
        return this.consoleHandler;
    }

    @Override
    public ArtifactVersion getVersion() {
        return soakVersion;
    }

    @Override
    public Compatibility getCompatibility() {
        return compatibility;
    }
}
