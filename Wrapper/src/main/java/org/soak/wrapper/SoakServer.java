package org.soak.wrapper;

import com.destroystokyo.paper.MaterialSetTag;
import com.destroystokyo.paper.entity.ai.MobGoals;
import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.ban.BanListType;
import io.papermc.paper.datapack.DatapackManager;
import io.papermc.paper.math.Position;
import io.papermc.paper.tag.EntitySetTag;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.*;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.*;
import org.bukkit.loot.LootTable;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapView;
import org.bukkit.packs.DataPackManager;
import org.bukkit.packs.ResourcePack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.SimpleServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.potion.PotionBrewer;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.CachedServerIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mose.collection.stream.builder.CollectionStreamBuilder;
import org.soak.WrapperManager;
import org.soak.exception.NotImplementedException;
import org.soak.map.*;
import org.soak.map.item.SoakItemStackMap;
import org.soak.map.item.SoakRecipeMap;
import org.soak.plugin.SoakExternalManager;
import org.soak.plugin.SoakManager;
import org.soak.utils.*;
import org.soak.utils.log.CustomLoggerFormat;
import org.soak.wrapper.block.data.SoakBlockData;
import org.soak.wrapper.command.SoakCommandMap;
import org.soak.wrapper.command.SoakConsoleCommandSender;
import org.soak.wrapper.entity.living.human.SoakPlayer;
import org.soak.wrapper.entity.living.human.user.SoakLoadingUser;
import org.soak.wrapper.help.SoakHelpMap;
import org.soak.wrapper.inventory.SoakInventory;
import org.soak.wrapper.inventory.SoakItemFactory;
import org.soak.wrapper.plugin.SoakPluginManager;
import org.soak.wrapper.plugin.messaging.SoakMessenger;
import org.soak.wrapper.profile.SoakPlayerProfile;
import org.soak.wrapper.scheduler.SoakBukkitScheduler;
import org.soak.wrapper.world.SoakWorld;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.ban.Ban;
import org.spongepowered.api.service.ban.BanTypes;
import org.spongepowered.api.tag.BlockTypeTags;
import org.spongepowered.api.tag.EntityTypeTags;
import org.spongepowered.api.tag.FluidTypeTags;
import org.spongepowered.api.tag.ItemTypeTags;
import org.spongepowered.api.world.DefaultWorldKeys;
import org.spongepowered.api.world.server.ServerWorld;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.stream.Collectors;

/*
    This class is a bloated mess.
    Maybe worth splitting this out into their own managers and then using this
    class to redirect the call to its respected manager.
 */
public abstract class SoakServer implements Server {

    private final Supplier<org.spongepowered.api.Server> serverSupplier;
    private final Singleton<SoakMessenger> messenger = new Singleton<>(() -> new SoakMessenger(Sponge.channelManager()));
    private final Singleton<SoakPluginManager> pluginManager = new Singleton<>(() -> new SoakPluginManager(Sponge::pluginManager));
    private final Singleton<SoakUnsafeValues> unsafeValues = new Singleton<>(SoakUnsafeValues::new);
    private final Singleton<SoakItemFactory> itemFactory = new Singleton<>(SoakItemFactory::new);
    private final Singleton<SoakBukkitScheduler> scheduler = new Singleton<>(SoakBukkitScheduler::new);
    private final Singleton<SimpleServicesManager> servicesManager = new Singleton<>(SimpleServicesManager::new);
    private final Singleton<SoakCommandMap> commandMap = new Singleton<>(SoakCommandMap::new);
    private final Singleton<SoakHelpMap> helpMap = new Singleton<>(SoakHelpMap::new);
    private final Singleton<SoakRegistry<org.spongepowered.api.world.generation.structure.Structure, Structure>> structureReg = new Singleton<>(() -> new SoakRegistry<>(RegistryTypes.STRUCTURE, Structure.class, t -> null));
    private final Singleton<SoakRegistry<org.spongepowered.api.world.generation.structure.StructureType, org.bukkit.generator.structure.StructureType>> structureTypeReg = new Singleton<>(() -> new SoakRegistry<>(RegistryTypes.STRUCTURE_TYPE, org.bukkit.generator.structure.StructureType.class, t -> null));
    private final Singleton<SimplePluginManager> simplePluginManagerWrapper = new Singleton<>(() -> {
        var pm = new SimplePluginManager(this, commandMap.get());
        pm.paperPluginManager = pluginManager.get();
        return pm;
    });
    private final Singleton<Map<Class<?>, Registry<?>>> registries = new Singleton<>(() -> Arrays
            .stream(Registry.class.getDeclaredFields())
            .filter(field -> Modifier.isFinal(field.getModifiers()))
            .filter(field -> Modifier.isPublic(field.getModifiers()))
            .filter(field -> Modifier.isStatic(field.getModifiers()))
            .filter(field -> Registry.class.isAssignableFrom(field.getType()))
            .map(field -> {
                try {
                    var reg = (Registry<?>) field.get(null);
                    return reg;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            })
            .filter(Objects::nonNull)
            .filter(reg -> !(reg instanceof SoakRegistry<?, ?>))
            .collect(Collectors.toMap(reg -> {
                if (reg instanceof Registry.SimpleRegistry simpleReg) {
                    var first = simpleReg.iterator().next();
                    return first.getClass();
                }
                var generic = (ParameterizedType) reg.getClass().getGenericInterfaces()[0];
                return (Class<?>) generic.getActualTypeArguments()[0];
            }, reg -> reg)));

    private final java.util.logging.Logger logger;

    public SoakServer(Supplier<org.spongepowered.api.Server> serverSupplier) {
        this.logger = java.util.logging.Logger.getLogger("soak");
        this.logger.setUseParentHandlers(false);
        this.logger.setLevel(Level.INFO);
        var consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new CustomLoggerFormat());
        this.logger.addHandler(consoleHandler);
        this.serverSupplier = serverSupplier;
    }

    @Override
    public boolean isAcceptingTransfers() {
        throw NotImplementedException.createByLazy(Server.class, "isAcceptingTransfers");
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull World world, @NotNull Position position) {
        return isOwnedByCurrentRegion(position.toLocation(world));
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull World world, @NotNull Position position, int squareRadiusChecks) {
        return isOwnedByCurrentRegion(position.toLocation(world), squareRadiusChecks);
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull Location location) {
        return isOwnedByCurrentRegion(location, 1);
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull Location location, int squareRadiusChunks) {
        var chunk = location.getChunk();
        return isOwnedByCurrentRegion(location.getWorld(), chunk.getX(), chunk.getZ(), squareRadiusChunks);
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull World world, int chunkX, int chunkZ) {
        return isOwnedByCurrentRegion(world, chunkX, chunkZ, 1);
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull World world, int chunkX, int chunkZ, int squareRadiusChunks) {
        throw NotImplementedException.createByLazy(Server.class, "isOwnedByCurrentRegion", World.class, int.class, int.class, int.class);
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull Entity entity) {
        return isOwnedByCurrentRegion(entity.getLocation());
    }

