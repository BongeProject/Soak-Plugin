package org.soak.plugin.loader.neo;

import net.neoforged.neoforgespi.language.IConfigurable;
import org.soak.plugin.SoakPluginContainer;

import java.util.List;
import java.util.Optional;

public class NeoSoakModConfig implements IConfigurable {

    private final SoakPluginContainer container;

    public NeoSoakModConfig(SoakPluginContainer container) {
        this.container = container;
    }

    @Override
    public <T> Optional<T> getConfigElement(String... strings) {
        if (match(strings, "modId")) {
            return (Optional<T>) Optional.of(container.metadata().id());
        }
        if (match(strings, "namespace")) {
            return (Optional<T>) Optional.of(container.metadata().id());
        }
        if (match(strings, "version")) {
            return (Optional<T>) Optional.of(container.metadata().version().toString());
        }
        if (match(strings, "displayName")) {
            return (Optional<T>) container.metadata().name();
        }
        if (match(strings, "description")) {
            return (Optional<T>) container.metadata().description();
        }
        return Optional.empty();
    }

    @Override
    public List<? extends IConfigurable> getConfigList(String... strings) {
        System.out.println("PluginListNode: " + String.join(".", strings));

        return List.of();
    }

    private boolean match(String[] node, String... compare) {
        if (node.length != compare.length) {
            return false;
        }
        for (var index = 0; index < node.length; index++) {
            if (!node[index].equals(compare[index])) {
                return false;
            }
        }
        return true;
    }
}
