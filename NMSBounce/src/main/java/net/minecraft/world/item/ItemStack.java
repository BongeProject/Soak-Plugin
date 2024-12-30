package net.minecraft.world.item;

import org.soak.wrapper.inventory.SoakItemStack;

public class ItemStack {

    private final SoakItemStack stack;

    public ItemStack(SoakItemStack stack) {
        this.stack = stack;
    }

    public SoakItemStack getSoak(){
        return this.stack;
    }
}
