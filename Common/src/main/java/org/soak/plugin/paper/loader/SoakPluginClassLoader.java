package org.soak.plugin.paper.loader;

import com.destroystokyo.paper.utils.PaperPluginLogger;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import io.papermc.paper.plugin.provider.classloader.PluginClassLoaderGroup;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.plugin.SoakManager;
import org.soak.plugin.SoakPluginContainer;
import org.soak.plugin.paper.meta.SoakPluginMeta;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.function.Supplier;
import java.util.zip.ZipFile;

@ApiStatus.Internal
public class SoakPluginClassLoader extends URLClassLoader implements ConfiguredPluginClassLoader {

    private final PluginProviderContext context;

    public SoakPluginClassLoader(PluginProviderContext context) throws MalformedURLException {
        super(new URL[]{context.getPluginSource().toUri().toURL()}, SoakPluginClassLoader.class.getClassLoader());
        this.context = context;
    }

    public static void setupPlugin(JavaPlugin javaPlugin, String loggerName, File pluginFile, File configFile, File dataFolder, Supplier<PluginMeta> configurationGetter, Supplier<ClassLoader> loaderGetter) {
        PluginMeta pMeta = configurationGetter.get();
        var logger = PaperPluginLogger.getLogger(loggerName);
        logger.setUseParentHandlers(false);
        logger.addHandler(SoakManager.getManager().getConsole());
        try {
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(configFile);
            try {
                copyYaml(configuration, pluginFile);
            } catch (IOException e) {
                //no default config
            }
            applyValue("isEnabled", javaPlugin, true);
            //applyValue("loader", javaPlugin, this);
            applyValue("server", javaPlugin, Bukkit.getServer());
            applyValue("file", javaPlugin, pluginFile);
            if (pMeta instanceof PluginDescriptionFile pluginDescriptionFile) {
                applyValue("description", javaPlugin, pluginDescriptionFile);
            } else if (pMeta instanceof SoakPluginMeta meta) {
                applyValue("description", javaPlugin, meta.toDescription());
            } else {
                throw new RuntimeException("Unknown meta class: " + pMeta.getClass().getName());
            }
            applyValue("pluginMeta", javaPlugin, pMeta);
            applyValue("dataFolder", javaPlugin, dataFolder);
            applyValue("classLoader", javaPlugin, loaderGetter.get());
            applyValue("newConfig", javaPlugin, configuration);
            applyValue("configFile", javaPlugin, configFile);
            applyValue("logger", javaPlugin, logger);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static void applyValue(String fieldName, JavaPlugin plugin, Object value) throws NoSuchFieldException, IllegalAccessException {
        var field = JavaPlugin.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(plugin, value);
        field.setAccessible(false);
    }

    private static void copyYaml(YamlConfiguration configuration, File file) throws IOException {
        ZipFile zip = new ZipFile(file);
        var configEntry = zip.getEntry("config.yml");
        if (configEntry == null) {
            return;
        }
        var configIS = zip.getInputStream(configEntry);
        var isReader = new InputStreamReader(configIS);

        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(isReader);
        isReader.close();
        configIS.close();
        zip.close();
        configuration.addDefaults(defaultConfig);
    }

    @Override
    public PluginMeta getConfiguration() {
        return this.context.getConfiguration();
    }

    @Override
    public Class<?> loadClass(@NotNull String name, boolean resolve, boolean checkGlobal, boolean checkLibs) throws ClassNotFoundException {

        //TODO GLOBAL AND LIBS
        return loadClass(name, resolve);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        var generatedClasses = SoakManager.getManager().generatedClasses();
        var foundGeneratedClass = generatedClasses.stream().filter(clazz -> clazz.getName().equals(name)).findAny();
        if (foundGeneratedClass.isPresent()) {
            return foundGeneratedClass.get();
        }
        return super.findClass(name);
    }

    @Override
    public void init(JavaPlugin javaPlugin) {
        setupPlugin(javaPlugin, this.getConfiguration().getName(), this.context.getPluginSource().toFile(), new File(this.context.getDataDirectory().toFile(), "config.yml"), this.context.getDataDirectory().toFile(), this::getConfiguration, () -> this);
    }

    @Override
    public @Nullable JavaPlugin getPlugin() {
        var config = this.getConfiguration();
        return SoakManager
                .getManager()
                .getBukkitSoakContainers()
                .map(SoakPluginContainer::getBukkitInstance)
                .filter(jp -> jp.getPluginMeta().equals(config))
                .findAny()
                .orElse(null);
    }

    @Override
    public @Nullable PluginClassLoaderGroup getGroup() {
        return null;
    }
}