package org.soak.plugin;

import com.google.inject.Inject;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.jar.asm.MethodTooLargeException;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.mosestream.MoseStream;
import org.soak.NMSBounceLoader;
import org.soak.WrapperManager;
import org.soak.commands.soak.SoakCommand;
import org.soak.config.SoakConfiguration;
import org.soak.data.sponge.PortalCooldownCustomData;
import org.soak.data.sponge.SoakKeys;
import org.soak.fix.forge.ForgeFixCommons;
import org.soak.generate.bukkit.AttributeTypeList;
import org.soak.generate.bukkit.EntityTypeList;
import org.soak.generate.bukkit.MaterialList;
import org.soak.hook.event.HelpMapListener;
import org.soak.io.SoakServerProperties;
import org.soak.plugin.external.SoakConfig;
import org.soak.plugin.loader.Locator;
import org.soak.plugin.loader.common.AbstractSoakPluginContainer;
import org.soak.plugin.loader.common.SoakPluginInjector;
import org.soak.plugin.paper.loader.SoakPluginClassLoader;
import org.soak.utils.RegisterUtils;
import org.soak.utils.SoakMemoryStore;
import org.soak.utils.log.CustomLoggerFormat;
import org.soak.wrapper.SoakServer;
import org.soak.wrapper.plugin.SoakPluginManager;
import org.soak.wrapper.v1_21_R2.NMSBounceSoakServer;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataStore;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.*;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.metadata.model.PluginDependency;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.jar.JarFile;
import java.util.logging.ConsoleHandler;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@org.spongepowered.plugin.builtin.jvm.Plugin("soak")
public class SoakPlugin implements SoakExternalManager, WrapperManager {

    private static SoakPlugin plugin;
    public final Collection<Class<?>> generatedClasses = new LinkedBlockingQueue<>();
    private final SoakConfiguration configuration;
    private final PluginContainer container;
    private final Logger logger;
    private final SoakMemoryStore memoryStore = new SoakMemoryStore();
    private final SoakServerProperties serverProperties = new SoakServerProperties();
    private final ConsoleHandler consoleHandler = new ConsoleHandler();
    private final int generatedClassesCount = 3;
    private final Collection<SoakPluginContainer> loadedPlugins = new TreeSet<>((plugin, compare) -> {
        var opDepends = plugin.metadata().dependencies().stream().filter(p -> p.id().equals(compare.metadata().id())).findAny();
        if (opDepends.isPresent()) {
            Integer result = switch (opDepends.get().loadOrder()) {
                case PluginDependency.LoadOrder.AFTER -> 1;
                case PluginDependency.LoadOrder.BEFORE -> -1;
                default -> null;
            };
            if (result != null) {
                return result;
            }
        }
        opDepends = compare.metadata().dependencies().stream().filter(p -> p.id().equals(plugin.metadata().id())).findAny();
        if (opDepends.isPresent()) {
            Integer result = switch (opDepends.get().loadOrder()) {
                case PluginDependency.LoadOrder.AFTER -> 1;
                case PluginDependency.LoadOrder.BEFORE -> -1;
                default -> null;
            };
            if (result != null) {
                return result;
            }
        }
        return 0;
    });

