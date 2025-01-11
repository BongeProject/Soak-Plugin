package org.soak.wrapper.inventory.meta.tool;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakBlockMap;
import org.soak.utils.ListMappingUtils;
import org.soak.wrapper.inventory.meta.AbstractItemMeta;
import org.spongepowered.api.data.Keys;

import java.util.*;

public class SoakToolComponent implements ToolComponent {

    private final @NotNull AbstractItemMeta item;

    public SoakToolComponent(AbstractItemMeta meta) {
        item = meta;
    }

    private Collection<org.spongepowered.api.data.type.ToolRule> toolRules() {
        return item.sponge().get(Keys.TOOL_RULES).orElse(Collections.emptyList());
    }

    @Override
    public float getDefaultMiningSpeed() {
        return item.sponge().get(Keys.EFFICIENCY).orElse(0.0).floatValue();
    }

    @Override
    public void setDefaultMiningSpeed(float v) {
        item.set(Keys.EFFICIENCY, ((Float) v).doubleValue());
    }

    @Override
    public int getDamagePerBlock() {
        return item.sponge().get(Keys.TOOL_DAMAGE_PER_BLOCK).orElse(0);
    }

    @Override
    public void setDamagePerBlock(int i) {
        item.set(Keys.TOOL_DAMAGE_PER_BLOCK, i);
    }

    @Override
    public @NotNull List<ToolRule> getRules() {
        return ListMappingUtils.direct(this
                        .item
                        .sponge()
                        .get(Keys.TOOL_RULES)
                        .orElse(Collections.emptyList()),
                rule -> new SoakToolRule(item, rule),
                rule -> ((SoakToolRule) rule).sponge(),
                false);
    }

    @Override
    public void setRules(@NotNull List<ToolRule> list) {
        var spongeList = list.stream().map(bTool -> ((SoakToolRule) bTool).sponge()).toList();
        this.item.setList(Keys.TOOL_RULES, spongeList);
    }

    @Override
    public @NotNull ToolRule addRule(@NotNull Material material, @Nullable Float aFloat, @Nullable Boolean aBoolean) {
        return addRule(Collections.singletonList(material), aFloat, aBoolean);
    }

    @Override
    public @NotNull ToolRule addRule(@NotNull Collection<Material> collection, @Nullable Float aFloat, @Nullable Boolean aBoolean) {
        var spongeBlocks = collection.stream().map(material -> SoakBlockMap.toSponge(material).orElseThrow()).toList();
        var spongeToolRule = org.spongepowered.api.data.type.ToolRule.forBlocks(spongeBlocks, aFloat == null ? null : aFloat.doubleValue(), aBoolean);
        var toolRules = new ArrayList<>(this.item.sponge().get(Keys.TOOL_RULES).orElse(Collections.emptyList()));
        toolRules.add(spongeToolRule);
        return new SoakToolRule(item, spongeToolRule);
    }

    @Override
    public @NotNull ToolRule addRule(@NotNull Tag<Material> tag, @Nullable Float aFloat, @Nullable Boolean aBoolean) {
        return addRule(tag.getValues(), aFloat, aBoolean);
    }

    @Override
    public boolean removeRule(@NotNull ToolRule toolRule) {
        if (!(toolRule instanceof SoakToolRule)) {
            throw new IllegalArgumentException("ToolRule must be soak version");
        }
        var tools = new ArrayList<>(this.item.sponge().get(Keys.TOOL_RULES).orElse(Collections.emptyList()));
        tools.remove(toolRule);
        this.item.setList(Keys.TOOL_RULES, tools);
        return false;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        throw NotImplementedException.createByLazy(ToolComponent.class, "serialize");
    }
}
