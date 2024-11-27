package org.soak.wrapper.inventory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.map.SoakMessageMap;
import org.soak.map.SoakResourceKeyMap;
import org.soak.utils.GeneralHelper;
import org.soak.wrapper.entity.living.human.SoakPlayer;
import org.soak.wrapper.inventory.view.AbstractInventoryView;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ContainerType;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.api.registry.RegistryTypes;

public class SoakMenuType<View extends InventoryView> implements MenuType.Typed<View> {

    private final ContainerType type;
    private final @Nullable Class<?> typeClass;

    public SoakMenuType(ContainerType type) {
        this(type, null);
    }

    public SoakMenuType(ContainerType type, @Nullable Class<?> typeClass) {
        this.type = type;
        this.typeClass = typeClass;
    }

    @Override
    public @NotNull View create(@NotNull HumanEntity humanEntity, @NotNull String s) {
        return create(humanEntity, SoakMessageMap.toComponent(s));
    }

    @Override
    public @NotNull View create(@NotNull HumanEntity humanEntity, @NotNull Component component) {
        if (!(humanEntity instanceof SoakPlayer player)) {
            throw new IllegalStateException("Player must be a SoakPlayer");
        }
        var container = player.spongeEntity().openInventory(ViewableInventory.builder().type(this.type).completeStructure().plugin(GeneralHelper.fromStackTrace()).build(), component).orElseThrow();
        return (View) AbstractInventoryView.wrap(container);
    }

    @NotNull
    @Override
    public Typed<InventoryView> typed() {
        return new SoakMenuType<>(this.type);
    }

    @NotNull
    @Override
    public <V extends InventoryView> Typed<V> typed(@NotNull Class<V> aClass) throws IllegalArgumentException {
        return new SoakMenuType<>(this.type, aClass);
    }

    @Override
    public @NotNull Class<? extends InventoryView> getInventoryViewClass() {
        if (this.typeClass != null) {
            return (Class<? extends InventoryView>) this.typeClass;
        }
        return InventoryView.class;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return SoakResourceKeyMap.mapToBukkit(this.type.key(RegistryTypes.CONTAINER_TYPE));
    }
}
