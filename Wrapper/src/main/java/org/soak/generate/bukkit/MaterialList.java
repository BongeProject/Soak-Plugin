package org.soak.generate.bukkit;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodCall;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import org.apache.maven.model.Resource;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.WrapperManager;
import org.soak.exception.NotImplementedException;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.state.StateContainer;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class MaterialList {

    public static Class<? extends Enum<?>> LOADED_CLASS;

    public static DynamicType.Unloaded<? extends Enum<?>> createMaterialList(ClassLoader loader) throws Exception {
        var blockIterator = BlockTypes.registry().stream().iterator();
        var completedItems = new HashSet<org.spongepowered.api.item.ItemType>();
        var materials = new HashSet<String>();
        while (blockIterator.hasNext()) {
            var blockType = blockIterator.next();
            blockType.item().ifPresent(completedItems::add);

            var key = blockType.key(RegistryTypes.BLOCK_TYPE);
            materials.add(toName(key));
        }
        var itemIterator = ItemTypes.registry().stream().iterator();
        while (itemIterator.hasNext()) {
            var itemType = itemIterator.next();
            if (completedItems.contains(itemType)) {
                continue;
            }
            var key = itemType.key(RegistryTypes.ITEM_TYPE);
            materials.add(toName(key));
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
        return classCreator.make();

    }

    private static String toName(ResourceKey key) {
        var prefix = key.namespace().equals(ResourceKey.MINECRAFT_NAMESPACE) ? "" : toEnumName(key.namespace() + "_");
        return prefix + toEnumName(key.value());
    }

    public static <T extends Enum<T>> EnumSet<T> values() {
        if (LOADED_CLASS == null) {
            throw new RuntimeException("MaterialList.LOADED_CLASS must be set");
        }
        return EnumSet.allOf((Class<T>) LOADED_CLASS);
    }

    public static <T extends Enum<T>> T value(org.spongepowered.api.item.ItemType type) {
        EnumSet<T> set = values();
        return set
                .stream()
                .filter(material -> material.name().equals(toName(type.key(RegistryTypes.ITEM_TYPE))))
                .findAny()
                .orElseThrow(() -> new IllegalStateException(type.key(RegistryTypes.ITEM_TYPE).asString() + " is not in material enum"));
    }

    public static <T extends Enum<T>> T value(org.spongepowered.api.block.BlockType type) {
        EnumSet<T> set = values();
        return set
                .stream()
                .filter(material -> material.name().equals(toName(type.key(RegistryTypes.BLOCK_TYPE))))
                .findAny()
                .orElseThrow(() -> new IllegalStateException(type.key(RegistryTypes.BLOCK_TYPE).asString() + " is not in material enum"));
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildAsBlockTypeMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return callMethod(builder, "asBlockType", BlockType.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildAsItemTypeMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return callMethod(builder, "asItemType", ItemType.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildCreateBlockDataMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return callMethod(builder, "createBlockData", BlockData.class, MethodCall::withAllArguments);

    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildCreateBlockDataStringArgumentMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return callMethod(builder, "createBlockData", BlockData.class, MethodCall::withAllArguments, String.class);

    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildCreateBlockDataConsumerArgumentMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return callMethod(builder, "createBlockData", BlockData.class, MethodCall::withAllArguments, Consumer.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildGetBlastResistanceMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return callMethod(builder, "getBlastResisance", float.class, MethodCall::withAllArguments);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildGetTranslationKeyMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return callMethod(builder, "getTranslationKey", String.class);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> buildGetCompostChanceMethod(DynamicType.Builder<? extends Enum<?>> builder) throws NoSuchMethodException {
        return callMethod(builder, "getCompostChance", float.class);
    }


    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> callMethod(DynamicType.Builder<? extends Enum<?>> builder, String method, Class<?> returnType, Class<?>... arguments) throws NoSuchMethodException {
        return callMethod(builder, method, returnType, extra -> extra, arguments);
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> callMethod(DynamicType.Builder<? extends Enum<?>> builder, String method, Class<?> returnType, Function<MethodCall, MethodCall> extra, Class<?>... parameters) throws NoSuchMethodException {
        List<Class<?>> arguments = new ArrayList<>();
        arguments.add(Enum.class);
        arguments.addAll(Arrays.asList(parameters));
        var call = extra.apply(MethodCall
                .invoke(MaterialList.class
                        .getMethod(method, arguments.toArray(Class[]::new)))
                .withThis());

        return builder.defineMethod(method, returnType)
                .withParameters(arguments)
                .intercept(call);
    }

    public static Optional<org.spongepowered.api.block.BlockType> getBlockType(Enum<?> enumEntry) {
        var name = enumEntry.name();

        return BlockTypes.registry().stream().filter(type -> {
            var key = type.key(RegistryTypes.BLOCK_TYPE);
            var suffix = toEnumName(key.value());
            if (suffix.equals(name)) {
                //minecraft namespaces
                return true;
            }
            var compare = toEnumName(key.namespace()) + "_" + suffix;
            return compare.equals(name);
        }).findAny();
    }

    public static Optional<org.spongepowered.api.item.ItemType> getItemType(Enum<?> enumEntry) {
        var name = enumEntry.name();

        return ItemTypes.registry().stream().filter(type -> {
            var key = type.key(RegistryTypes.ITEM_TYPE);
            var suffix = toEnumName(key.value());
            if (suffix.equals(name)) {
                //minecraft namespaces
                return true;
            }
            var compare = toEnumName(key.namespace()) + "_" + suffix;
            return compare.equals(name);
        }).findAny();
    }

    public static BlockType asBlockType(Enum<?> enumEntry) {
        throw NotImplementedException.createByLazy(enumEntry, "asBlockType");
    }

    public static ItemType asItemType(Enum<?> enumEntry) {
        throw NotImplementedException.createByLazy(enumEntry, "asBlockType");
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

    public static float getCompostChance(Enum<?> enumEntry) {
        throw NotImplementedException.createByLazy(enumEntry, "getCompostChance");
    }

    private @Nullable String getTranslationKey(ComponentLike item) {
        Component component = item.asComponent();
        if (component instanceof TranslatableComponent) {
            return ((TranslatableComponent) component).key();
        }
        return null;
    }

    public @NotNull String getTranslationKey(Enum<?> enumEntry) {
        return getItemType(enumEntry)
                .map(item -> getTranslationKey(item.asComponent()))
                .or(() -> getBlockType(enumEntry)
                        .map(block -> getTranslationKey(block.asComponent())))
                .orElseThrow(() -> new IllegalStateException("The material of " + enumEntry.name() + " does not have a translation key. Bukkit does not expect this."));
    }

    private static String toEnumName(String name) {
        return name.toUpperCase().replace(" ", "_");
    }
}
