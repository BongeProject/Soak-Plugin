package org.soak.config;

import org.soak.config.node.properties.OnlineModeProperty;
import org.soak.config.node.properties.PropertiesNode;
import org.soak.config.node.properties.SpawnProtectionProperty;
import org.soak.plugin.SoakPlugin;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class SoakServerProperties {

    private static final File PROPERTIES_FILE = new File("server.properties");

    private final OnlineModeProperty onlineMode = new OnlineModeProperty();
    private final SpawnProtectionProperty spawnProtection = new SpawnProtectionProperty();

    public SoakServerProperties() {

    }

    public Stream<? extends PropertiesNode<?>> properties() {
        return Arrays.stream(SoakServerProperties.class.getDeclaredFields())
                .filter(field -> Modifier.isPrivate(field.getModifiers()))
                .filter(field -> Modifier.isFinal(field.getModifiers()))
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> PropertiesNode.class.isAssignableFrom(field.getType()))
                .map(field -> {
                    try {
                        return (PropertiesNode<?>) field.get(this);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).filter(Objects::nonNull);
    }

    private <T extends PropertiesNode<?>> T property(Class<T> type) {
        return properties().filter(property -> type.isInstance(property))
                .findAny()
                .map(property -> (T) property)
                .orElseThrow(() -> new RuntimeException("Unknown property type " + type.getName()));
    }

    private boolean loadValue(PropertiesNode<?> property) {
        if (!PROPERTIES_FILE.exists()) {
            return false;
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(PROPERTIES_FILE)));
            var opLine = br.lines().filter(line -> line.startsWith(property.name())).findAny();
            if (opLine.isEmpty()) {
                return false;
            }
            var line = opLine.get();
            var index = line.indexOf("=");
            if (index == -1) {
                return false;
            }
            var value = line.substring(index + 1);
            property.setMemoryValue(value);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public OnlineModeProperty onlineMode() {
        var property = property(OnlineModeProperty.class);
        if (property.value().isEmpty()) {
            if (!loadValue(property)) {
                SoakPlugin.plugin().logger().warn("Cannot read server.properties");
            }
        }
        return property;
    }

    public SpawnProtectionProperty spawnProtection() {
        var property = property(SpawnProtectionProperty.class);
        if (property.value().isEmpty()) {
            if (!loadValue(property)) {
                SoakPlugin.plugin().logger().warn("Cannot read server.properties");
            }
        }
        return property;
    }
}