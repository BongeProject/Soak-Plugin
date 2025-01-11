package org.soak.plugin;

import org.bukkit.command.PluginCommandYamlParser;
import org.soak.command.BukkitRawCommand;
import org.soak.data.sponge.PortalCooldownCustomData;
import org.soak.data.sponge.SoakKeys;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataStore;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.*;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.lang.reflect.InvocationTargetException;

public class SoakWrapperListener {

    private final AbstractSpongePluginMain pluginMain;

    public SoakWrapperListener(AbstractSpongePluginMain pluginMain) {
        this.pluginMain = pluginMain;
    }

    @Listener
    public void onConstruct(ConstructPluginEvent event) {
        if (!event.plugin().equals(pluginMain.container)) { //just to ensure it
            return;
        }
        try {
            pluginMain.loadPlugin();
        } catch (NoSuchMethodException | IllegalAccessException e) {
            this.pluginMain.getLogger().error("The JavaPlugin instance must have a public empty argument constructor", e);
        } catch (InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Listener
    public void onPluginCommandRegister(RegisterCommandEvent<Command.Raw> event) {
        var bukkitCommands = PluginCommandYamlParser.parse(pluginMain.getPlugin());
        pluginMain.commands.addAll(bukkitCommands);
        bukkitCommands.forEach(cmd -> event.register(pluginMain.container, new BukkitRawCommand(pluginMain.soakPluginContainer, cmd), cmd.getName(), cmd.getAliases().toArray(String[]::new)));
    }

    @Listener
    public void onPluginLoad(StartingEngineEvent<Server> event) {
        pluginMain.getLogger().warn("On Engine starting");
        try {
            pluginMain.getPlugin().onLoad();
        } catch (Throwable e) {
            SoakManager.getManager().displayError(e, pluginMain.getPlugin());
        }
    }

    //issue
    //Bukkit plugins assume everything is loaded when onEnable is run, this is because Craftbukkit loads everything before onEnable is used ....
    //using StartedEngineEvent despite the timing known to be incorrect
    @Listener
    public void onPluginEnable(StartedEngineEvent<Server> event) {
        pluginMain.getLogger().warn("On Engine started");
        try {
            pluginMain.getPlugin().onEnable();
        } catch (Throwable e) {
            SoakManager.getManager().displayError(e, pluginMain.getPlugin());
        }
    }


    @Listener
    public void dataRegister(RegisterDataEvent event) {
        DataStore dataStore = DataStore.of(SoakKeys.BUKKIT_DATA,
                DataQuery.of("soak"),
                ItemStack.class,
                ItemStackSnapshot.class); //TODO -> find more
        DataRegistration registration = DataRegistration.builder()
                .dataKey(SoakKeys.BUKKIT_DATA)
                .store(dataStore)
                .build();

        event.register(registration);

        SoakKeys.init(event);
    }

    @Listener(order = Order.FIRST)
    public void startingPlugin(StartingEngineEvent<Server> event) {
        //SoakRegister.startEnchantmentTypes(pluginMain.getLogger());
        //SoakRegister.startPotionEffects(pluginMain.getLogger());
        PortalCooldownCustomData.createTickScheduler();
    }

    @Listener
    public void stoppingPlugin(StoppingEngineEvent<Server> event) {
        pluginMain.getPlugin().onDisable();
    }

    @Listener(order = Order.LAST)
    public void endingPlugin(StoppingEngineEvent<Server> event) {
        Sponge.server().scheduler().executor(pluginMain.container).shutdown();
        Sponge.asyncScheduler().executor(pluginMain.container).shutdown();
        /*MoseStream.stream(plugins)
                .map(plugin -> SoakPlugin
                        .server()
                        .getPluginManager()
                        .getContext(plugin.getBukkitInstance()))
                .forEach(context -> {
                    var loader = context.loader();
                    try {
                        logger.debug("Closing: " + context.getConfiguration().getName());
                        loader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        plugins.forEach(SoakPluginInjector::removePluginFromPlatform);*/

        var thread = new Thread(() -> {
            while (Thread.getAllStackTraces().keySet().stream().anyMatch(mainThread -> mainThread.getName().equals("server thread"))) {
                try {
                    Thread.currentThread().wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //for some reason threads get blocked when soak loads plugins. this forces the shutoff.
            //TODO -> figure out why threads get blocked
            pluginMain.getLogger().debug("Using ublocking fix from Soak");
            System.exit(0);
        });
        thread.setName("unblocker");
        thread.start();
    }
}