    @Override
    public @NotNull RegionScheduler getRegionScheduler() {
        throw NotImplementedException.createByLazy(Server.class, "getRegionScheduler");
    }

    @Override
    public @NotNull AsyncScheduler getAsyncScheduler() {
        throw NotImplementedException.createByLazy(Server.class, "getAsyncScheduler");
    }

    @Override
    public @NotNull GlobalRegionScheduler getGlobalRegionScheduler() {
        throw NotImplementedException.createByLazy(Server.class, "getGlobalRegionScheduler");
    }

    @Override
    public void banIP(@NotNull InetAddress inetAddress) {
        throw NotImplementedException.createByLazy(Server.class, "banIP", InetAddress.class);
    }

    @Override
    public void unbanIP(@NotNull InetAddress inetAddress) {
        throw NotImplementedException.createByLazy(Server.class, "unbanIP", InetAddress.class);
    }

    @Override
    public <B extends BanList<E>, E> @NotNull B getBanList(@NotNull BanListType<B> banListType) {
        throw NotImplementedException.createByLazy(Server.class, "getBanList", BanListType.class);
    }

    @Override
    public @NotNull EntityFactory getEntityFactory() {
        throw NotImplementedException.createByLazy(Server.class, "getEntityFactory");
    }

    @Override
    public @NotNull ServerLinks getServerLinks() {
        throw NotImplementedException.createByLazy(Server.class, "getServerLinks");
    }

    @Override
    public @NotNull ItemStack craftItem(@NotNull ItemStack[] itemStacks, @NotNull World world) {
        throw NotImplementedException.createByLazy(Server.class, "craftItem", ItemStack[].class, World.class);
    }

    @Override
    public @NotNull ItemCraftResult craftItemResult(@NotNull ItemStack[] itemStacks, @NotNull World world, @NotNull Player player) {
        throw NotImplementedException.createByLazy(Server.class, "craftItemResult", ItemStack[].class, World.class, Player.class);
    }

    @Override
    public @NotNull ItemCraftResult craftItemResult(@NotNull ItemStack[] itemStacks, @NotNull World world) {
        throw NotImplementedException.createByLazy(Server.class, "craftItemResult", ItemStack[].class, World.class);
    }

    @Override
    public void updateResources() {
        throw NotImplementedException.createByLazy(Server.class, "updateResources");
    }

    @Override
    public void updateRecipes() {
        throw NotImplementedException.createByLazy(Server.class, "updateRecipes");
    }

    @Override
    public boolean removeRecipe(@NotNull NamespacedKey namespacedKey, boolean b) {
        throw NotImplementedException.createByLazy(Server.class, NamespacedKey.class, boolean.class);
    }

    @Override
    public @Nullable ItemStack createExplorerMap(@NotNull World world, @NotNull Location location, @NotNull org.bukkit.generator.structure.StructureType structureType, @NotNull MapCursor.Type type, int i, boolean b) {
        throw NotImplementedException.createByLazy(Server.class, "createExplorerMap", World.class, Location.class, org.bukkit.generator.structure.StructureType.class, MapCursor.Type.class, int.class, boolean.class);
    }

    public org.spongepowered.api.Server spongeServer() {
        return this.serverSupplier.get();
    }

    @Override
    public @Nullable World getWorld(@NotNull Key key) {
        var namespace = NamespacedKey.fromString(key.asString());
        if (namespace == null) {
            return null;
        }
        return this.getWorld(namespace);
    }

    @Override
    public @Nullable ResourcePack getServerResourcePack() {
        throw NotImplementedException.createByLazy(Server.class, "getServerResourcePack");
    }

    @Override
    public boolean isLoggingIPs() {
        throw NotImplementedException.createByLazy(Server.class, "isLoggingIPs");
    }

    @Override
    public @NotNull ServerTickManager getServerTickManager() {
        throw NotImplementedException.createByLazy(Server.class, "getServerTickManager");
    }

    @Override
    public @NotNull <T extends Keyed> Iterable<Tag<T>> getTags(@NotNull String registry, @NotNull Class<T> clazz) {
        throw NotImplementedException.createByLazy(Server.class, "getTags", String.class, Class.class);
    }

