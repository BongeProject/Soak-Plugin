package org.soak.wrapper.inventory;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ComplexRecipe;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.soak.map.SoakResourceKeyMap;
import org.soak.map.item.SoakItemStackMap;
import org.spongepowered.api.item.recipe.crafting.SpecialCraftingRecipe;
import org.spongepowered.api.registry.RegistryTypes;

public class SoakComplexRecipe implements ComplexRecipe {

    private final SpecialCraftingRecipe recipe;

    public SoakComplexRecipe(SpecialCraftingRecipe recipe) {
        this.recipe = recipe;
    }

    public SpecialCraftingRecipe spongeRecipe() {
        return this.recipe;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return SoakResourceKeyMap.mapToBukkit(this.recipe.type().key(RegistryTypes.RECIPE_TYPE));
    }

    @Override
    public @NotNull ItemStack getResult() {
        return SoakItemStackMap.toBukkit(this.recipe.exemplaryResult());
    }
}
