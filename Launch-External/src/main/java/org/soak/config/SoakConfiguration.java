package org.soak.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.soak.config.node.*;
import org.soak.plugin.external.SoakConfig;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SoakConfiguration implements SoakConfig {

    public static final FileConfigNode PLUGIN_FOLDER = new FileConfigNode(new ConfigNodeMeta<>()
            .setDefaultValue(new File("mods/bukkit/plugin"))
            .setComment("The location of where plugins should be placed"),
            "path", "plugin");
    public static final FileConfigNode CONFIG_FOLDER = new FileConfigNode(new ConfigNodeMeta<>()
            .setDefaultValue(new File("plugins"))
            .setComment("The location of where plugin config files are stored.\nPlease note that most plugins hard code the 'plugins' path and break if this value is changed"),
            "path", "config");
    public static final ComponentConfigNode NO_PERMISSION_MESSAGE = new ComponentConfigNode(new ConfigNodeMeta<>()
            .setDefaultValue(Component.text("You do not have permission to do that").color(NamedTextColor.RED))
            .setComment("The message that is sent to the player if they do not have permission, the message is in JSON format (there are websites that can build the message for you). \nPlease note that most plugins use there own messaging system"),
            "messages", "permission", "none");
    public static final BooleanConfigNode SHOW_DEBUG_LOG = new BooleanConfigNode("messages", "debug", "show");
    public static final ListConfigNode<String> LOAD_ON_CONSTRUCTION = new ListConfigNode<>(new ConfigNodeMeta<List<String>>()
            .setComment("Add plugin names to make the plugin load early. This can help some plugins register the required data, but other plugins may break."),
            new StringConfigNode(),
            "compatibility", "run early", "plugins");
    public static final ListConfigNode<String> LOAD_LATE = new ListConfigNode<>(new ConfigNodeMeta<List<String>>().setComment("Add plugin names to make the plugin run late. This should be used if you are getting 'NoClassDefFoundError' when the plugin is booting"), new StringConfigNode(), "compatibility", "run late", "plugins");
    public static final BooleanConfigNode SHOULD_MATERIALS_LIST_USE_MODDED = new BooleanConfigNode(new ConfigNodeMeta<>().setComment("Allows modded blocks to be used with Bukkit plugins. Note: Due to a design flaw in Bukkit, only enable this if your server doesn't add too many blocks and/or items"), "Hooks", "Bukkit", "Material");

    private final File file;
    private final HoconConfigurationLoader loader;
    private final CommentedConfigurationNode node;

    public SoakConfiguration(File file) throws ConfigurateException {
        this.file = file;
        this.loader = HoconConfigurationLoader.builder().file(file).build();
        this.node = this.loader.load();
    }

    public static Stream<? extends ConfigNode<?>> getNodes() {
        return Arrays.stream(SoakConfiguration.class.getDeclaredFields())
                .filter(field -> Modifier.isPublic(field.getModifiers()))
                .filter(field -> Modifier.isFinal(field.getModifiers()))
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .filter(field -> ConfigNode.class.isAssignableFrom(field.getType()))
                .map(field -> {
                    try {
                        return (ConfigNode<?>) field.get(null);
                    } catch (IllegalAccessException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }

    public void setDefaults(boolean replaceCurrent) throws SerializationException {
        var nodes = getNodes().toList();
        for (var node : nodes) {
            node.setDefault(root(), replaceCurrent);
        }
    }

    public File file() {
        return file;
    }

    public HoconConfigurationLoader loader() {
        return loader;
    }

    public CommentedConfigurationNode root() {
        return this.node;
    }

    public boolean showDebugLog() {
        return parse(SHOW_DEBUG_LOG);
    }

    public File pluginFolder() {
        return parse(PLUGIN_FOLDER);
    }

    public boolean shouldMaterialListUseModded() {
        return parse(SHOULD_MATERIALS_LIST_USE_MODDED);
    }

    public <T> T parse(ConfigNode<T> node) {
        var opParse = node.parse(this.node);
        return opParse
                .orElseGet(() -> node
                        .getDefaultValue()
                        .orElseThrow(() -> new IllegalStateException("Could not get default value for " + Arrays.stream(node.node()).map(Object::toString).collect(Collectors.joining("->")))));
    }

    public <T> void set(ConfigNode<T> node, T value) throws SerializationException {
        node.set(this.node, value);
    }

    public void save() throws ConfigurateException {
        this.loader.save(this.node);
    }

    public List<String> getLoadingEarlyPlugins() {
        return parse(LOAD_ON_CONSTRUCTION);
    }

    public List<String> getLoadingLatePlugins() {
        return parse(LOAD_LATE);
    }

    @Override
    public File getConfigPath() {
        return parse(CONFIG_FOLDER);
    }

    @Override
    public Component getNoPermissionMessage() {
        return parse(NO_PERMISSION_MESSAGE);
    }
}