    @Override
    public <T extends Keyed> Tag<T> getTag(@NotNull String registry, @NotNull NamespacedKey tag, @NotNull Class<T> clazz) {
        ResourceKey key = SoakResourceKeyMap.mapToSponge(tag);
        boolean classMatch = clazz.getName().equals(Material.class.getName());
        if (classMatch && registry.equals(Tag.REGISTRY_BLOCKS)) {
            var opTag = FakeRegistryHelper.<org.spongepowered.api.tag.Tag<BlockType>>getFields(BlockTypeTags.class, org.spongepowered.api.tag.Tag.class)
                    .stream()
                    .filter(spongeTag -> spongeTag.key().equals(key))
                    .findAny();
            if (opTag.isPresent()) {
                return (Tag<T>) (Object) new MaterialSetTag(tag, TagHelper.getBlockTypes(opTag.get()).map(block -> SoakBlockMap.toBukkit(block)).collect(Collectors.toList()));
            }
        }
        if (classMatch && registry.equals(Tag.REGISTRY_ITEMS)) {
            var opTag = FakeRegistryHelper.<org.spongepowered.api.tag.Tag<ItemType>>getFields(ItemTypeTags.class, org.spongepowered.api.tag.Tag.class)
                    .stream()
                    .filter(spongeTag -> spongeTag.key().equals(key))
                    .findAny();
            if (opTag.isPresent()) {
                return (Tag<T>) (Object) new MaterialSetTag(tag, TagHelper.getItemTypes(opTag.get()).map(item -> SoakItemStackMap.toBukkit(item)).collect(Collectors.toList()));
            }
        }
        if (clazz.getName().equals(EntityType.class.getName()) && registry.equals(Tag.REGISTRY_ENTITY_TYPES)) {
            var opTag = FakeRegistryHelper.<org.spongepowered.api.tag.Tag<org.spongepowered.api.entity.EntityType<?>>>getFields(EntityTypeTags.class, org.spongepowered.api.tag.Tag.class)
                    .stream()
                    .filter(spongeTag -> spongeTag.key().equals(key))
                    .findAny();
            if (opTag.isPresent()) {
                return (Tag<T>) (Object) new EntitySetTag(tag, TagHelper.getEntityTypes(opTag.get()).map(entity -> SoakEntityMap.toBukkit(entity)).collect(Collectors.toList()));
            }
        }


        //overrides
        if (registry.equals(Tag.REGISTRY_ITEMS) && tag.asString().equals("minecraft:coral_blocks")) {
            return (Tag<T>) (Object) new MaterialSetTag(tag,
                    ItemTypes.registry()
                            .stream()
                            .filter(item -> item.key(RegistryTypes.ITEM_TYPE).value().contains("coral_blocks"))
                            .map(item -> SoakItemStackMap.toBukkit(item))
                            .collect(Collectors.toList()));
        }

        if (registry.equals(Tag.REGISTRY_BLOCKS) && tag.asString()
                .equals("minecraft:wool_carpets")) {
            Set<Material> itemTypes = TagHelper.getBlockTypes(BlockTypeTags.WOOL_CARPETS).map(block -> SoakBlockMap.toBukkit(block)).collect(Collectors.toSet());
            return (Tag<T>) (Object) new MaterialSetTag(tag, itemTypes);
        }

        if (registry.equals(Tag.REGISTRY_ITEMS) && tag.asString().equals("minecraft:wool_carpets")) {
            Set<Material> itemTypes = TagHelper.getItemTypes(ItemTypeTags.WOOL_CARPETS).map(item -> SoakItemStackMap.toBukkit(item)).collect(Collectors.toSet());
            return (Tag<T>) (Object) new MaterialSetTag(tag, itemTypes);

        }

        if (registry.equals(Tag.REGISTRY_FLUIDS) && tag.asString().equals("minecraft:water")) {
            return (Tag<T>) (Object) new SoakFluidTag(FluidTypeTags.WATER);
        }

        if (registry.equals(Tag.REGISTRY_FLUIDS) && tag.asString().equals("minecraft:lava")) {
            return (Tag<T>) (Object) new SoakFluidTag(FluidTypeTags.LAVA);
        }

        if (registry.equals(Tag.REGISTRY_ITEMS) && tag.asString().equals("minecraft:crops")) {
            var items = ItemTypes.registry()
                    .stream()
                    .filter(item -> org.spongepowered.api.item.inventory.ItemStack.of(item)
                            .get(Keys.REPLENISHED_FOOD)
                            .isPresent())
                    .map(item -> SoakItemStackMap.toBukkit(item))
                    .collect(Collectors.toSet());
            return (Tag<T>) (Object) new MaterialSetTag(tag, items);
        }
        if (registry.equals(Tag.REGISTRY_ITEMS) && tag.asString().equals("minecraft:furnace_materials")) {
            var items = ItemTypes.registry()
                    .stream()
                    .filter(item -> org.spongepowered.api.item.inventory.ItemStack.of(item)
                            .get(Keys.MAX_COOK_TIME)
                            .isPresent())
                    .map(item -> SoakItemStackMap.toBukkit(item))
                    .collect(Collectors.toSet());
            return (Tag<T>) (Object) new MaterialSetTag(tag, items);
        }
        SoakManager.getManager().getLogger().warn("No tag found of registry: '" + registry + "' Type: " + clazz.getSimpleName() + " id: " + tag.asString());
        return null;
    }

    public void syncCommands() {
        //Craftbukkit public method to reregister all commands and apply to all players as well as register to brig
        var commandManager = this.spongeServer().commandManager();
        this.spongeServer().streamOnlinePlayers().forEach(commandManager::updateCommandTreeForPlayer);
    }

    @Override
    public @NotNull Server.Spigot spigot() {
        throw NotImplementedException.createByLazy(Server.class, "spigot");
    }

    @Override
    public @NotNull File getWorldContainer() {
        throw NotImplementedException.createByLazy(Server.class, "getWorldContainer");
    }

    @Override
    public @NotNull File getPluginsFolder() {
        return new File("org/soak/generate/bukkit/plugins");
    }

    @Override
    public @NotNull String getName() {
        return "soak";
    }

    @Override
    public @NotNull String getVersion() {
        return "(MC: " + getMinecraftVersion() + ") - " + SoakManager.getManager().getVersion()
                .toString();
    }

    @Override
    public @NotNull String getBukkitVersion() {
        return getMinecraftVersion() + "-R1.0-SNAPSHOT";
    }

    @Override
    public @NotNull String getMinecraftVersion() {
        return Sponge.platform().minecraftVersion().name();
    }

    @Override
    public @NotNull Collection<? extends Player> getOnlinePlayers() {
        if (!Sponge.isServerAvailable()) {
            return Collections.emptySet();
        }
        var builder = CollectionStreamBuilder
                .builder()
                .collection(Sponge.server().onlinePlayers())
                .basicMap(player -> (Player) SoakManager.<WrapperManager>getManager().getMemoryStore().get(player));
        return ListMappingUtils
                .fromStream(
                        builder,
                        () -> Sponge.server().onlinePlayers().stream(),
                        (spongePlayer, soakPlayer) -> ((SoakPlayer) soakPlayer).spongeEntity().equals(spongePlayer),
                        Comparator.comparing(spongePlayer -> spongePlayer.get(Keys.LAST_DATE_JOINED).orElseThrow(() -> new RuntimeException("No value found for last joined on a online player"))))
                .buildList();
    }

    @Override
    public int getMaxPlayers() {
        return this.spongeServer().maxPlayers();
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        throw NotImplementedException.createByLazy(SoakServer.class, "setMaxPlayers", int.class);
    }

    @Override
    public int getPort() {
        return this.spongeServer().boundAddress().map(InetSocketAddress::getPort).orElseThrow(() -> new IllegalStateException("Cannot get ip"));
    }

    @Override
    public int getViewDistance() {
        return this.defaultWorld().properties().viewDistance();
    }

    @Override
    public int getSimulationDistance() {
        throw NotImplementedException.createByLazy(Server.class, "getSimulationDistance");
    }

    @Override
    public @NotNull String getIp() {
        return this.spongeServer().boundAddress().map(ip -> ip.getAddress().toString()).orElseThrow(() -> new IllegalStateException("Cannot get ip"));
    }

    @Override
    public @NotNull String getWorldType() {
        return defaultWorld().worldType().key(RegistryTypes.WORLD_TYPE).formatted();
    }

    @Override
    public boolean getGenerateStructures() {
        throw NotImplementedException.createByLazy(SoakServer.class, "getGenerateStructures");
    }

    @Override
    public int getMaxWorldSize() {
        throw NotImplementedException.createByLazy(SoakServer.class, "getMaxWorldSize");
    }

    @Override
    public boolean getAllowEnd() {
        return this.spongeServer().worldManager().world(DefaultWorldKeys.THE_END).isPresent();
    }

    @Override
    public boolean getAllowNether() {
        return this.spongeServer().worldManager().world(DefaultWorldKeys.THE_NETHER).isPresent();
    }

    @Override
    public @NotNull List<String> getInitialEnabledPacks() {
        throw NotImplementedException.createByLazy(Server.class, "getInitialEnabledPacks");
    }

