package org.soak.commands;

import org.soak.plugin.SoakManager;
import org.soak.plugin.SoakPluginContainer;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParser;
import org.spongepowered.api.command.parameter.managed.clientcompletion.ClientCompletionType;
import org.spongepowered.api.command.parameter.managed.clientcompletion.ClientCompletionTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.registry.DefaultedRegistryType;
import org.spongepowered.api.registry.Registry;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SoakArguments {

    public static <T> Parameter.Value<T> registry(Class<T> clazz, String name, Supplier<Registry<T>> supplier){
        return Parameter.builder(clazz).key(name).addParser(new ValueParser<T>() {
                    @Override
                    public Optional<T> parseValue(Parameter.Key<? super T> parameterKey, ArgumentReader.Mutable reader, CommandContext.Builder context) throws ArgumentParseException {
                        var key = reader.parseResourceKey();
                        var reg = supplier.get();
                        return reg.findValue(key);
                    }

                    @Override
                    public List<ClientCompletionType> clientCompletionType() {
                        return List.of(ClientCompletionTypes.RESOURCE_KEY.get());
                    }
                })
                .completer((context, currentInput) -> {
                    var reg = supplier.get();
                    return ItemTypes
                            .registry()
                            .stream()
                            .map(itemType -> itemType.key((DefaultedRegistryType<T>) reg.type()).asString())
                            .filter(itemTypeString -> itemTypeString.startsWith(currentInput))
                            .map(CommandCompletion::of)
                            .toList();
                })
                .build();
    }


    public static Parameter.Value.Builder<SoakPluginContainer> soakPlugins() {
        return soakPlugins((context, pluginContainer) -> true);
    }

    public static Parameter.Value.Builder<SoakPluginContainer> soakPlugins(BiPredicate<CommandContext, SoakPluginContainer> predicate) {
        return Parameter.builder(SoakPluginContainer.class)
                .addParser((parameterKey, reader, context) -> {
                    String input = reader.parseString();
                    return SoakManager.getManager().getBukkitContainers()
                            .filter(spc -> spc.getBukkitInstance().getName().equalsIgnoreCase(input))
                            .findAny()
                            .filter(spc -> predicate.test(context, spc));
                })
                .completer((context, currentInput) -> SoakManager.getManager().getBukkitContainers()
                        .filter(soakPluginContainer -> soakPluginContainer.getBukkitInstance()
                                .getName()
                                .toLowerCase()
                                .startsWith(currentInput.toLowerCase()))
                        .filter(soakPluginContainer -> predicate.test(context, soakPluginContainer))
                        .map(spc -> CommandCompletion.of(spc.getBukkitInstance().getName()))
                        .collect(Collectors.toList()));
    }
}
