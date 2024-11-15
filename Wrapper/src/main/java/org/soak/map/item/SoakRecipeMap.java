package org.soak.map.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.soak.map.SoakResourceKeyMap;
import org.soak.wrapper.inventory.SoakComplexRecipe;
import org.spongepowered.api.item.recipe.RecipeTypes;
import org.spongepowered.api.item.recipe.cooking.CookingRecipe;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.ShapelessCraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.SpecialCraftingRecipe;
import org.spongepowered.api.item.recipe.single.StoneCutterRecipe;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SoakRecipeMap {

    public static Recipe toBukkit(org.spongepowered.api.item.recipe.Recipe<?> recipe) {
        NamespacedKey key = SoakResourceKeyMap.mapToBukkit(recipe.type().key(RegistryTypes.RECIPE_TYPE));
        var result = SoakItemStackMap.toBukkit(recipe.exemplaryResult());
        List<Ingredient> inputs = recipe.ingredients();

        if (recipe instanceof CookingRecipe cooking) {
            var input = SoakItemStackMap.toBukkit(cooking.ingredient().displayedItems().getFirst().type());
            if (cooking.type().equals(RecipeTypes.SMELTING.get())) {
                return new FurnaceRecipe(key,
                        result,
                        input,
                        cooking.experience(),
                        (int) cooking.cookingTime().ticks());
            }
            if (cooking.type().equals(RecipeTypes.BLASTING.get())) {
                return new BlastingRecipe(key,
                        result,
                        input,
                        cooking.experience(),
                        (int) cooking.cookingTime().ticks());
            }
            if (cooking.type().equals(RecipeTypes.CAMPFIRE_COOKING.get())) {
                return new CampfireRecipe(key,
                        result,
                        input,
                        cooking.experience(),
                        (int) cooking.cookingTime().ticks());
            }
            if (cooking.type().equals(RecipeTypes.SMOKING.get())) {
                return new SmokingRecipe(key,
                        result,
                        input,
                        cooking.experience(),
                        (int) cooking.cookingTime().ticks());
            }
        }
        if (recipe instanceof StoneCutterRecipe stoneCutterRecipe) {
            var input = SoakItemStackMap.toBukkit(stoneCutterRecipe.ingredients().getFirst().displayedItems().getFirst().type());
            return new StonecuttingRecipe(key, result, input);
        }

        if (recipe instanceof SpecialCraftingRecipe) {
            return new SoakComplexRecipe((SpecialCraftingRecipe) recipe);
        }


        if (recipe instanceof ShapedCraftingRecipe shapedCrafting) {
            var shaped = new ShapedRecipe(key, result);
            int i = 0;
            Map<Integer, String> characterMap = new LinkedHashMap<>();
            Map<Character, ItemStack> ingredients = new LinkedHashMap<>();
            for (int x = 0; x < shapedCrafting.width(); x++) {
                for (int z = 0; z < shapedCrafting.height(); z++) {
                    i++;
                    char c = (char) (65 + i);
                    Ingredient ingr = shapedCrafting.ingredient(x, z);
                    var items = ingr.displayedItems();
                    String line = characterMap.getOrDefault(x, "");
                    characterMap.put(x, line + c);
                    if (items.isEmpty()) {
                        continue;
                    }
                    ingredients.put(c, SoakItemStackMap.toBukkit(items.getFirst()));
                }
            }
            shaped.shape(characterMap.values().toArray(new String[0]));
            ingredients.forEach(shaped::setIngredient);
            return shaped;
        }

        if (recipe instanceof ShapelessCraftingRecipe) {
            var shapeless = new ShapelessRecipe(key, result);
            inputs.stream()
                    .map(Ingredient::displayedItems)
                    .filter(in -> !in.isEmpty())
                    .map(List::getFirst)
                    .map(SoakItemStackMap::toBukkit)
                    .forEach(shapeless::addIngredient);
            return shapeless;
        }
        throw new RuntimeException("Unknown mapping for recipetype " + recipe.type()
                .key(RegistryTypes.RECIPE_TYPE)
                .formatted() + ": " + recipe.getClass().getName());
    }
}