    @Override
    public @NotNull List<String> getInitialDisabledPacks() {
        throw NotImplementedException.createByLazy(Server.class, "getInitialDisabledPacks");
    }

    @Override
    public @NotNull DataPackManager getDataPackManager() {
        throw NotImplementedException.createByLazy(Server.class, "getDataPackManager");
    }

    @Override
    public @NotNull String getResourcePack() {
        throw NotImplementedException.createByLazy(Server.class, "getResourcePack");
    }

    @Override
    public @NotNull String getResourcePackHash() {
        throw NotImplementedException.createByLazy(Server.class, "getResourcePackHash");
    }

    @Override
    public @NotNull String getResourcePackPrompt() {
        throw NotImplementedException.createByLazy(Server.class, "getResourcePackPrompt");
    }

    @Override
    public boolean isResourcePackRequired() {
        throw NotImplementedException.createByLazy(Server.class, "isResourcePackRequired");
    }

    @Override
    public void setWhitelist(boolean value) {
        throw NotImplementedException.createByLazy(SoakServer.class, "setWhitelist", boolean.class);
    }

    @Override
    public boolean isWhitelistEnforced() {
        throw NotImplementedException.createByLazy(Server.class, "isWhitelistEnforced");
    }

    @Override
    public void setWhitelistEnforced(boolean b) {
        throw NotImplementedException.createByLazy(Server.class, "setWhitelistEnforced", boolean.class);
    }

