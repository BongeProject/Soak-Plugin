package org.soak.generate.bukkit;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.jar.asm.Opcodes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Translatable;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.WrapperManager;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakResourceKeyMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.inventory.SoakItemTypeTyped;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.state.StateContainer;
import org.spongepowered.api.tag.ItemTypeTags;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class MaterialList {

    public static final Map<String, org.spongepowered.api.block.BlockType> BLOCK_TYPE_MAP = new HashMap<>();
    public static final Map<String, org.spongepowered.api.item.ItemType> ITEM_TYPE_MAP = new HashMap<>();
    public static Class<? extends Enum<?>> LOADED_CLASS;

    public static DynamicType.Unloaded<? extends Enum<?>> createMaterialList() throws Exception {
        var shouldUseModded = SoakManager.<WrapperManager>getManager().shouldMaterialListUseModded();
        var blockIterator = BlockTypes.registry().stream().iterator();
        var completedItems = new HashSet<org.spongepowered.api.item.ItemType>();
        var materials = new HashSet<String>();
        while (blockIterator.hasNext()) {
            var blockType = blockIterator.next();
            var key = blockType.key(RegistryTypes.BLOCK_TYPE);
            if (!shouldUseModded && !key.namespace().equals(ResourceKey.MINECRAFT_NAMESPACE)) {
                continue;
            }
            var name = CommonGenerationCode.toName(key);
            materials.add(name);

            BLOCK_TYPE_MAP.put(name, blockType);

            blockType.item().ifPresent(itemType -> {
                if (BlockTypes.registry().findValue(blockType.key(RegistryTypes.BLOCK_TYPE)).isPresent()) {
                    return;
                }
                completedItems.add(itemType);
                ITEM_TYPE_MAP.put(name, itemType);
            });
        }
        var itemIterator = ItemTypes.registry().stream().iterator();
        while (itemIterator.hasNext()) {
            var itemType = itemIterator.next();
            if (completedItems.contains(itemType)) {
                continue;
            }
            var key = itemType.key(RegistryTypes.ITEM_TYPE);
            if (!shouldUseModded && !key.namespace().equals(ResourceKey.MINECRAFT_NAMESPACE)) {
                continue;
            }
            var name = CommonGenerationCode.toName(key);
            materials.add(name);
            ITEM_TYPE_MAP.put(name, itemType);
        }

        var classCreator = new ByteBuddy()
                .makeEnumeration(materials)
                .name("org.bukkit.Material");

        classCreator = buildAsBlockTypeMethod(classCreator);
        classCreator = buildAsItemTypeMethod(classCreator);
        classCreator = buildCreateBlockDataMethod(classCreator);
        classCreator = buildCreateBlockDataStringArgumentMethod(classCreator);
        classCreator = buildCreateBlockDataConsumerArgumentMethod(classCreator);
        classCreator = buildGetBlastResistanceMethod(classCreator);
        classCreator = buildGetTranslationKeyMethod(classCreator);
        classCreator = buildGetCompostChanceMethod(classCreator);
        classCreator = buildIsLegacy(classCreator);
        classCreator = buildIsAir(classCreator);
        classCreator = buildIsBlock(classCreator);
        classCreator = buildIsItem(classCreator);
        classCreator = buildGetMaxDurabilityMethod(classCreator);
        classCreator = buildGetKeyMethod(classCreator);

        classCreator = buildStaticMatchMaterial(classCreator);
        return classCreator.implement(Keyed.class, Translatable.class, net.kyori.adventure.translation.Translatable.class).make();

    }

    public static <T extends Enum<T>> EnumSet<T> values() {
        if (LOADED_CLASS == null) {
            throw new RuntimeException("MaterialList.LOADED_CLASS must be set");
        }
        return EnumSet.allOf((Class<T>) LOADED_CLASS);
    }

    public static <T extends Enum<T>> T value(org.spongepowered.api.item.ItemType type) {
        var enumName = ITEM_TYPE_MAP
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(type))
                .findAny()
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException(type.key(RegistryTypes.ITEM_TYPE).asString() + " is not in material enum"));
        EnumSet<T> values = values();
        return values
                .stream()
                .filter(enumValue -> enumValue.name().equals(enumName))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Found Material name of '" + enumName + "' but couldnt find the enum"));
    }

    public static <T extends Enum<T>> T value(org.spongepowered.api.block.BlockType type) {
        var enumName = BLOCK_TYPE_MAP
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(type))
                .findAny()
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException(type.key(RegistryTypes.BLOCK_TYPE).asString() + " is not in material enum"));
        EnumSet<T> values = values();
        return values
                .stream()
                .filter(enumValue -> enumValue.name().equals(enumName))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Found Material name of '" + enumName + "' but couldnt find the enum"));
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildAsBlockTypeMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(MaterialList.class, builder, "asBlockType", BlockType.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildAsItemTypeMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(MaterialList.class, builder, "asItemType", ItemType.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildCreateBlockDataMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(MaterialList.class, builder, "createBlockData", BlockData.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildCreateBlockDataStringArgumentMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(MaterialList.class, builder, "createBlockData", BlockData.class, call -> call.withArgument(0), String.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildCreateBlockDataConsumerArgumentMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(MaterialList.class, builder, "createBlockData", BlockData.class, call -> call.withArgument(0), Consumer.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildGetBlastResistanceMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(MaterialList.class, builder, "getBlastResistance", float.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildGetTranslationKeyMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(MaterialList.class, builder, "getTranslationKey", String.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildGetCompostChanceMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(MaterialList.class, builder, "getCompostChance", float.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildGetMaxDurabilityMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(MaterialList.class, builder, "getMaxDurability", short.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildGetKeyMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(MaterialList.class, builder, "getKey", NamespacedKey.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildIsLegacy(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(MaterialList.class, builder, "isLegacy", boolean.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildIsAir(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(MaterialList.class, builder, "isAir", boolean.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildIsBlock(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(MaterialList.class, builder, "isBlock", boolean.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildIsItem(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(MaterialList.class, builder, "isItem", boolean.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildStaticMatchMaterial(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return CommonGenerationCode.callStaticMethodReturnSelf(MaterialList.class, builder, "matchMaterial", MethodCall::withAllArguments, String.class);
    }

    public static Optional<org.spongepowered.api.block.BlockType> getBlockType(Enum<?> enumEntry) {
        return Optional.ofNullable(BLOCK_TYPE_MAP.get(enumEntry.name()));
    }

    public static Optional<org.spongepowered.api.item.ItemType> getItemType(Enum<?> enumEntry) {
        return Optional.ofNullable(ITEM_TYPE_MAP.get(enumEntry.name()));
    }

    public static BlockType asBlockType(Enum<?> enumEntry) {
        throw NotImplementedException.createByLazy(enumEntry, "asBlockType");
    }

    public static ItemType asItemType(Enum<?> enumEntry) {
        var spongeItemType = getItemType(enumEntry).orElseThrow();
        return new SoakItemTypeTyped<>(spongeItemType);
    }

    public static BlockData createBlockData(Enum<?> enumEntry) {
        var spongeBlockState = getBlockType(enumEntry)
                .map(StateContainer::defaultState)
                .orElseThrow(() -> new IllegalStateException("Cannot create BlockData from a item"));
        return SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongeBlockState);
    }

    public static BlockData createBlockData(Enum<?> enumEntry, @Nullable String data) {
        var blockType = getBlockType(enumEntry).orElseThrow(() -> new IllegalStateException("Cannot invoke 'createBlockData' on a none block"));
        BlockState state = BlockState.fromString(blockType.key(RegistryTypes.BLOCK_TYPE).asString() + (data == null ? "" : "[" + data + "]"));
        return SoakManager.<WrapperManager>getManager().getMemoryStore().get(state);
    }

    public static BlockData createBlockData(Enum<?> enumEntry, @Nullable Consumer<BlockData> consumer) {
        var result = createBlockData(enumEntry);
        if (consumer != null) {
            consumer.accept(result);
        }
        return result;
    }

    public static float getBlastResistance(Enum<?> enumEntry) {
        return getBlockType(enumEntry).flatMap(type -> type.get(Keys.BLAST_RESISTANCE)).orElse(0.0).floatValue();
    }

    public static NamespacedKey getKey(Enum<?> enumEntry) {
        return getBlockType(enumEntry)
                .map(type -> type.key(RegistryTypes.BLOCK_TYPE))
                .or(() -> getItemType(enumEntry)
                        .map(type -> type.key(RegistryTypes.ITEM_TYPE)))
                .map(SoakResourceKeyMap::mapToBukkit)
                .orElseThrow(() -> new RuntimeException(enumEntry.name() + " does not have a key"));
    }

    public static boolean isLegacy(Enum<?> enumEntry) {
        return enumEntry.name().startsWith("LEGACY_");
    }

    public static boolean isAir(Enum<?> enumEntry) {
        return getItemType(enumEntry).map(type -> type.equals(ItemTypes.AIR.get())).orElse(false);
    }

    public static boolean isBlock(Enum<?> enumEntry) {
        return getBlockType(enumEntry).isPresent();
    }

    public static boolean isItem(Enum<?> enumEntry) {
        return getItemType(enumEntry).isPresent();
    }

    public static short getMaxDurability(Enum<?> enumEntry) {
        return getItemType(enumEntry).flatMap(item -> item.get(Keys.MAX_DURABILITY)).map(Integer::shortValue).orElse((short) 0);
    }

    public static float getCompostChance(Enum<?> enumEntry) {
        throw NotImplementedException.createByLazy(enumEntry, "getCompostChance");
    }

    private static @Nullable String getTranslationKey(ComponentLike item) {
        Component component = item.asComponent();
        if (component instanceof TranslatableComponent) {
            return ((TranslatableComponent) component).key();
        }
        return null;
    }

    public static @NotNull String getTranslationKey(Enum<?> enumEntry) {
        return getItemType(enumEntry)
                .map(item -> getTranslationKey(item.asComponent()))
                .or(() -> getBlockType(enumEntry)
                        .map(block -> getTranslationKey(block.asComponent())))
                .orElseThrow(() -> new IllegalStateException("The material of " + enumEntry.name() + " does not have a translation key. Bukkit does not expect this."));
    }

    public static @Nullable Enum<?> getMaterial(ResourceKey key) {
        var opBlockType = BlockTypes.registry().findValue(key);
        if (opBlockType.isPresent()) {
            return value(opBlockType.get());
        }

        var opItemType = ItemTypes.registry().findValue(key);
        return opItemType.<Enum<?>>map(MaterialList::value).orElse(null);
    }

    public static Enum<?> matchMaterial(String name) {
        name = name.toLowerCase();
        name = name.replaceAll(" ", "_");
        ResourceKey key = ResourceKey.resolve(name);
        try {
            return getMaterial(key);
        } catch (RuntimeException e) {
            return null;
        }
    }
}
