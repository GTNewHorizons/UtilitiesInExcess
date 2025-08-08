package com.fouristhenumber.utilitiesinexcess;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.common.items.ItemGluttonsAxe;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemHeavenlyRing;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemMobJar;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemWateringCan;
import com.fouristhenumber.utilitiesinexcess.config.ItemConfig;

import cpw.mods.fml.common.registry.GameRegistry;

// Credit to Et Futurum (Requiem)
public enum ModItems {
    // spotless:off

    // make sure to leave a trailing comma
    HUNGER_AXE(ItemConfig.enableHungerAxe, new ItemGluttonsAxe(), "hunger_axe"),
    HEAVENLY_RING(ItemConfig.enableHeavenlyRing, new ItemHeavenlyRing(), "heavenly_ring"),
    MOB_JAR(ItemConfig.enableMobJar, new ItemMobJar(), "mob_jar"),
    WATERING_CAN_BASIC(ItemConfig.wateringCan.Tier.enableWateringCanBasic, new ItemWateringCan(1,3), "watering_can_basic"),
    WATERING_CAN_ADVANCED(ItemConfig.wateringCan.Tier.enableWateringCanAdvanced, new ItemWateringCan(2,5), "watering_can_advanced"),
    WATERING_CAN_ELITE(ItemConfig.wateringCan.Tier.enableWateringCanElite, new ItemWateringCan(3,7), "watering_can_elite"),
    INVERSION_SIGIL_INACTIVE(ItemConfig.enableInversionSigil, new Item().setUnlocalizedName("inversion_sigil_inactive").setTextureName("utilitiesinexcess:inversion_sigil_inactive"), "inversion_sigil_inactive"),
    INVERSION_SIGIL_ACTIVE(ItemConfig.enableInversionSigil, new Item().setUnlocalizedName("inversion_sigil_active").setTextureName("utilitiesinexcess:inversion_sigil_active"), "inversion_sigil_active"),
    INVERTED_INGOT(ItemConfig.enableInvertedIngot, new Item().setUnlocalizedName("inverted_ingot").setTextureName("utilitiesinexcess:inverted_ingot"), "inverted_ingot"),
    DIAMOND_STICK(ItemConfig.enableDiamondStick, new Item().setUnlocalizedName("diamond_stick").setTextureName("utilitiesinexcess:diamond_stick"), "diamond_stick");

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