    @Override
    public @NotNull Set<OfflinePlayer> getWhitelistedPlayers() {
        var whitelistService = this.spongeServer().serviceProvider().whitelistService();
        var userManager = this.spongeServer().userManager();
        var futurePlayers = whitelistService.whitelistedProfiles().thenCompose(profiles -> {
            var futures = profiles.stream().map(userManager::load).toArray(CompletableFuture[]::new);
            return CompletableFuture
                    .allOf(futures)
                    .thenApply(v -> Arrays
                            .stream(futures)
                            .map(future -> {
                                try {
                                    return (Optional<User>) future.get();
                                } catch (InterruptedException | ExecutionException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .map(user -> (OfflinePlayer) new SoakOfflinePlayer(user)).collect(Collectors.toSet()));
        });
        try {
            return futurePlayers.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reloadWhitelist() {
        throw NotImplementedException.createByLazy(SoakServer.class, "reloadWhitelist");
    }

    @Override
    @Deprecated
    public int broadcastMessage(@NotNull String message) {
        return this.broadcast(SoakMessageMap.toComponent(message));
    }

    @Override
    public @NotNull String getUpdateFolder() {
        return getUpdateFolderFile().getAbsolutePath();
    }

    @Override
    public @NotNull File getUpdateFolderFile() {
        return new File("mods/bukkit/update");
    }

    @Override
    public long getConnectionThrottle() {
        throw NotImplementedException.createByLazy(SoakServer.class, "getConnectionThrottle");
    }

    @Override
    public int getTicksPerAnimalSpawns() {
        throw NotImplementedException.createByLazy(SoakServer.class, "getTicksPerAnimalSpawn");
    }

    @Override
    public int getTicksPerMonsterSpawns() {
        throw NotImplementedException.createByLazy(SoakServer.class, "getTicksPerMonsterSpawn");
    }

    @Override
    public int getTicksPerWaterSpawns() {
        throw NotImplementedException.createByLazy(SoakServer.class, "getTicksPerWaterSpawns");
    }

    @Override
    public int getTicksPerWaterAmbientSpawns() {
        throw NotImplementedException.createByLazy(SoakServer.class, "getTicksPerWaterAnbientSpawns");
    }

    @Override
    public int getTicksPerWaterUndergroundCreatureSpawns() {
        throw NotImplementedException.createByLazy(Server.class, "getTicksPerWaterUndergroundCreatureSpawns");
    }

    @Override
    public int getTicksPerAmbientSpawns() {
        throw NotImplementedException.createByLazy(SoakServer.class, "getTicksPerAmbientSpawns");
    }

    @Override
    public int getTicksPerSpawns(@NotNull SpawnCategory spawnCategory) {
        throw NotImplementedException.createByLazy(Server.class, "getTicksPerSpawns", SpawnCategory.class);
    }

    @Override
    public @Nullable Player getPlayerExact(@NotNull String name) {
        return this.getOnlinePlayers().stream().filter(player -> player.getName().equals(name)).findAny().orElse(null);

    }

    @Override
    public @NotNull List<Player> matchPlayer(@NotNull String name) {
        throw NotImplementedException.createByLazy(SoakServer.class, "matchPlayer", String.class);
    }

    @Override
    public @Nullable UUID getPlayerUniqueId(@NotNull String playerName) {
        Player player = getPlayerExact(playerName);
        if (player == null) {
            return null;
        }
        return player.getUniqueId();
    }

    @Override
    public @Nullable Player getPlayer(@NotNull UUID uuid) {
        return Sponge.server().player(uuid).map(player -> SoakManager.<WrapperManager>getManager().getMemoryStore().get(player)).orElse(null);
    }

    @Override
    public @Nullable Player getPlayer(@NotNull String s) {
        return Sponge.server().player(s).map(player -> SoakManager.<WrapperManager>getManager().getMemoryStore().get(player)).orElse(null);

    }

    public @NotNull SoakPluginManager getSoakPluginManager() {
        return this.pluginManager.get();
    }

    @Override
    public @NotNull SimplePluginManager getPluginManager() {
        return this.simplePluginManagerWrapper.get();
    }

    @Override
    public @NotNull BukkitScheduler getScheduler() {
        return this.scheduler.get();
    }

    @Override
    public @NotNull ServicesManager getServicesManager() {
        return this.servicesManager.get();
    }

    @Override
    public @NotNull List<World> getWorlds() {
        var builder = CollectionStreamBuilder
                .builder()
                .collection(this.spongeServer().worldManager().worlds())
                .basicMap(world -> (World) SoakManager.<WrapperManager>getManager().getMemoryStore().get(world));
        return ListMappingUtils.fromStream(builder, () -> this.spongeServer().worldManager().worlds().stream(), (spongeWorld, soakWorld) -> ((SoakWorld) soakWorld).sponge().equals(spongeWorld), Comparator.comparing(world -> world.key().formatted()))
                .buildList();
    }

    @Override
    public boolean isTickingWorlds() {
        throw NotImplementedException.createByLazy(Server.class, "isTickingWorlds");
    }

    @Override
    public @Nullable World createWorld(@NotNull WorldCreator creator) {
        throw NotImplementedException.createByLazy(SoakServer.class, "createWorld", WorldCreator.class);
    }

    @Override
    public boolean unloadWorld(@NotNull World world, boolean save) {
        var spongeWorld = ((SoakWorld) world).sponge();
        if (save) {
            try {
                spongeWorld.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        var worldManager = this.spongeServer().worldManager();
        try {
            return worldManager.unloadWorld(spongeWorld).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull WorldBorder createWorldBorder() {
        throw NotImplementedException.createByLazy(Server.class, "createWorldBorder");
    }

    @Override
    public @Nullable MapView getMap(int id) {
        throw NotImplementedException.createByLazy(SoakServer.class, "getMap", int.class);
    }

    @Override
    public @NotNull MapView createMap(@NotNull World world) {
        throw NotImplementedException.createByLazy(SoakServer.class, "createMap", World.class);
    }

    @Override
    public @NotNull ItemStack createExplorerMap(@NotNull World world, @NotNull Location location, @NotNull StructureType structureType) {
        throw NotImplementedException.createByLazy(SoakServer.class,
                "createExplorerMap",
                World.class,
                Location.class,
                StructureType.class);
    }

    @Override
    public @NotNull ItemStack createExplorerMap(@NotNull World world, @NotNull Location location, @NotNull StructureType structureType, int radius, boolean findUnexplored) {
        throw NotImplementedException.createByLazy(SoakServer.class,
                "createExplorerMap",
                World.class,
                Location.class,
                StructureType.class,
                boolean.class);
    }

    @Override
    public void reload() {
        SoakManager.getManager().getLogger()
                .warn("A Bukkit plugin attempted to reload the plugin list. This is not possible in Sponge, reloading the data instead");
        reloadData();
    }

    @Override
    public void reloadData() {
        throw NotImplementedException.createByLazy(SoakServer.class, "reloadData");
    }

    @Override
    public @Nullable PluginCommand getPluginCommand(@NotNull String name) {
        PluginCommand pluginCommand = SoakManager.getManager().getBukkitContainers()
                .sorted(Comparator.comparing(pl -> pl.metadata().id()))
                .flatMap(pl -> SoakManager.<WrapperManager>getManager().getBukkitCommands(pl.getBukkitInstance()).stream())
                .filter(cmd -> {
                    if (cmd.getName().equalsIgnoreCase(name)) {
                        return true;
                    }
                    return cmd.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(name));
                })
                .filter(cmd -> cmd instanceof PluginCommand)
                .findFirst()
                .map(cmd -> (PluginCommand) cmd)
                .orElse(null);
        if (pluginCommand == null) {
            SoakManager.<WrapperManager>getManager().getLogger().warn("A Bukkit plugin attempted to access the command '" + name + "'. It however does not exist");
        }
        return pluginCommand;

    }

    @Override
    public void savePlayers() {
        throw NotImplementedException.createByLazy(SoakServer.class, "savePlayers");
    }

    @Override
    public boolean dispatchCommand(@NotNull CommandSender sender, @NotNull String commandLine) throws CommandException {
        throw NotImplementedException.createByLazy(SoakServer.class,
                "dispatchCommand",
                CommandSender.class,
                String.class);
    }

    @Override
    public boolean addRecipe(@Nullable Recipe recipe) {
        return addRecipe(recipe, false);
    }

    @Override
    public boolean addRecipe(@Nullable Recipe recipe, boolean resendRecipes) {
        throw NotImplementedException.createByLazy(SoakServer.class, "addRecipe", Recipe.class, boolean.class);
    }

    @Override
    public @Nullable Recipe getCraftingRecipe(@NotNull ItemStack[] itemStacks, @NotNull World world) {
        throw NotImplementedException.createByLazy(Server.class, "getCraftingRecipe", ItemStack.class, World.class);
    }

    @Override
    public @NotNull ItemStack craftItem(@NotNull ItemStack[] itemStacks, @NotNull World world, @NotNull Player player) {
        throw NotImplementedException.createByLazy(Server.class, "craftItem", ItemStack.class, World.class, Player.class);
    }

    @Override
    public @NotNull Iterator<Recipe> recipeIterator() {
        return Sponge
                .server()
                .recipeManager()
                .all()
                .stream()
                .<Recipe>map(recipe -> {
                    try {
                        return SoakRecipeMap.toBukkit(recipe);
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .iterator();
    }

    @Override
    public void clearRecipes() {
        throw NotImplementedException.createByLazy(SoakServer.class, "clearRecipes");
    }

    @Override
    public void resetRecipes() {
        throw NotImplementedException.createByLazy(SoakServer.class, "resetRecipes");
    }

    @Override
    public boolean removeRecipe(@NotNull NamespacedKey key) {
        throw NotImplementedException.createByLazy(Server.class, "removeRecipe", NamespacedKey.class);
    }

    @Override
    public @NotNull Map<String, String[]> getCommandAliases() {
        throw NotImplementedException.createByLazy(SoakServer.class, "getCommandAliases", Map.class);
    }

    public ServerWorld defaultWorld() {
        return this.spongeServer()
                .worldManager()
                .world(DefaultWorldKeys.DEFAULT)
                .orElseThrow(() -> new RuntimeException("default world is not loaded"));
    }

    @Override
    public int getSpawnRadius() {
        return SoakManager.<WrapperManager>getManager().getServerProperties().spawnProtection().orElse();
    }

    @Override
    public void setSpawnRadius(int value) {
        SoakManager.<WrapperManager>getManager().getServerProperties().spawnProtection().setMemoryValue(value);
    }

    @Override
    public boolean shouldSendChatPreviews() {
        throw NotImplementedException.createByLazy(Server.class, "shouldSendChatPreviews");
    }

    @Override
    public boolean isEnforcingSecureProfiles() {
        throw NotImplementedException.createByLazy(Server.class, "isEnforcingSecureProfiles");
    }

    @Override
    public boolean getHideOnlinePlayers() {
        throw NotImplementedException.createByLazy(Server.class, "getHideOnlinePlayers");
    }

    @Override
    public boolean getOnlineMode() {
        return this.spongeServer().isOnlineModeEnabled();
    }

    @Override
    public boolean getAllowFlight() {
        throw NotImplementedException.createByLazy(SoakServer.class, "getAllowFlight");
    }

    @Override
    public boolean isHardcore() {
        return this.spongeServer().isHardcoreModeEnabled();
    }

    @Override
    public @NotNull OfflinePlayer getOfflinePlayer(@NotNull String name) {
        var cached = getOfflinePlayerIfCached(name);
        if (cached != null) {
            return cached;
        }
        var future = Sponge.server()
                .gameProfileManager()
                .profile(name)
                .thenCompose(profile -> Sponge.server().userManager().loadOrCreate(profile.uuid()));
        return new SoakOfflinePlayer(future);
    }

    @Override
    public @Nullable OfflinePlayer getOfflinePlayerIfCached(@NotNull String name) {
        if (Sponge.server().userManager().streamOfMatches(name).findAny().isEmpty()) {
            return null;
        }
        var future = Sponge.server()
                .userManager()
                .load(name)
                .thenApply(op -> op.orElseThrow(() -> new RuntimeException("Failed to load cached player")));
        return new SoakOfflinePlayer(future);
    }

    @Override
    public @NotNull OfflinePlayer getOfflinePlayer(@NotNull UUID id) {
        var future = Sponge.server().userManager().loadOrCreate(id);
        return new SoakOfflinePlayer(future);
    }

    @Override
    public org.bukkit.profile.@NotNull PlayerProfile createPlayerProfile(@Nullable UUID uuid, @Nullable String s) {
        throw NotImplementedException.createByLazy(Server.class, "createPlayerProfile", UUID.class, String.class);
    }

    @Override
    public org.bukkit.profile.@NotNull PlayerProfile createPlayerProfile(@NotNull UUID uuid) {
        throw NotImplementedException.createByLazy(Server.class, "createPlayerProfile", UUID.class);
    }

    @Override
    public org.bukkit.profile.@NotNull PlayerProfile createPlayerProfile(@NotNull String s) {
        throw NotImplementedException.createByLazy(Server.class, "createPlayerProfile", String.class);
    }

    @Override
    public @NotNull Set<String> getIPBans() {
        var banService = this.spongeServer().serviceProvider().banService();
        var futureIps = banService.ipBans().thenApply(ips -> CollectionStreamBuilder.builder().collection(ips).basicMap(ip -> ip.address().toString()).buildSet());
        try {
            return futureIps.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void banIP(@NotNull String arg0) {
        var banService = this.spongeServer().serviceProvider().banService();
        try {
            var address = InetAddress.getByName(arg0);
            var ban = Ban.builder().address(address).type(BanTypes.IP).build();
            banService.add(ban);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void unbanIP(@NotNull String arg0) {
        var banService = this.spongeServer().serviceProvider().banService();
        try {
            var address = InetAddress.getByName(arg0);
            banService.pardon(address);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public @NotNull Set<OfflinePlayer> getBannedPlayers() {
        var banService = Sponge.server().serviceProvider().banService();
        var userManager = Sponge.server().userManager();
        CompletableFuture<Set<OfflinePlayer>> profiles = banService
                .profileBans()
                .thenCompose(collection -> {
                    CompletableFuture<Optional<User>>[] futures = collection.stream().map(profile -> userManager.load(profile.profile())).toArray(CompletableFuture[]::new);
                    return CompletableFuture
                            .allOf(futures)
                            .thenApply(v -> Arrays
                                    .stream(futures)
                                    .map(f -> {
                                        try {
                                            return f.get();
                                        } catch (InterruptedException | ExecutionException e) {
                                            throw new RuntimeException(e);
                                        }
                                    })
                                    .filter(Optional::isPresent)
                                    .map(opUser -> new SoakOfflinePlayer(opUser.get()))
                                    .collect(Collectors.toSet()));
                });

        try {
            return profiles.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull BanList getBanList(@NotNull BanList.Type type) {
        throw NotImplementedException.createByLazy(Server.class, "getBanList", BanList.Type.class);
    }

    @Override
    public @NotNull Set<OfflinePlayer> getOperators() {
        throw NotImplementedException.createByLazy(Server.class, "getOperators");
    }

    @Override
    public @NotNull GameMode getDefaultGameMode() {
        return SoakGameModeMap.toBukkit(this.spongeServer().gameMode());
    }

    @Override
    public void setDefaultGameMode(@NotNull GameMode arg0) {
        throw NotImplementedException.createByLazy(Server.class, "setDefaultGameMode", GameMode.class);
    }

    @Override
    public @NotNull ConsoleCommandSender getConsoleSender() {
        return new SoakConsoleCommandSender();
    }

    @Override
    public @NotNull CommandSender createCommandSender(@NotNull Consumer<? super Component> consumer) {
        throw NotImplementedException.createByLazy(Server.class, "createCommandSender", Consumer.class);
    }

    @Override
    public OfflinePlayer[] getOfflinePlayers() {
        return Sponge.server().userManager().streamAll().map(SoakLoadingUser::new).toArray(OfflinePlayer[]::new);
    }

    @Override
    public @NotNull Messenger getMessenger() {
        return this.messenger.get();
    }

    @Override
    public @NotNull SoakHelpMap getHelpMap() {
        return this.helpMap.get();
    }

    @Override
    public @NotNull Inventory createInventory(InventoryHolder arg0, @NotNull InventoryType arg1) {
        throw NotImplementedException.createByLazy(Server.class,
                "createInventory",
                InventoryHolder.class,
                InventoryType.class);
    }

    @Override
    public @NotNull Inventory createInventory(InventoryHolder arg0, @NotNull InventoryType arg1, @NotNull Component arg2) {
        throw NotImplementedException.createByLazy(Server.class,
                "createInventory",
                InventoryHolder.class,
                InventoryType.class,
                Component.class);
    }

    @Deprecated
    @Override
    public @NotNull Inventory createInventory(InventoryHolder arg0, @NotNull InventoryType arg1, @NotNull String arg2) {
        return createInventory(arg0, arg1, SoakMessageMap.toComponent(arg2));
    }

    @Override
    public @NotNull Inventory createInventory(InventoryHolder arg0, int arg1) {
        return createChestInventory(arg0, arg1, null);
    }

    @Override
    public @NotNull Inventory createInventory(InventoryHolder arg0, int arg1, @NotNull Component arg2) {
        return createChestInventory(arg0, arg1, arg2);
    }

    private Inventory createChestInventory(InventoryHolder holder, int size, @Nullable Component title) {
        int rows = (size / 9);
        var containerType = InventoryHelper.toChestContainerType(rows);
        var plugin = GeneralHelper.fromStackTrace();
        var inventory = ViewableInventory.builder().type(containerType).completeStructure().plugin(plugin).build();
        //TODO holder
        var bukkitInv = new SoakInventory<>(inventory);
        bukkitInv.setRequestedTitle(title);
        return bukkitInv;
    }

    @Deprecated
    @Override
    public @NotNull Inventory createInventory(InventoryHolder arg0, int arg1, @NotNull String arg2) {
        return createInventory(arg0, arg1, SoakMessageMap.toComponent(arg2));
    }

    @Deprecated
    @Override
    public @NotNull Merchant createMerchant(String arg0) {
        throw NotImplementedException.createByLazy(Server.class, "createMerchant", String.class);
    }

    @Override
    public int getMaxChainedNeighborUpdates() {
        throw NotImplementedException.createByLazy(Server.class, "getMaxChainedNeighborUpdates");
    }

    @Override
    public @NotNull Merchant createMerchant(Component arg0) {
        throw NotImplementedException.createByLazy(Server.class, "createMerchant", Component.class);
    }

    @Override
    public int getMonsterSpawnLimit() {
        throw NotImplementedException.createByLazy(Server.class, "getMonsterSpawnLimit");
    }

    @Override
    public int getAnimalSpawnLimit() {
        throw NotImplementedException.createByLazy(Server.class, "getAnimalSpawnLimit");
    }

    @Override
    public int getWaterAnimalSpawnLimit() {
        throw NotImplementedException.createByLazy(Server.class, "getWaterAnimalSpawnLimit");
    }

    @Override
    public int getWaterAmbientSpawnLimit() {
        throw NotImplementedException.createByLazy(Server.class, "getWaterAmbientSpawnLimit");
    }

    @Override
    public int getWaterUndergroundCreatureSpawnLimit() {
        throw NotImplementedException.createByLazy(Server.class, "getWaterUndergroundCreatureSpawnLimit");
    }

    @Override
    public int getAmbientSpawnLimit() {
        throw NotImplementedException.createByLazy(Server.class, "getAmbientSpawnLimit");
    }

    @Override
    public int getSpawnLimit(@NotNull SpawnCategory spawnCategory) {
        throw NotImplementedException.createByLazy(Server.class, "getSpawnLimit", SpawnCategory.class);
    }

    @Override
    public boolean isPrimaryThread() {
        return this.spongeServer().onMainThread();
    }

    @Override
    public void setMotd(@NotNull String s) {
        motd(SoakMessageMap.toComponent(s));
    }

    @Override
    public void motd(@NotNull Component component) {
        throw NotImplementedException.createByLazy(Server.class, "motd", Component.class);
    }

    @Override
    public @NotNull Component motd() {
        return this.spongeServer().motd();
    }

    @Deprecated
    @Override
    public @NotNull String getMotd() {
        return LegacyComponentSerializer.legacySection().serialize(motd());
    }

    @Override
    public Component shutdownMessage() {
        throw NotImplementedException.createByLazy(Server.class, "shutdownMessage");
    }

    @Deprecated
    @Override
    public String getShutdownMessage() {
        throw NotImplementedException.createByLazy(Server.class, "getShutdownMessage");
    }

    @Override
    public Warning.@NotNull WarningState getWarningState() {
        throw NotImplementedException.createByLazy(Server.class, "getWarningState");
    }

    @Override
    public @NotNull ItemFactory getItemFactory() {
        return this.itemFactory.get();
    }

    @Override
    public @NotNull ScoreboardManager getScoreboardManager() {
        throw NotImplementedException.createByLazy(Server.class, "getScoreboardManager");
    }

    @Override
    public @NotNull Criteria getScoreboardCriteria(@NotNull String s) {
        throw NotImplementedException.createByLazy(Server.class, "getScoreboardCriteria", String.class);
    }

    @Override
    public CachedServerIcon getServerIcon() {
        throw NotImplementedException.createByLazy(Server.class, "getServerIcon");
    }

    @Override
    public @NotNull CachedServerIcon loadServerIcon(@NotNull BufferedImage arg0) {
        throw NotImplementedException.createByLazy(Server.class, "loadServerIcon", BufferedImage.class);
    }

    @Override
    public @NotNull CachedServerIcon loadServerIcon(@NotNull File arg0) {
        throw NotImplementedException.createByLazy(Server.class, "loadServerIcon", File.class);
    }

    @Override
    public int getIdleTimeout() {
        throw NotImplementedException.createByLazy(Server.class, "getIdleTimeout");
    }

    @Override
    public void setIdleTimeout(int arg0) {
        throw NotImplementedException.createByLazy(Server.class, "setIdleTimeout", int.class);
    }

    @Override
    public ChunkGenerator.@NotNull ChunkData createChunkData(@NotNull World arg0) {
        throw NotImplementedException.createByLazy(Server.class, "createChunkData", World.class);
    }

    @Override
    public @NotNull KeyedBossBar createBossBar(@NotNull NamespacedKey arg0, String arg1, @NotNull BarColor arg2, @NotNull BarStyle arg3, BarFlag[] arg4) {
        throw NotImplementedException.createByLazy(Server.class,
                "createBossBar",
                NamespacedKey.class,
                String.class,
                BarColor.class,
                BarStyle.class,
                BarFlag[].class);
    }

    @Override
    public @NotNull BossBar createBossBar(String arg0, @NotNull BarColor arg1, @NotNull BarStyle arg2, BarFlag[] arg3) {
        throw NotImplementedException.createByLazy(Server.class,
                "createBossBar",
                String.class,
                BarColor.class,
                BarStyle.class,
                BarFlag[].class);
    }

    @Override
    public @NotNull Iterator<KeyedBossBar> getBossBars() {
        throw NotImplementedException.createByLazy(Server.class, "getBossBars");
    }

    @Override
    public KeyedBossBar getBossBar(@NotNull NamespacedKey arg0) {
        throw NotImplementedException.createByLazy(Server.class, "getBossBar", NamespacedKey.class);
    }

    @Override
    public boolean removeBossBar(@NotNull NamespacedKey arg0) {
        throw NotImplementedException.createByLazy(Server.class, "removeBossBar", NamespacedKey.class);
    }

    @Override
    public Entity getEntity(@NotNull UUID arg0) {
        return getWorlds().stream().map(world -> world.getEntity(arg0)).filter(Objects::nonNull).findAny().orElse(null);
    }

    @Override
    public double[] getTPS() {
        throw NotImplementedException.createByLazy(Server.class, "getTPS");
    }

    @Override
    public long[] getTickTimes() {
        throw NotImplementedException.createByLazy(Server.class, "getTickTimes");
    }

    @Override
    public double getAverageTickTime() {
        return this.spongeServer().averageTickTime();
    }

    @Override
    public @NotNull SoakCommandMap getCommandMap() {
        return this.commandMap.get();
    }

    @Override
    public Advancement getAdvancement(@NotNull NamespacedKey arg0) {
        throw NotImplementedException.createByLazy(Server.class, "getAdvancement", NamespacedKey.class);
    }

    @Override
    public @NotNull Iterator<Advancement> advancementIterator() {
        throw NotImplementedException.createByLazy(Server.class, "advancementIterator");
    }

    @Override
    public @NotNull BlockData createBlockData(@NotNull String blockStateString) {
        var spongeState = BlockState.fromString(blockStateString);
        return SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongeState);
    }

    @Override
    public @NotNull BlockData createBlockData(@NotNull Material material) {
        return material.createBlockData();
    }

    @Override
    public @NotNull BlockData createBlockData(@Nullable Material material, @Nullable String s) throws IllegalArgumentException {
        if (material == null && s == null) {
            throw new IllegalArgumentException("Both material and string cannot be null");
        }
        if (material == null && s.startsWith("[")) {
            throw new IllegalArgumentException("Cannot find material from '" + s + "'");
        }
        if (material != null && (s == null || s.startsWith("["))) {
            return createBlockData(material);
        }
        if (s.startsWith("[")) {
            s = SoakBlockMap.toSponge(material).orElseThrow(() -> new IllegalStateException("Item cannot be converted to BlockState")).key(RegistryTypes.BLOCK_TYPE).formatted() + s;
        }
        var blockState = BlockState.fromString(s);
        return new SoakBlockData(blockState);
    }

    @Override
    public @NotNull BlockData createBlockData(@NotNull Material material, @Nullable Consumer<? super BlockData> consumer) {
        var result = createBlockData(material);
        if (consumer != null) {
            consumer.accept(result);
        }
        return result;
    }

    @Override
    public LootTable getLootTable(@NotNull NamespacedKey arg0) {
        throw NotImplementedException.createByLazy(Server.class, "getLootTable", NamespacedKey.class);
    }

    @Override
    public @NotNull List<Entity> selectEntities(@NotNull CommandSender sender, @NotNull String selector) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(Server.class, "selectEntities", CommandSender.class, String.class);
    }

    @Override
    public @NotNull StructureManager getStructureManager() {
        throw NotImplementedException.createByLazy(Server.class, "getStructureManager");
    }

    @Override
    public @Nullable <T extends Keyed> Registry<T> getRegistry(@NotNull Class<T> aClass) {
        if (aClass.isAssignableFrom(Structure.class)) {
            return (Registry<T>) structureReg.get();
        }
        if (aClass.isAssignableFrom(org.bukkit.generator.structure.StructureType.class)) {
            return (Registry<T>) structureTypeReg.get();
        }
        return (Registry<T>) this.registries.get().get(aClass);

    }

    @Override
    public void reloadPermissions() {
        throw NotImplementedException.createByLazy(Server.class, "reloadPermissions");
    }

    @Override
    public boolean reloadCommandAliases() {
        throw NotImplementedException.createByLazy(Server.class, "reloadCommandAliases");
    }

    @Override
    public boolean suggestPlayerNamesWhenNullTabCompletions() {
        throw NotImplementedException.createByLazy(Server.class, "suggestPlayerNamesWhenNullTabCompletions");
    }

    @Override
    public @NotNull String getPermissionMessage() {
        return LegacyComponentSerializer.legacySection().serialize(permissionMessage());
    }

    @Override
    public @NotNull Component permissionMessage() {
        return SoakManager.ifManager(SoakExternalManager.class, em -> em.getConfig().getNoPermissionMessage()).orElseGet(() -> Component.text("Do not have permission").color(NamedTextColor.RED));
    }

    @Override
    public @NotNull PlayerProfile createProfile(@NotNull UUID id) {
        var profileManager = Sponge.server().gameProfileManager();
        var opProfile = profileManager.cache().findById(id).map(profile -> new SoakPlayerProfile(profile, true));
        if (opProfile.isPresent()) {
            return opProfile.get();
        }
        try {
            //blocking -> need to fix this
            return profileManager.uncached()
                    .profile(id).thenApply(profile -> new SoakPlayerProfile(profile, false)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull PlayerProfile createProfile(@Nullable UUID uuid, @Nullable String name) {
        if (uuid == null) {
            if (name == null) {
                throw new IllegalArgumentException("Id or name must be provided");
            }
            return createProfile(name);
        }
        var profileManager = Sponge.server().gameProfileManager();
        var opProfile = profileManager.cache().findById(uuid).map(profile -> name == null ? profile : profile.withName(name)).map(profile -> new SoakPlayerProfile(profile, true));
        if (opProfile.isPresent()) {
            return opProfile.get();
        }
        try {
            //blocking -> need to fix this
            return profileManager.uncached()
                    .profile(uuid).thenApply(profile -> new SoakPlayerProfile(name == null ? profile : profile.withName(name), false)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull PlayerProfile createProfileExact(@Nullable UUID uuid, @Nullable String s) {
        return this.createProfile(uuid, s);
    }

    @Override
    public @NotNull PlayerProfile createProfile(@NotNull String name) {
        var profileManager = Sponge.server().gameProfileManager();
        var opProfile = profileManager.cache().findByName(name).map(profile -> new SoakPlayerProfile(profile, true));
        if (opProfile.isPresent()) {
            return opProfile.get();
        }
        try {
            //blocking -> need to fix this
            return profileManager.uncached()
                    .profile(name).thenApply(profile -> new SoakPlayerProfile(profile, false)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getCurrentTick() {
        throw NotImplementedException.createByLazy(Server.class, "getCurrentTick");
    }

    @Override
    public boolean isStopping() {
        throw NotImplementedException.createByLazy(Server.class, "isStopping");
    }

    @Override
    public @NotNull MobGoals getMobGoals() {
        throw NotImplementedException.createByLazy(Server.class, "getMobGoals");
    }

    @Override
    public @NotNull DatapackManager getDatapackManager() {
        throw NotImplementedException.createByLazy(Server.class, "getDatapackManager");
    }

    @Override
    public @NotNull PotionBrewer getPotionBrewer() {
        throw NotImplementedException.createByLazy(Server.class, "getPotionBrewer");
    }

    @Override
    public boolean hasWhitelist() {
        return this.spongeServer().isWhitelistEnabled();
    }

    @Override
    public void shutdown() {
        this.spongeServer().shutdown();
    }

    @Deprecated
    @Override
    public @NotNull UnsafeValues getUnsafe() {
        return this.unsafeValues.get();
    }

    @Override
    public @NotNull java.util.logging.Logger getLogger() {
        return this.logger;
    }

    @Deprecated
    @Override
    public int broadcast(@NotNull String arg0, @NotNull String arg1) {
        return broadcast(SoakMessageMap.toComponent(arg0), arg1);
    }

    @Override
    public int broadcast(@NotNull Component message, @NotNull String from) {
        var newComponent = Component.text("[" + from + "] ").color(NamedTextColor.GOLD).append(message);
        return broadcast(newComponent);
    }

    @Override
    public int broadcast(@NotNull Component message) {
        Audience audience = this.spongeServer().broadcastAudience();
        Unfinal<Integer> count = new Unfinal<>(0);
        audience.forEachAudience((au) -> count.set(count.get() + 1));
        audience.sendMessage(message);
        return count.get();
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return this.spongeServer().audiences();
    }

    @Override
    public void sendPluginMessage(@NotNull Plugin source, @NotNull String channel, byte[] message) {
        throw NotImplementedException.createByLazy(Server.class,
                "sendPluginMessage",
                Plugin.class,
                String.class,
                byte[].class);
    }

    @Override
    public @NotNull Set<String> getListeningPluginChannels() {
        throw NotImplementedException.createByLazy(Server.class, "getListeningPluginChannels");
    }
}
