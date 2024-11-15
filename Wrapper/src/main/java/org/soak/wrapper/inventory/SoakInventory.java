package org.soak.wrapper.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mose.collection.stream.builder.CollectionStreamBuilder;
import org.soak.WrapperManager;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakLocationMap;
import org.soak.map.item.SoakItemStackMap;
import org.soak.map.item.inventory.SoakInventoryMap;
import org.soak.plugin.SoakManager;
import org.soak.utils.ListMappingUtils;
import org.soak.utils.ReflectionHelper;
import org.soak.wrapper.entity.living.human.SoakPlayer;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryTypes;
import org.spongepowered.api.item.inventory.type.BlockEntityInventory;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.util.Nameable;
import org.spongepowered.api.world.Locatable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SoakInventory<Inv extends org.spongepowered.api.item.inventory.Inventory> implements Inventory {

    private final Inv spongeInventory;
    private Component requestedTitle;

    public SoakInventory(Inv spongeInventory) {
        this.spongeInventory = spongeInventory;
    }

    public static <I extends org.spongepowered.api.item.inventory.Inventory> SoakInventory<I> wrap(I inventory) {
        return new SoakInventory<>(inventory);
    }

    public Optional<Component> requestedTitle() {
        return Optional.ofNullable(this.requestedTitle);
    }

    public void setRequestedTitle(Component component) {
        this.requestedTitle = component;
    }

    public Inv sponge() {
        return this.spongeInventory;
    }

    @Override
    public @NotNull ItemStack[] getContents() {
        return items().toArray(ItemStack[]::new);
    }

    @Override
    public void setContents(ItemStack[] arg0) {
        throw NotImplementedException.createByLazy(Inventory.class, "setContents", ItemStack[].class);
    }

    //why ..... why is it a hashmap rather than a normal interface map .... i cant mock that
    @Override
    public @NotNull HashMap<Integer, ? extends ItemStack> all(@NotNull Material material) throws IllegalArgumentException {
        var map = slotsMatching(spongeSlot -> SoakItemStackMap.toSponge(material).map(itemType -> itemType.equals(spongeSlot.peek().type())).orElse(false));
        return new HashMap<>(map);
    }

    //why ..... why is it a hashmap rather than a normal interface map .... i cant mock that
    @Override
    public @NotNull HashMap<Integer, ? extends ItemStack> all(@Nullable ItemStack item) {
        var map = slotsMatching(spongeSlot -> item == null || SoakItemStackMap.toSponge(item).equals(spongeSlot.peek()));
        return new HashMap<>(map);
    }

    private Map<Integer, ? extends ItemStack> slotsMatching(Predicate<Slot> slotMatch) {
        return this.sponge().slots().stream().filter(slotMatch).collect(Collectors.toMap(slot -> slot.getInt(Keys.SLOT_INDEX).orElseThrow(() -> new IllegalStateException("Cannot get slot index for inventory: " + this.sponge().toString())), slot -> SoakItemStackMap.toBukkit(slot.peek())));
    }

    private org.spongepowered.api.item.inventory.Inventory asJustStack(ItemStack stack) {
        var item = SoakItemStackMap.toSponge(stack);
        return this.spongeInventory.query(QueryTypes.ITEM_STACK_EXACT.get().of(item));
    }

    private org.spongepowered.api.item.inventory.Inventory asJustType(Material material) {
        var itemType = SoakItemStackMap.toSponge(material)
                .orElseThrow(() -> new RuntimeException("Material " + material.name() + " has no itemtype"));
        return this.spongeInventory.query(QueryTypes.ITEM_TYPE.get().of(itemType));
    }

    @Override
    public ItemStack getItem(int arg0) {
        var spongeItem = this.spongeInventory.peekAt(arg0)
                .orElse(org.spongepowered.api.item.inventory.ItemStack.empty());
        return SoakItemStackMap.toBukkit(spongeItem);
    }

    @Override
    public void remove(@NotNull ItemStack arg0) {
        asJustStack(arg0).clear();
    }

    @Override
    public void remove(@NotNull Material arg0) {
        asJustType(arg0).clear();
    }

    @Override
    public void clear(int arg0) {
        sponge().slot(arg0).ifPresent(slot -> slot.offer(org.spongepowered.api.item.inventory.ItemStack.empty()));
    }

    @Override
    public void clear() {
        this.spongeInventory.clear();
    }

    @Override
    public boolean isEmpty() {
        return this.spongeInventory.capacity() == this.spongeInventory.freeCapacity();
    }

    private Stream<ItemStack> items() {
        return this.spongeInventory.slots()
                .stream()
                .map(org.spongepowered.api.item.inventory.Inventory::peek)
                .filter(item -> item.equals(org.spongepowered.api.item.inventory.ItemStack.empty()))
                .map(SoakItemStackMap::toBukkit);
    }

    @Override
    public @NotNull ListIterator<ItemStack> iterator() {
        return items().collect(Collectors.toList()).listIterator();
    }

    @Override
    public @NotNull ListIterator<ItemStack> iterator(int index) {
        return items().collect(Collectors.toList()).listIterator(index);
    }

    @Override
    public boolean contains(ItemStack arg0, int arg1) {
        throw NotImplementedException.createByLazy(Inventory.class, "contains", ItemStack.class, int.class);
    }

    @Override
    public boolean contains(@NotNull Material arg0, int arg1) {
        throw NotImplementedException.createByLazy(Inventory.class, "contains", Material.class, int.class);
    }

    @Override
    public boolean contains(ItemStack arg0) {
        return asJustStack(arg0).capacity() != 0;
    }

    @Override
    public boolean contains(@NotNull Material arg0) {
        return asJustType(arg0).capacity() != 0;
    }

    @Override
    public int first(@NotNull ItemStack arg0) {
        var slots = asJustStack(arg0).slots();
        if (slots.isEmpty()) {
            return -1;
        }
        return slots.get(0).get(Keys.SLOT_INDEX).orElse(-1);
    }

    @Override
    public int first(@NotNull Material arg0) {
        var slots = asJustType(arg0).slots();
        if (slots.isEmpty()) {
            return -1;
        }
        return slots.get(0).get(Keys.SLOT_INDEX).orElse(-1);
    }

    @Override
    public Location getLocation() {
        var spongeInv = sponge();
        if (spongeInv instanceof CarriedInventory<?> carrier) {
            var opCarrier = carrier.carrier();
            if (opCarrier.isPresent() && opCarrier.get() instanceof Locatable locatable) {
                return SoakLocationMap.toBukkit(locatable.serverLocation());
            }
        }
        throw NotImplementedException.createByLazy(Inventory.class, "getLocation");
    }

    @Override
    public int close() {
        return playerOwner().map(player -> {
            var didClose = player.closeInventory();
            return didClose ? 1 : 0;
        }).orElse(0);
    }

    private Optional<ServerPlayer> playerOwner() {
        var sponge = sponge();
        if (sponge instanceof Container container) {
            return Optional.of(container.viewer());
        }
        if (sponge instanceof ViewableInventory customInventory && customInventory.hasViewers() && customInventory.viewers().size() == 1) {
            return Optional.of(customInventory.viewers().iterator().next());
        }
        if (!(sponge instanceof PlayerInventory playerInventory)) {
            return Optional.empty();
        }
        var opPlayer = playerInventory.carrier();
        if (opPlayer.isEmpty()) {
            return Optional.empty();
        }
        var serverPlayer = (ServerPlayer) opPlayer.get();
        return Optional.of(serverPlayer);
    }

    @Override
    public @NotNull InventoryType getType() {
        if (!(spongeInventory instanceof Container)) {
            throw new RuntimeException("Sponge inventory is not a Container, unknown inventory type");
        }
        return SoakInventoryMap.toBukkit((Container) this.spongeInventory);
    }

    @Override
    public int getSize() {
        return this.spongeInventory.capacity();
    }

    @Override
    public int getMaxStackSize() {
        throw NotImplementedException.createByLazy(Inventory.class, "getMaxStackSize");
    }

    @Override
    public void setMaxStackSize(int arg0) {
        throw NotImplementedException.createByLazy(Inventory.class, "setMaxStackSize", int.class);
    }

    @Override
    public void setItem(int arg0, ItemStack arg1) {
        var item = SoakItemStackMap.toSponge(arg1);
        this.spongeInventory.set(arg0, item);
    }

    @Override
    public @NotNull HashMap<Integer, ItemStack> addItem(@NotNull ItemStack... items) throws IllegalArgumentException {
        HashMap<Integer, ItemStack> map = new HashMap<>();
        for (var bukkitItem : items) {
            boolean wasPlaced = false;
            var item = SoakItemStackMap.toSponge(bukkitItem);
            for (var slot : this.spongeInventory.slots()) {
                if (!slot.canFit(item)) {
                    continue;
                }
                var slotIndex = slot.get(Keys.SLOT_INDEX)
                        .orElseThrow(() -> new RuntimeException("No slot index for " + this.spongeInventory.getClass()
                                .getName()));
                map.put(slotIndex, bukkitItem);
                wasPlaced = true;
                break;
            }
            if (!wasPlaced) {
                throw new IllegalArgumentException("Cannot place item " + item.type()
                        .key(RegistryTypes.ITEM_TYPE)
                        .formatted());
            }
        }
        if (map.size() != items.length) {
            //shouldn't be hit, but just in case
            throw new IllegalArgumentException("Cannot place all items");
        }
        map.forEach((index, bukkitItem) -> {
            var item = SoakItemStackMap.toSponge(bukkitItem);
            this.spongeInventory.offer(index, item);
        });
        return map;
    }

    @Override
    public @NotNull HashMap<Integer, ItemStack> removeItem(@NotNull ItemStack... items) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(Inventory.class, "removeItem", ItemStack[].class);
    }

    @Override
    public @NotNull HashMap<Integer, ItemStack> removeItemAnySlot(@NotNull ItemStack... items) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(Inventory.class, "removeItemAnySlot", ItemStack[].class);
    }

    @Override
    public ItemStack[] getStorageContents() {
        throw NotImplementedException.createByLazy(Inventory.class, "getStorageContents");
    }

    @Override
    public void setStorageContents(ItemStack[] arg0) {
        throw NotImplementedException.createByLazy(Inventory.class, "setStorageContents", ItemStack[].class);
    }

    @Override
    public boolean containsAtLeast(ItemStack arg0, int arg1) {
        throw NotImplementedException.createByLazy(Inventory.class, "containsAtLeast", ItemStack.class, int.class);
    }

    @Override
    public int firstEmpty() {
        var sponge = sponge();
        return IntStream
                .range(0, sponge.capacity())
                .boxed()
                .filter(index -> sponge
                        .slot(index)
                        .map(slot -> slot.peek().equals(org.spongepowered.api.item.inventory.ItemStack.empty()))
                        .orElse(false))
                .findFirst()
                .orElse(-1);
    }

    @Override
    public @NotNull List<HumanEntity> getViewers() {
        var sponge = sponge();
        var isMenu = sponge instanceof org.spongepowered.api.item.inventory.menu.InventoryMenu;
        var isViewableInventory = sponge instanceof ViewableInventory;
        var isCarried = sponge instanceof CarriedInventory;
        var isBlockEntity = sponge instanceof BlockEntityInventory;
        var isContainer = sponge instanceof Container;
        var opViewable = sponge.asViewable();
        if (opViewable.isPresent()) {
            var viewers = opViewable.get().viewers();
            if (viewers.isEmpty()) {
                //above fails for some reason -> here is a hacky way to override above
                try {
                    viewers = ReflectionHelper.getField(opViewable.get(), "viewers");
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            if (!viewers.isEmpty()) {
                return ListMappingUtils.fromStream(CollectionStreamBuilder
                                        .builder()
                                        .collection(viewers, human -> ((SoakPlayer) human).spongeEntity())
                                        .basicMap(serverPlayer -> (HumanEntity) SoakManager.<WrapperManager>getManager().getMemoryStore().get(serverPlayer)),
                                viewers::stream,
                                (spongePlayer, bukkitPlayer) -> spongePlayer.uniqueId().equals(bukkitPlayer.getUniqueId()), Comparator.comparing(Nameable::name))
                        .buildList();
            }

        }

        return playerOwner().stream().map(player -> (HumanEntity) SoakManager.<WrapperManager>getManager().getMemoryStore().get(player)).toList();
    }

    @Override
    public InventoryHolder getHolder() {
        throw NotImplementedException.createByLazy(Inventory.class, "getHolder");
    }

    @Override
    public InventoryHolder getHolder(boolean arg0) {
        throw NotImplementedException.createByLazy(Inventory.class, "getHolder", boolean.class);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SoakInventory<?> soakInventory)) {
            return false;
        }
        //title
        if (!Objects.equals(soakInventory.requestedTitle, this.requestedTitle)) {
            return false;
        }
        //viewers
        var thisViewers = this.getViewers();
        var soakViewers = soakInventory.getViewers();
        if (!new HashSet<>(soakViewers).containsAll(thisViewers)) {
            return false;
        }
        //items
        return this.sponge().slots().stream().map(slot -> {
            Optional<Slot> opSlot = slot.get(Keys.SLOT_INDEX).flatMap(index -> soakInventory.sponge().slot(index));
            return Map.entry(slot, opSlot);
        }).allMatch(entry -> {
            var slot = entry.getKey();
            var opCompare = entry.getValue();
            return opCompare.filter(value -> slot.peek().equalTo(value.peek())).isPresent();
        });
    }

    @Override
    public int hashCode() {
        int title = this.requestedTitle == null ? 0 : this.requestedTitle.hashCode();
        int content = Integer.parseInt(this.sponge().slots().stream().map(slot -> slot.peek().hashCode() + "").collect(Collectors.joining("")));
        return title + content;
    }
}