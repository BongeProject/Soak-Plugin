package org.soak.plugin.paper.yaml;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractPluginYamlValue<V> implements PluginYamlValue<V> {

    private final @NotNull Object[] name;

    AbstractPluginYamlValue(@NotNull Object... name) {
        this.name = name;
    }

    @Override
    public @NotNull Object[] name() {
        return this.name;
    }

    public static class PermissionPluginYamlValue extends AbstractPluginYamlValue<Permission> {

        public PermissionPluginYamlValue(Object... name) {
            super(name);
        }

        @Override
        public @NotNull Permission value(@NotNull ConfigurationNode node) throws ConfigurateException {
            String description = PluginYamlValues.DESCRIPTION.value(node);
            PermissionDefault level = PermissionDefault.valueOf(node.node("default").getString());
            var path = node.path().array();
            var children = node.childrenMap().entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toString(), entry -> entry.getValue().getBoolean()));
            String name = path[path.length - 1].toString();
            return new Permission(name, description, level, children);
        }
    }

    public static class StringListPluginYamlValue extends AbstractPluginYamlValue<List<String>> {

        public StringListPluginYamlValue(@NotNull Object... name){
            super(name);
        }

        @Override
        public @NotNull List<String> value(@NotNull ConfigurationNode node) throws ConfigurateException {
            return node.childrenList().stream().map(ConfigurationNode::getString).toList();
        }
    }

    static class BooleanPluginYamlValue extends AbstractPluginYamlValue<Boolean> {

        public BooleanPluginYamlValue(@NotNull Object... name) {
            super(name);
        }

        @Override
        public @NotNull Boolean value(@NotNull ConfigurationNode node) throws ConfigurateException {
            if (node.node(name()).isNull()) {
                throw new ConfigurateException("No value found");
            }
            return node.node(name()).getBoolean();
        }
    }

    static class IntegerPluginYamlValue extends AbstractPluginYamlValue<Integer> {

        public IntegerPluginYamlValue(@NotNull Object... name) {
            super(name);
        }

        @Override
        public @NotNull Integer value(@NotNull ConfigurationNode node) throws ConfigurateException {
            if (node.node(name()).isNull()) {
                throw new ConfigurateException("No value found");
            }
            return node.node(name()).getInt();
        }
    }

    static class StringPluginYamlValue extends AbstractPluginYamlValue<String> {

        public StringPluginYamlValue(@NotNull Object... name) {
            super(name);
        }

        @Override
        public @NotNull String value(@NotNull ConfigurationNode node) throws ConfigurateException {
            var value = node.node(name()).getString();
            if (value == null) {
                throw new ConfigurateException("No value found");
            }
            return value;
        }
    }
}