    @Inject
    public SoakPlugin(PluginContainer pluginContainer, Logger logger) {
        plugin = this;
        GlobalSoakData.MANAGER_INSTANCE = this;
        this.container = pluginContainer;
        this.logger = logger;
        try {
            Path path = Sponge.configManager().pluginConfig(this.container).configPath();
            this.configuration = new SoakConfiguration(path.toFile());
            this.configuration.setDefaults(false);
            this.configuration.save();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    public static SoakPlugin plugin() {
        return plugin;
    }

    public static SoakServer server() {
        return (SoakServer) Bukkit.getServer();
    }

    private boolean hasDependency(Plugin plugin) {
        String id = plugin.getName().toLowerCase();
        return this
                .loadedPlugins
                .stream()
                .anyMatch(pluginContainer -> pluginContainer
                        .metadata()
                        .dependencies()
                        .stream()
                        .anyMatch(dependency -> dependency.id().equals(id)));
    }

    @Listener
    private void generateClasses(RegisterRegistryValueEvent.EngineScoped<Server> event) {
        String creatingClass = "Material";
        try {
            var classLoader = SoakPlugin.class.getClassLoader();

            var materialList = MaterialList.createMaterialList();
            MaterialList.LOADED_CLASS = materialList.load(classLoader, ClassLoadingStrategy.Default.INJECTION).getLoaded();
            generatedClasses.add(MaterialList.LOADED_CLASS);

            creatingClass = "EntityType";
            var entityTypeList = EntityTypeList.createEntityTypeList();
            EntityTypeList.LOADED_CLASS = entityTypeList.load(classLoader, ClassLoadingStrategy.Default.INJECTION).getLoaded();
            generatedClasses.add(EntityTypeList.LOADED_CLASS);

            creatingClass = "Attribute";
            var attributeList = AttributeTypeList.createEntityTypeList();
            AttributeTypeList.LOADED_CLASS = attributeList.load(classLoader, ClassLoadingStrategy.Default.INJECTION).getLoaded();
            generatedClasses.add(AttributeTypeList.LOADED_CLASS);
        } catch (MethodTooLargeException e) {
            throw new IllegalStateException("This is a problem with Bukkit's design: PaperMC seem to be making a fix with its hardfork: Too many entries in " + creatingClass, e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public Collection<Class<?>> generatedClasses() {
        return Collections.unmodifiableCollection(this.generatedClasses);
    }

    @Override
    public Stream<SoakPluginContainer> getBukkitSoakContainers() {
        return getPlugins();
    }

    @Override
    public Stream<PluginContainer> getBukkitPluginContainers() {
        return getPlugins().map(SoakPluginContainer::getTrueContainer);
    }

    @Override
    public PluginContainer getOwnContainer() {
        return this.container;
    }

    public ConsoleHandler getConsole() {
        return this.consoleHandler;
    }

    @Override
    public ArtifactVersion getVersion() {
        return this.container.metadata().version();
    }

    @Override
    public @NotNull SoakPluginClassLoader getSoakClassLoader(@NotNull SoakPluginContainer container) {
        var classLoader = SoakPlugin.server().getSoakPluginManager().getContext(container.getBukkitInstance()).loader();
        if (classLoader == null) {
            throw new IllegalStateException("SoakPluginContainer has not been registered with a classloader");
        }
        return classLoader;
    }

    public SoakMemoryStore getMemoryStore() {
        return this.memoryStore;
    }

    //try not using
    public SoakServerProperties getServerProperties() {
        return this.serverProperties;
    }

    @Override
    public Collection<org.bukkit.command.Command> getBukkitCommands(Plugin plugin) {
        var pluginContainer = getSoakContainer(plugin);
        if (!(pluginContainer instanceof AbstractSoakPluginContainer aspc)) {
            throw new IllegalStateException("Plugin expended to be extending AbstractSoakPluginContainer");
        }
        return aspc.instance().commands();
    }

    @Override
    public boolean shouldMaterialListUseModded() {
        return this.configuration.shouldMaterialListUseModded();
    }

    @Listener
    public void registerCommands(RegisterCommandEvent<Command.Parameterized> event) {
        event.register(this.container, SoakCommand.createSoakCommand(), "soak");
    }

    @Listener
    public void dataRegister(RegisterDataEvent event) {
        DataStore dataStore = DataStore.of(SoakKeys.BUKKIT_DATA,
                DataQuery.of("soak"),
                ItemStack.class,
                ItemStackSnapshot.class); //TODO -> find more
        DataRegistration registration = DataRegistration.builder()
                .dataKey(SoakKeys.BUKKIT_DATA)
                .store(dataStore)
                .build();

        event.register(registration);

        SoakKeys.init(event);
    }

    @Listener(order = Order.FIRST)
    public void startingPlugin(StartingEngineEvent<Server> event) {
        //SoakRegister.startEnchantmentTypes(this.logger);
        //SoakRegister.startPotionEffects(this.logger);
        PortalCooldownCustomData.createTickScheduler();

        loadPlugins(true);
    }

    @Listener(order = Order.LAST)
    public void endingPlugin(StoppingEngineEvent<Server> event) {
        var plugins = this.getPlugins().toList();
        plugins.forEach(plugin -> {
            Sponge.server().scheduler().executor(plugin).shutdown();
            Sponge.asyncScheduler().executor(plugin).shutdown();
        });

        Sponge.server().scheduler().executor(container).shutdown();
        Sponge.asyncScheduler().executor(container).shutdown();

        plugins.forEach(container -> {
            //ensures shutdown
            container.getBukkitInstance().onDisable();
        });
        MoseStream.stream(plugins)
                .map(plugin -> SoakPlugin
                        .server()
                        .getSoakPluginManager()
                        .getContext(plugin.getBukkitInstance()))
                .forEach(context -> {
                    var loader = context.loader();
                    try {
                        logger.debug("Closing: " + context.getConfiguration().getName());
                        loader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        plugins.forEach(SoakPluginInjector::removePluginFromPlatform);

        var thread = new Thread(() -> {
            while (Thread.getAllStackTraces().keySet().stream().anyMatch(mainThread -> mainThread.getName().equals("server thread"))) {
                try {
                    Thread.currentThread().wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //for some reason threads get blocked when soak loads plugins. this forces the shutoff.
            //TODO -> figure out why threads get blocked
            logger.debug("Using ublocking fix from Soak");
            System.exit(0);
        });
        thread.setName("unblocker");
        thread.start();
    }

    public boolean didClassesGenerate() {
        return this.generatedClasses.size() == this.generatedClassesCount;
    }

    @Listener
    public void construct(ConstructPluginEvent event) {
        if (ForgeFixCommons.isRequired()) {
            try {
                ForgeFixCommons.installApacheCommons();
                this.logger.info("Forced install of Apache 2");
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        var loader = NMSBounceLoader.getLoader();
        if (loader.hasNMSBounce()) {
            try {
                loader.extractNmsBounce();
                loader.loadNMSBounce();
            } catch (Throwable e) {
                this.logger.warn("NMSBounce not active. Bukkit plugins wont be able to access NMS", e);
            }
        } else {
            this.logger.warn("NMSBounce not active. Bukkit plugins wont be able to access NMS");
        }

        this.consoleHandler.setFormatter(new CustomLoggerFormat());

        SoakServer server = new NMSBounceSoakServer(Sponge::server);
        //noinspection deprecation
        Bukkit.setServer(server);

        RegisterUtils.registerSerializable();

        loadPlugins(false);
        Sponge.eventManager().registerListeners(this.container, new HelpMapListener());
    }

    private void loadPlugins(boolean late) {
        SoakPluginManager pluginManager = SoakPlugin.server().getSoakPluginManager();
        var latePlugins = this.configuration.getLoadingLatePlugins();
        Collection<File> files = Locator.files();
        for (File file : files) {
            try {
                var jarFile = new JarFile(file);
                var entry = jarFile.getJarEntry("plugin.yml");
                if (entry == null) {
                    entry = jarFile.getJarEntry("paper-plugin.yml");
                }
                if (entry == null) {
                    continue;
                }
                var is = jarFile.getInputStream(entry);
                var br = new BufferedReader(new InputStreamReader(is));
                var yamlNode = YamlConfigurationLoader.builder().buildAndLoadString(br.lines().collect(Collectors.joining("\n")));
                var pluginName = yamlNode.node("name").getString();
                if (pluginName == null) {
                    continue;
                }
                if ((late && !latePlugins.contains(pluginName)) || (!late && latePlugins.contains(pluginName))) {
                    continue;
                }
                jarFile.close();
            } catch (Throwable e) {
                continue;
            }


            JavaPlugin plugin;
            try {
                //noinspection deprecation
                plugin = pluginManager.loadPlugin(file);
            } catch (Throwable e) {
                SoakManager.getManager().displayError(e, file);
                continue;
            }
            if (plugin == null) {
                this.logger.error("Failed to load '" + file.getName() + "'. Unknown error");
                continue;
            }

            SoakPluginContainer container = new AbstractSoakPluginContainer(file, plugin, hasDependency(plugin) ? Order.EARLY : Order.DEFAULT);
            container.downloadLibraries();
            loadedPlugins.add(container);
            Sponge.eventManager().registerListeners(container, container.instance(), MethodHandles.lookup());
        }
        SoakPluginInjector.injectPlugins(loadedPlugins);

        this.getPlugins().forEach(container -> ((AbstractSoakPluginContainer) container).instance().onPluginsConstructed());
        if (late) {
            this.getPlugins().forEach(container -> ((AbstractSoakPluginContainer) container).getBukkitInstance().onLoad());
        }
    }

    public Stream<SoakPluginContainer> getPlugins() {
        return loadedPlugins
                .stream();
    }

    public PluginContainer container() {
        return this.container;
    }

    public SoakConfiguration config() {
        return this.configuration;
    }

    public Logger logger() {
        return this.logger;
    }

    @Override
    public SoakConfig getConfig() {
        return this.configuration;
    }

}
