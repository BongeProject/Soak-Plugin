package org.soak.commands.soak;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.soak.commands.SoakArguments;
import org.soak.generate.bukkit.AttributeTypeList;
import org.soak.generate.bukkit.EntityTypeList;
import org.soak.generate.bukkit.MaterialList;
import org.soak.plugin.SoakManager;
import org.soak.plugin.SoakPlugin;
import org.soak.plugin.SoakPluginContainer;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.Comparator;
import java.util.stream.Collectors;

public class SoakCommand {


    private static Command.Parameterized createCommandsForCommand() {
        Parameter.Value<SoakPluginContainer> pluginParameter = SoakArguments.soakPlugins(((context, pluginContainer) -> {
                    return !pluginContainer.getBukkitInstance()
                            .getDescription()
                            .getCommands()
                            .isEmpty();
                }))
                .key("plugin")
                .build();
        return Command.builder().addParameter(pluginParameter).executor(context -> {
            SoakPluginContainer pluginContainer = context.requireOne(pluginParameter);
            JavaPlugin plugin = pluginContainer.getBukkitInstance();
            pluginContainer.getBukkitInstance().getDescription().getCommands().keySet().forEach(cmdName -> {
                PluginCommand pluginCommand = plugin.getCommand(cmdName);
                if (pluginCommand == null) {
                    SoakManager.getManager().getLogger()
                            .error("plugin (" + plugin.getName() + ") has " + cmdName + " as a command in it's plugin.yml yet cannot be found when receiving");
                    return;
                }
                context.sendMessage(Identity.nil(),
                        Component.text(pluginCommand.getName() + ": " + pluginCommand.getDescription() + ": " + pluginCommand.getUsage()));
            });
            return CommandResult.success();
        }).build();
    }

    private static Command.Parameterized createPluginsCommand() {
        return Command
                .builder()
                .addChild(createCommandsForCommand(), "commands")
                .executor(context -> {
                    var plugins = SoakManager.getManager().getBukkitSoakContainers()
                            .map(SoakPluginContainer::getBukkitInstance)
                            .sorted(Comparator.comparing(plugin -> ((JavaPlugin) plugin).isEnabled())
                                    .thenComparing(plugin -> ((JavaPlugin) plugin).getName()))
                            .map(plugin -> Component.text(plugin.getName() + " - Loaded " + (SoakPlugin.plugin().config().getLoadingEarlyPlugins().contains(plugin.getName()) ? "Early" : "Late"))
                                    .color(plugin.isEnabled() ? NamedTextColor.GREEN : NamedTextColor.RED))
                            .collect(Collectors.toList());

                    Component pluginsLine = Component.join(JoinConfiguration.separator(Component.text(",    ")),
                            plugins);

                    context.cause().sendMessage(Identity.nil(), Component
                            .text("(" + plugins.size() + ")")
                            .color(TextColor.color(25, 255, 25))
                            .append(Component.text(" Bukkit plugins found").color(NamedTextColor.WHITE)));
                    context.cause().sendMessage(Identity.nil(), pluginsLine);
                    return CommandResult.success();
                })
                .build();
    }

    public static Command.Parameterized createInfoCommand() {
        return Command.builder()
                .executor(context -> {
                    var id = Identity.nil();
                    context.sendMessage(id,
                            createInfoMessage("Version",
                                    SoakPlugin.plugin().container().metadata().version().toString()));
                    return CommandResult.success();
                })
                .build();
    }

    public static Command.Parameterized createMaterialList() {
        return Command.builder()
                .addChild(createMaterialBlockFind(), "block", "b")
                .addChild(createMaterialItemFind(), "item", "i")
                .executor(context -> {
                    MaterialList.values().stream().sorted(Comparator.comparing(Enum::name)).forEach(mat -> {
                        String item = MaterialList.getItemType(mat).map(t -> t.key(RegistryTypes.ITEM_TYPE).asString()).orElse("NONE");
                        String block = MaterialList.getBlockType(mat).map(t -> t.key(RegistryTypes.BLOCK_TYPE).asString()).orElse("NONE");
                        Component component = Component.text(mat.name() + "(Item: " + item + ", Block: " + block + ")");

                        context.sendMessage(component);
                    });
                    return CommandResult.success();
                })
                .build();
    }

    public static Command.Parameterized createEntityTypeList() {
        return Command.builder()
                .executor(context -> {
                    EntityTypeList.values().stream().sorted(Comparator.comparing(Enum::name)).forEach(mat -> {
                        String entityType = EntityTypeList.getEntityType(mat).map(t -> t.key(RegistryTypes.ENTITY_TYPE).asString()).orElse("NONE");
                        Component component = Component.text(mat.name() + "(EntityType: " + entityType + ")");
                        context.sendMessage(component);
                    });
                    return CommandResult.success();
                })
                .build();
    }

    public static Command.Parameterized createAttributeTypeList() {
        return Command.builder()
                .executor(context -> {
                    AttributeTypeList.values().stream().sorted(Comparator.comparing(Enum::name)).forEach(att -> {
                        String attributeType = AttributeTypeList.getAttributeType(att).key(RegistryTypes.ATTRIBUTE_TYPE).formatted();
                        var component = Component.text(att.name() + "(AttributeType: " + attributeType + ")");
                        context.sendMessage(component);
                    });
                    return CommandResult.success();
                }).build();
    }

    public static Command.Parameterized createMaterialItemFind() {
        var itemParameter = SoakArguments.registry(ItemType.class, "item", ItemTypes::registry, () -> RegistryTypes.ITEM_TYPE);
        return Command.builder().addParameter(itemParameter).executor(context -> {
            var t = context.requireOne(itemParameter);
            context.sendMessage(Component.text(MaterialList.value(t).name()));
            return CommandResult.success();
        }).build();
    }

    public static Command.Parameterized createMaterialBlockFind() {
        var blockParameter = SoakArguments.registry(BlockType.class, "block", BlockTypes::registry, () -> RegistryTypes.BLOCK_TYPE);
        return Command.builder().addParameter(blockParameter).executor(context -> {
            var t = context.requireOne(blockParameter);
            context.sendMessage(Component.text(MaterialList.value(t).name()));
            return CommandResult.success();
        }).build();
    }

    public static Command.Parameterized createSoakCommand() {
        return Command.builder()
                .addChild(createPluginsCommand(), "plugins", "pl")
                .addChild(createInfoCommand(), "info")
                .addChild(createMaterialList(), "material")
                .addChild(createEntityTypeList(), "entityType", "entity")
                .addChild(createAttributeTypeList(), "attributeType", "attribute")
                .build();
    }

    private static Component createInfoMessage(String key, String value) {
        return createInfoMessage(key, value, NamedTextColor.AQUA);
    }

    private static Component createInfoMessage(String key, String value, TextColor colour) {
        Component valueMessage = Component.text(value).color(colour);
        Component keyMessage = Component.text(key).color(NamedTextColor.GOLD);
        return keyMessage.append(Component.text(": ").color(NamedTextColor.BLUE)).append(valueMessage);
    }
}
