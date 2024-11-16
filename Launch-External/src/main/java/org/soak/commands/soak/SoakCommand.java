package org.soak.commands.soak;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.soak.commands.SoakArguments;
import org.soak.plugin.SoakManager;
import org.soak.plugin.SoakPlugin;
import org.soak.plugin.SoakPluginContainer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.Parameter;

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
                    var plugins = SoakManager.getManager().getBukkitContainers()
                            .map(SoakPluginContainer::getBukkitInstance)
                            .sorted(Comparator.comparing(plugin -> ((JavaPlugin) plugin).isEnabled())
                                    .thenComparing(plugin -> ((JavaPlugin) plugin).getName()))
                            .map(plugin -> Component.text(plugin.getName())
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

    public static Command.Parameterized createSoakCommand() {
        return Command.builder()
                .addChild(createPluginsCommand(), "plugins", "pl")
                .addChild(createInfoCommand(), "info")
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
