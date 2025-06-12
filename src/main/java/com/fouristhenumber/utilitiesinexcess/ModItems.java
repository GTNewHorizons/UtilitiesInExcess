package com.fouristhenumber.utilitiesinexcess;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.common.items.ItemBuilderWand;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemGluttonsAxe;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemHeavenlyRing;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemMobJar;
import com.fouristhenumber.utilitiesinexcess.config.ItemConfig;

import cpw.mods.fml.common.registry.GameRegistry;

// Credit to Et Futurum (Requiem)
public enum ModItems {
    // spotless:off

    // make sure to leave a trailing comma
    HUNGER_AXE(ItemConfig.enableHungerAxe, new ItemGluttonsAxe(), "hungerAxe"),
    HEAVENLY_RING(ItemConfig.enableHeavenlyRing, new ItemHeavenlyRing(), "heavenlyRing"),
    MOB_JAR(ItemConfig.enableMobJar, new ItemMobJar(), "mobJar"),
    BUILDER_WAND(ItemConfig.enableBuilderWand, new ItemBuilderWand(16), "builderWand"),


    ; // leave trailing semicolon
    // spotless:on

    public static final ModItems[] VALUES = values();

    public static void init() {
        for (ModItems item : VALUES) {
            if (item.isEnabled()) {
                GameRegistry.registerItem(item.get(), item.name);
            }
        }
    }

    final private boolean isEnabled;
    final private Item theItem;
    private final String name;

    ModItems(boolean enabled, Item item, String name) {
        isEnabled = enabled;
        theItem = item;
        this.name = name;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public Item get() {
        return theItem;
    }

    public ItemStack newItemStack() {
        return newItemStack(1);
    }

    public ItemStack newItemStack(int count) {
        return newItemStack(count, 0);
    }

    public ItemStack newItemStack(int count, int meta) {
        return new ItemStack(this.get(), count, meta);
    }
}
