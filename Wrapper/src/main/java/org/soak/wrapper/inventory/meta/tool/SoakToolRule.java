package org.soak.wrapper.inventory.meta.tool;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakBlockMap;
import org.soak.wrapper.inventory.meta.AbstractItemMeta;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.ToolRule;

import java.util.*;
import java.util.stream.Collectors;

public class SoakToolRule implements ToolComponent.ToolRule {

    private final AbstractItemMeta item;
    private ToolRule rule;

    public SoakToolRule(@NotNull AbstractItemMeta item, @NotNull ToolRule rule) {
        this.rule = rule;
        this.item = item;
    }

    public ToolRule sponge() {
        return this.rule;
    }

    @Override
    public @NotNull Collection<Material> getBlocks() {
        return this.rule.blocks().stream().map(SoakBlockMap::toBukkit).collect(Collectors.toSet());
    }

    @Override
    public void setBlocks(@NotNull Material material) {
        setBlocks(Collections.singletonList(material));
    }

    @Override
    public void setBlocks(@NotNull Collection<Material> collection) {
        var blockTypes = collection.stream().map(SoakBlockMap::toSponge).map(Optional::orElseThrow).collect(Collectors.toList());
        var newToolRule = ToolRule.forBlocks(blockTypes, rule.speed().orElse(null), rule.drops().orElse(null));
        var toolRules = new ArrayList<>(this.item.sponge().get(Keys.TOOL_RULES).orElse(Collections.emptyList()));
        toolRules.remove(this.rule);
        toolRules.add(newToolRule);
        rule = newToolRule;
    }

    @Override
    public void setBlocks(@NotNull Tag<Material> tag) {
        setBlocks(tag.getValues());
    }

    private void updateRule(ToolRule rule) {
        var toolRules = new ArrayList<>(this.item.sponge().get(Keys.TOOL_RULES).orElse(Collections.emptyList()));
        toolRules.remove(this.rule);
        toolRules.add(rule);
        this.item.setList(Keys.TOOL_RULES, toolRules);

        this.rule = rule;
    }

    @Override
    public @Nullable Float getSpeed() {
        return this.rule.speed().map(Double::floatValue).orElse(null);
    }

    @Override
    public void setSpeed(@Nullable Float aFloat) {
        var toolRule = ToolRule.forBlocks(new ArrayList<>(rule.blocks()), aFloat == null ? null : aFloat.doubleValue(), rule.drops().orElse(null));
        updateRule(toolRule);
    }

    @Override
    public @Nullable Boolean isCorrectForDrops() {
        return this.rule.drops().orElse(null);
    }

    @Override
    public void setCorrectForDrops(@Nullable Boolean aBoolean) {
        var toolRule = ToolRule.forBlocks(new ArrayList<>(rule.blocks()), rule.speed().orElse(null), aBoolean);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        throw NotImplementedException.createByLazy(ToolComponent.ToolRule.class, "serialize");
    }
}
