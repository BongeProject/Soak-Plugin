package org.soak.wrapper;

import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import io.papermc.paper.inventory.tooltip.TooltipContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.damage.DamageEffect;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CreativeCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.generate.bukkit.MaterialList;
import org.soak.map.item.SoakItemStackMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.inventory.SoakItemStack;
import org.soak.wrapper.plugin.lifecycle.event.SoakLifecycleEventManager;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.InstrumentTypes;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.item.potion.PotionTypes;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;

@SuppressWarnings("deprecation")
public class SoakUnsafeValues implements UnsafeValues {

    //Hidden method used by some plugins
    //these are unique to every minecraft version
    public String getMappingsVersion() {
        return "7092ff1ff9352ad7e2260dc150e6a3ec";
    }

    @Override
    public GsonComponentSerializer colorDownsamplingGsonComponentSerializer() {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "colorDownsamplingGsonComponentSerializer");
    }

    @Override
    public ComponentFlattener componentFlattener() {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "componentFlattener");
    }

    @Override
    public GsonComponentSerializer gsonComponentSerializer() {
        return GsonComponentSerializer.gson();
    }

    @Override
    public LegacyComponentSerializer legacyComponentSerializer() {
        return LegacyComponentSerializer.legacySection();
    }

    @Override
    public Component resolveWithContext(Component component, CommandSender commandSender, Entity entity, boolean b) throws IOException {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "resolveWithContext", Component.class, CommandSender.class, Entity.class, boolean.class);
    }

    @Override
    public void reportTimings() {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "reportTimings");
    }

    @Override
    public Material toLegacy(Material arg0) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "toLegacy", Material.class);
    }

    @Override
    public Material fromLegacy(MaterialData arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "fromLegacy", MaterialData.class, boolean.class);
    }

    @Override
    public Material fromLegacy(MaterialData arg0) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "fromLegacy", MaterialData.class);
    }

    @Override
    public BlockData fromLegacy(Material arg0, byte arg1) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "fromLegacy", Material.class, byte.class);
    }

    @Override
    public Material fromLegacy(Material arg0) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "fromLegacy", Material.class);
    }

    @Override
    public Material getMaterial(String arg0, int arg1) {
        //DONT KNOW WHAT THE NUMBER IS, BUT STRING LOOKS LIKE MATERIAL NAME
        return (Material) MaterialList.getMaterial(arg0);
    }

    @Override
    public int getDataVersion() {
        return Sponge.platform().minecraftVersion().dataVersion().orElseThrow();
    }

    @Override
    public ItemStack modifyItemStack(ItemStack itemStack, String jsonString) {
        try {
            var jsonRoot = GsonConfigurationLoader.builder().buildAndLoadString(jsonString);
            var spongeItemStack = SoakItemStackMap.toSponge(itemStack);
            jsonRoot.childrenMap().entrySet().stream().map(entry -> {
                        var keyName = entry.getKey().toString();
                        var valueNode = entry.getValue();

                        return switch (keyName) {
                            case "Potion" -> {
                                var potionTypeId = Objects.requireNonNull(valueNode.getString());
                                var potionType = PotionTypes.registry().findEntry(ResourceKey.resolve(potionTypeId));
                                if (potionType.isEmpty()) {
                                    SoakManager.getManager().getLogger().warn("Could not get value of '{}' for key 'PotionType'", potionTypeId);
                                    yield null;
                                }
                                yield Map.entry(Keys.POTION_TYPE, potionType.get());
                            }
                            case "Fireworks" -> {
                                //this is probably wrong
                                var flightSeconds = valueNode.node("Flight").getDouble();
                                var flightTicks = (long) (flightSeconds * 20);
                                yield Map.entry(Keys.FIREWORK_FLIGHT_MODIFIER, Ticks.of(flightTicks));
                            }
                            case "instrument" -> {
                                var instrumentId = Objects.requireNonNull(valueNode.getString());
                                var instrumentType = InstrumentTypes.registry().findEntry(ResourceKey.resolve(instrumentId));
                                if (instrumentType.isEmpty()) {
                                    SoakManager.getManager().getLogger().warn("Could not get value of '{}' for key 'InstrumentType'", instrumentId);
                                    yield null;
                                }
                                yield Map.entry(Keys.INSTRUMENT_TYPE, instrumentType.get());
                            }
                            case "Levels" -> {
                                //beacon levels
                                var beaconLevel = valueNode.getDouble();
                                SoakManager.getManager().getLogger().warn("Could not get value of '{}' for key 'Beacon level'", beaconLevel);
                                yield null;
                            }
                            default -> throw new IllegalStateException("Unknown Item Key of: " + keyName);
                        };
                    })
                    .filter(Objects::nonNull)
                    .forEach(entry -> offer(spongeItemStack, entry.getKey(), entry.getValue()));

            return SoakItemStackMap.toBukkit(spongeItemStack);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return itemStack;
    }

    private <T> void offer(org.spongepowered.api.item.inventory.ItemStack stack, Key<?> key, Object object) {
        var genericKey = (Key<? extends Value<T>>) key;
        stack.offer(genericKey, (T) object);
    }

    @Override
    public void checkSupported(PluginDescriptionFile pdf) throws InvalidPluginException {
        String apiVersion = pdf.getAPIVersion();
        if (apiVersion == null) {
            throw new InvalidPluginException("Legacy plugins are not supported on Soak");
        }
    }

    @Override
    public byte[] processClass(PluginDescriptionFile arg0, String arg1, byte[] arg2) {
        //TODO no idea what this does
        return arg2;
    }

    @Override
    public Advancement loadAdvancement(NamespacedKey arg0, String arg1) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "loadAdvancement", NamespacedKey.class, String.class);
    }

    @Override
    public boolean removeAdvancement(NamespacedKey arg0) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "removeAdvancement", NamespacedKey.class);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(Material material, EquipmentSlot equipmentSlot) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "getDefaultAttributeModifiers", Material.class, EquipmentSlot.class);
    }

    @Override
    public CreativeCategory getCreativeCategory(Material material) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "getCreativeCategory", Material.class);
    }

    @Override
    public String getBlockTranslationKey(Material material) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "getBlockTranslationKey", Material.class);
    }

    @Override
    public String getItemTranslationKey(Material material) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "getItemTranslationKey", Material.class);
    }

    @Override
    public String getTimingsServerName() {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "getTimingsServerName");
    }

    @Override
    public byte[] serializeItem(ItemStack arg0) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "serializeItem", ItemStack.class);
    }

    @Override
    public ItemStack deserializeItem(byte[] arg0) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "deserializeItem", byte[].class);
    }

    @Override
    public @NotNull JsonObject serializeItemAsJson(@NotNull ItemStack itemStack) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "serializeItemAsJson", ItemStack.class);
    }

    @Override
    public @NotNull ItemStack deserializeItemFromJson(@NotNull JsonObject jsonObject) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "deserializeItemFromJson", JsonObject.class);
    }

    @Override
    public byte[] serializeEntity(Entity entity) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "serializeEntity", Entity.class);
    }

    @Override
    public Entity deserializeEntity(byte[] bytes, World world, boolean b) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "deserializeEntity", byte.class, World.class, boolean.class);
    }

    @Override
    public String getTranslationKey(ItemStack arg0) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "getTranslationKey", ItemStack.class);
    }

    @Override
    public String getTranslationKey(Attribute attribute) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "getTranslationKey", Attribute.class);
    }

    @Override
    public PotionType.InternalPotionData getInternalPotionData(NamespacedKey namespacedKey) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "getInternalPotionData", NamespacedKey.class);
    }

    @Override
    public @Nullable DamageEffect getDamageEffect(@NotNull String s) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "getDamageEffect", String.class);
    }

    @Override
    public DamageSource.Builder createDamageSourceBuilder(@NotNull DamageType damageType) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "createDamageSourceBuilder", DamageType.class);
    }

    @Override
    public String get(Class<?> aClass, String s) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "get", Class.class, String.class);
    }

    @Override
    public <B extends Keyed> B get(Registry<B> registry, NamespacedKey namespacedKey) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "get", Registry.class, NamespacedKey.class);
    }

    @Override
    public String getTranslationKey(EntityType arg0) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "getTranslationKey", EntityType.class);
    }

    @Override
    public int nextEntityId() {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "nextEntityId");
    }

    @Override
    public @NotNull String getMainLevelName() {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "getMainLevelName");
    }

    @Override
    public boolean isValidRepairItemStack(@NotNull ItemStack arg0, @NotNull ItemStack arg1) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "isValidRepairItemStack", ItemStack.class, ItemStack.class);
    }

    @Override
    public int getProtocolVersion() {
        return Sponge.platform().minecraftVersion().protocolVersion();
    }

    @Override
    public boolean hasDefaultEntityAttributes(@NotNull NamespacedKey namespacedKey) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "hasDefaultEntityAttributes", NamespacedKey.class);
    }

    @Override
    public @NotNull Attributable getDefaultEntityAttributes(@NotNull NamespacedKey namespacedKey) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "getDefaultEntityAttributes", NamespacedKey.class);
    }

    @Override
    public @NotNull NamespacedKey getBiomeKey(RegionAccessor regionAccessor, int i, int i1, int i2) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "setBiomeKey", RegionAccessor.class, int.class, int.class, int.class);
    }

    @Override
    public void setBiomeKey(RegionAccessor regionAccessor, int i, int i1, int i2, NamespacedKey namespacedKey) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "setBiomeKey", RegionAccessor.class, int.class, int.class, int.class, NamespacedKey.class);
    }

    @Override
    public String getStatisticCriteriaKey(@NotNull Statistic statistic) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "getStatisticCriteriaKey", Statistic.class);
    }

    @Override
    public @Nullable Color getSpawnEggLayerColor(EntityType entityType, int i) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "getSpawnEggLayerColor", EntityType.class, int.class);
    }

    @Override
    public LifecycleEventManager<Plugin> createPluginLifecycleEventManager(JavaPlugin javaPlugin, BooleanSupplier booleanSupplier) {
        boolean test = booleanSupplier.getAsBoolean();
        //TODO work this one out, not on javadocs
        return new SoakLifecycleEventManager<>();
    }

    @Override
    public @NotNull List<Component> computeTooltipLines(@NotNull ItemStack itemStack, @NotNull TooltipContext tooltipContext, @Nullable Player player) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "computeTooltipLines", ItemStack.class, TooltipContext.class, Player.class);
    }

    @Override
    public @Nullable <A extends Keyed, M> Tag<A> getTag(@NotNull TagKey<A> tagKey) {
        throw NotImplementedException.createByLazy(UnsafeValues.class, "getTag", TagKey.class);
    }

    @Override
    public ItemStack createEmptyStack() {
        return new SoakItemStack();
    }

    @Override
    public boolean isSupportedApiVersion(String arg0) {
        //example: 1.19
        DefaultArtifactVersion thisVersion = new DefaultArtifactVersion(arg0);
        DefaultArtifactVersion minVersion = new DefaultArtifactVersion("1.13");
        return thisVersion.compareTo(minVersion) >= 0;
    }

    @Override
    public PlainComponentSerializer plainComponentSerializer() {
        return PlainComponentSerializer.plain();
    }

    @Override
    public PlainTextComponentSerializer plainTextSerializer() {
        return PlainTextComponentSerializer.plainText();
    }

}