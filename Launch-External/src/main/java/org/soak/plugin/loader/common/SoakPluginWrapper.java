package org.soak.plugin.loader.common;

import io.leangen.geantyref.TypeToken;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.plugin.Plugin;
import org.soak.command.BukkitRawCommand;
import org.soak.plugin.SoakManager;
import org.soak.plugin.SoakPlugin;
import org.soak.plugin.SoakPluginContainer;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.event.EventListenerRegistration;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;

import java.util.Collection;
import java.util.LinkedHashSet;

public class SoakPluginWrapper {

    public static final String CMD_DESCRIPTION = "description";
    public static final String CMD_USAGE = "usage";
    private final SoakPluginContainer pluginContainer;
    private final Collection<org.bukkit.command.Command> commands = new LinkedHashSet<>();
    private boolean hasRunShutdown;

    public SoakPluginWrapper(SoakPluginContainer pluginContainer, Order order) {
        this.pluginContainer = pluginContainer;

        Plugin plugin = this.pluginContainer.getBukkitInstance();
        this.commands.addAll(PluginCommandYamlParser.parse(plugin));

        var earlyPlugins = SoakPlugin.plugin().config().getLoadingEarlyPlugins();
        if (earlyPlugins.contains(this.pluginContainer.getBukkitInstance().getName())) {
            try {
                plugin.onLoad();
            } catch (Throwable e) {
                SoakManager.getManager().displayError(e, plugin);
            }
        }
        Sponge.eventManager().registerListener(EventListenerRegistration.builder(new TypeToken<StartedEngineEvent<Server>>() {
        }).order(order).listener(this::onPluginEnable).plugin(pluginContainer.getTrueContainer()).build());
    }

    public void onPluginsConstructed() {
        var earlyPlugins = SoakPlugin.plugin().config().getLoadingEarlyPlugins();
        if (!earlyPlugins.contains(this.pluginContainer.getBukkitInstance().getName())) {
            return;
        }
        Plugin plugin = this.pluginContainer.getBukkitInstance();
        try {
            plugin.onEnable();
        } catch (Throwable e) {
            SoakManager.getManager().displayError(e, plugin);
        }
    }

    public SoakPluginContainer container() {
        return this.pluginContainer;
    }

    public Collection<org.bukkit.command.Command> commands() {
        return this.commands;
    }

    @Listener
    public void onCommandRegister(RegisterCommandEvent<Command.Raw> event) {
        this.commands.forEach(cmd -> event.register(this.pluginContainer,
                new BukkitRawCommand(this.pluginContainer, cmd),
                cmd.getName(),
                cmd.getAliases().toArray(String[]::new)));
    }

    @Listener
    public void onPluginLoad(StartingEngineEvent<Server> event) {
        if (!SoakPlugin.plugin().didClassesGenerate()) {
            return;
        }
        var earlyPlugins = SoakPlugin.plugin().config().getLoadingEarlyPlugins();
        if (earlyPlugins.contains(this.pluginContainer.getBukkitInstance().getName())) {
            return;
        }
        Plugin plugin = this.pluginContainer.getBukkitInstance();
        try {
            plugin.onLoad();
        } catch (Throwable e) {
            SoakManager.getManager().displayError(e, plugin);
        }
    }

    //issue
    //Bukkit plugins assume everything is loaded when onEnable is run, this is because Craftbukkit loads everything before onEnable is used ....
    //using StartedEngineEvent despite the timing known to be incorrect
    public void onPluginEnable(StartedEngineEvent<Server> event) {
        if (!SoakPlugin.plugin().didClassesGenerate()) {
            return;
        }
        var earlyPlugins = SoakPlugin.plugin().config().getLoadingEarlyPlugins();
        if (earlyPlugins.contains(this.pluginContainer.getBukkitInstance().getName())) {
            return;
        }
        Plugin plugin = this.pluginContainer.getBukkitInstance();
        try {
            plugin.onEnable();
        } catch (Throwable e) {
            SoakManager.getManager().displayError(e, plugin);
        }
    }

    @Listener
    public void onPluginDisable(StoppingEngineEvent<Server> event) {
        if (!SoakPlugin.plugin().didClassesGenerate()) {
            return;
        }
        if (hasRunShutdown) {
            return;
        }
        Plugin plugin = this.pluginContainer.getBukkitInstance();
        try {
            plugin.onDisable();
        } catch (Throwable e) {
            SoakManager.getManager().displayError(e, plugin);
        }
        hasRunShutdown = true;
    }
}
