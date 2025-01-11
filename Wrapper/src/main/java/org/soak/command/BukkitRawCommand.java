package org.soak.command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.PluginCommand;
import org.soak.map.SoakMessageMap;
import org.soak.map.SoakSubjectMap;
import org.soak.plugin.SoakManager;
import org.soak.plugin.SoakPluginContainer;
import org.soak.utils.BasicEntry;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.event.EventContextKeys;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BukkitRawCommand implements Command.Raw {

    private final org.bukkit.command.Command command;
    private final SoakPluginContainer owningPlugin;

    public BukkitRawCommand(SoakPluginContainer owningPlugin, org.bukkit.command.Command command) {
        this.command = command;
        this.owningPlugin = owningPlugin;
    }

    @Override
    public CommandResult process(CommandCause cause, ArgumentReader.Mutable arguments) throws CommandException {
        String command = cause.context().get(EventContextKeys.COMMAND).map(rawCommand -> {
            int index = rawCommand.indexOf(" ");
            if (index == -1) {
                return rawCommand;
            }
            return rawCommand.substring(0, index);
        }).orElse(this.command.getName());
        String[] args = arguments.input().split(" ");
        try {
            boolean result = this.command.execute(SoakSubjectMap.mapToBukkit(cause.subject()), command, args);
            return result ? CommandResult.success() : CommandResult.error(this.usage(cause));
        } catch (org.bukkit.command.CommandException e) {
            SoakManager.getManager()
                    .displayError(e.getCause(),
                            this.owningPlugin.getBukkitInstance(),
                            new BasicEntry<>("type", "Execute"),
                            new BasicEntry<>("command", command),
                            new BasicEntry<>("arguments", String.join(" ", args)));
            return CommandResult.error(Component.text(e.getMessage()));
        } catch (Throwable e) {
            SoakManager.getManager()
                    .displayError(e,
                            this.owningPlugin.getBukkitInstance(),
                            new BasicEntry<>("type", "Execute"),
                            new BasicEntry<>("command", command),
                            new BasicEntry<>("arguments", String.join(" ", args)));
            return CommandResult.error(Component.text(e.getMessage()));
        }
    }

    @Override
    public List<CommandCompletion> complete(CommandCause cause, ArgumentReader.Mutable arguments) throws CommandException {
        if (!(this.command instanceof PluginCommand)) {
            return Collections.emptyList();
        }
        var plCmd = (PluginCommand) this.command;
        if (plCmd.getTabCompleter() == null) {
            return Collections.emptyList();
        }
        String command = cause.context().get(EventContextKeys.COMMAND).map(rawCommand -> {
            int index = rawCommand.indexOf(" ");
            if (index == -1) {
                return rawCommand;
            }
            return rawCommand.substring(0, index);
        }).orElse("");
        String[] args = arguments.input().split(" ");

        try {
            List<String> commands = plCmd.getTabCompleter()
                    .onTabComplete(SoakSubjectMap.mapToBukkit(cause.subject()), this.command, command, args);
            if (commands == null) {
                return Collections.emptyList();
            }
            return commands.stream().map(CommandCompletion::of).collect(Collectors.toList());
        } catch (Throwable e) {
            SoakManager.getManager()
                    .displayError(e,
                            this.owningPlugin.getBukkitInstance(),
                            new BasicEntry<>("type", "Suggest"),
                            new BasicEntry<>("command", command),
                            new BasicEntry<>("arguments", String.join(" ", args)));
            return Collections.emptyList();
        }
    }

    @Override
    public boolean canExecute(CommandCause cause) {
        return true;
    }

    @Override
    public Optional<Component> shortDescription(CommandCause cause) {
        return extendedDescription(cause);
    }

    @Override
    public Optional<Component> extendedDescription(CommandCause cause) {
        return Optional.of(SoakMessageMap.toComponent(this.command.getDescription()));
    }

    @Override
    public Component usage(CommandCause cause) {
        return SoakMessageMap.toComponent(this.command.getUsage());
    }
}
