package org.soak.plugin;

import com.google.inject.Inject;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.jar.asm.MethodTooLargeException;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.mosestream.MoseStream;
import org.soak.WrapperManager;
import org.soak.commands.soak.SoakCommand;
import org.soak.config.SoakConfiguration;
import org.soak.data.sponge.PortalCooldownCustomData;
import org.soak.data.sponge.SoakKeys;
import org.soak.fix.forge.ForgeFixCommons;
import org.soak.generate.bukkit.EntityTypeList;
import org.soak.generate.bukkit.MaterialList;
import org.soak.hook.event.HelpMapListener;
import org.soak.io.SoakServerProperties;
import org.soak.plugin.external.SoakConfig;
import org.soak.plugin.loader.Locator;
import org.soak.plugin.loader.common.AbstractSoakPluginContainer;
import org.soak.plugin.loader.common.SoakPluginInjector;
import org.soak.utils.SoakMemoryStore;
import org.soak.utils.log.CustomLoggerFormat;
import org.soak.wrapper.SoakServer;
import org.soak.wrapper.plugin.SoakPluginManager;
import org.soak.wrapper.v1_19_R4.NMSBounceSoakServer;
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
import org.spongepowered.plugin.PluginContainer;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.ConsoleHandler;
import java.util.stream.Stream;

@org.spongepowered.plugin.builtin.jvm.Plugin("soak")
public class SoakPlugin implements SoakExternalManager, WrapperManager {

    private static SoakPlugin plugin;
    private final SoakConfiguration configuration;
    private final PluginContainer container;
    private final Logger logger;
    private final SoakMemoryStore memoryStore = new SoakMemoryStore();

    private final SoakServerProperties serverProperties = new SoakServerProperties();
    private final ConsoleHandler consoleHandler = new ConsoleHandler();

    public final Collection<Class<?>> generatedClasses = new LinkedBlockingQueue<>();
    private final int generatedClassesCount = 2;
    private final Collection<SoakPluginContainer> loadedPlugins = new ArrayList<>();

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

    @Listener
    private void generateMaterial(RegisterRegistryValueEvent.EngineScoped<Server> event) {
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
        } catch (MethodTooLargeException e) {
            throw new IllegalStateException("This is a problem with Bukkit's design: PaperMC seem to be making a fix with its hardfork: Too many entries in " + creatingClass, e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SoakPlugin plugin() {
        return plugin;
    }

    public static SoakServer server() {
        return (SoakServer) Bukkit.getServer();
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

        this.consoleHandler.setFormatter(new CustomLoggerFormat());

        SoakServer server = new NMSBounceSoakServer(Sponge::server);
        SoakPluginManager pluginManager = server.getSoakPluginManager();
        //noinspection deprecation
        Bukkit.setServer(server);

        Collection<File> files = Locator.files();
        for (File file : files) {
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

            SoakPluginContainer container = new AbstractSoakPluginContainer(file, plugin);
            loadedPlugins.add(container);
            Sponge.eventManager().registerListeners(container, container.instance(), MethodHandles.lookup());
        }
        SoakPluginInjector.injectPlugins(loadedPlugins);
        this.getPlugins().forEach(container -> ((AbstractSoakPluginContainer) container).instance().onPluginsConstructed());
        Sponge.eventManager().registerListeners(this.container, new HelpMapListener());
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
