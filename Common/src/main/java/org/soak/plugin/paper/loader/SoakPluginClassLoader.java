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
import org.jetbrains.annotations.UnmodifiableView;
import org.soak.NMSBounceLoader;
import org.soak.plugin.SoakManager;
import org.soak.plugin.SoakPluginContainer;
import org.soak.plugin.paper.meta.SoakPluginMeta;
import org.soak.plugin.paper.meta.SoakPluginMetaBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

@ApiStatus.Internal
public class SoakPluginClassLoader extends URLClassLoader implements ConfiguredPluginClassLoader, FoundClassLoader {

    private final PluginProviderContext context;
    private final Collection<Class<?>> classes = new LinkedTransferQueue<>();

    public SoakPluginClassLoader(PluginProviderContext context) throws MalformedURLException {
        super(librariesClassLoader(context), SoakPluginClassLoader.class.getClassLoader());
        this.context = context;
    }

    private static URL[] librariesClassLoader(PluginProviderContext context) throws MalformedURLException {
        PluginMeta meta = context.getConfiguration();
        List<String> libraries;
        if(meta instanceof SoakPluginMeta spm){
            libraries = spm.getLibraries();
        }else{
            libraries = ((PluginDescriptionFile)meta).getLibraries();
        }
        URL extra = context.getPluginSource().toUri().toURL();
        List<File> paths = libraries.stream().map(string -> {
            String[] split =string.split(":", 3);
            String group = split[0].replaceAll(Pattern.quote("."), "/");
            String id = split[1];
            String version = split[2];
            return new File("soakLibraries/" + group + "/" + id + "/" + version + "/" + id + "-" + version + ".jar");
        }).toList();
        String missing = paths.stream().filter(file -> !file.exists()).map(File::getName).map(n -> n.substring(0, n.length() - 4)).collect(Collectors.joining(", "));
        if(!missing.isBlank()){
            context.getLogger().error("Not all libraries have loaded, they could be downloading: Missing " + missing);
        }
        var stream = paths.stream().filter(file -> file.exists()).map(file -> {
            try {
                return file.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });
        return Stream.concat(stream, Stream.of(extra)).toArray(URL[]::new);

    }

    public static void setupPlugin(JavaPlugin javaPlugin, String loggerName, File pluginFile, File configFile, File dataFolder, Supplier<PluginMeta> descriptionGetter, Supplier<SoakPluginMeta> configurationGetter, Supplier<ClassLoader> loaderGetter) {
        PluginMeta pDescription = descriptionGetter.get();
        SoakPluginMeta pMeta = configurationGetter.get();
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
            if (pDescription instanceof PluginDescriptionFile pluginDescriptionFile) {
                applyValue("description", javaPlugin, pluginDescriptionFile);
            } else if (pDescription instanceof SoakPluginMeta meta) {
                applyValue("description", javaPlugin, meta.toDescription());
            } else {
                throw new RuntimeException("Unknown meta class: " + pDescription.getClass().getName());
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

    public SoakPluginMeta buildMeta(){
        var config = getConfiguration();
        if(config instanceof SoakPluginMeta spm) {
            return spm;
        }
        return new SoakPluginMetaBuilder().from(config).build();
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
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        var clazz = super.loadClass(name, resolve);
        this.classes.add(clazz);
        return clazz;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        var generatedClasses = SoakManager.getManager().generatedClasses();
        var foundGeneratedClass = generatedClasses.stream().filter(clazz -> clazz.getName().equals(name)).findAny();
        if (foundGeneratedClass.isPresent()) {
            return foundGeneratedClass.get();
        }

        var opOtherClass = SoakManager
                .getManager()
                .getBukkitSoakContainers()
                .flatMap(spc -> SoakManager
                        .getManager()
                        .getSoakClassLoader(spc)
                        .getClasses()
                        .stream())
                .filter(clazz -> clazz.getTypeName().equals(name))
                .findAny();

        if (opOtherClass.isPresent()) {
            return opOtherClass.get();
        }
        try {
            return super.findClass(name);
        } catch (ClassNotFoundException e) {
            //allow other class loaders to supply there solution if possible

            var opClass = NMSBounceLoader.getLoader().classes().stream().filter(clazz -> clazz.getName().equals(name)).findAny();
            if (opClass.isPresent()) {
                return opClass.get();
            }
            throw e;
            //find a solution for NMS
        }
    }

    @Override
    public @NotNull @UnmodifiableView Collection<Class<?>> getClasses() {
        return Collections.unmodifiableCollection(this.classes);
    }

    @Override
    public void init(JavaPlugin javaPlugin) {
        setupPlugin(javaPlugin, this.getConfiguration().getName(), this.context.getPluginSource().toFile(), new File(this.context.getDataDirectory().toFile(), "config.yml"), this.context.getDataDirectory().toFile(), this::getConfiguration, this::buildMeta, () -> this);
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