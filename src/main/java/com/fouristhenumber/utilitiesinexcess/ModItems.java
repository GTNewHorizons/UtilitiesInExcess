package com.fouristhenumber.utilitiesinexcess;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.common.items.ItemDisabled;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemHeavenlyRing;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemMobJar;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemWateringCan;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemAntiParticulateShovel;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemDestructionPickaxe;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemEthericSword;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemGluttonsAxe;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemPrecisionShears;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemReversingHoe;
import com.fouristhenumber.utilitiesinexcess.config.items.ItemConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.WateringCanConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.*;

import cpw.mods.fml.common.registry.GameRegistry;

// Credit to Et Futurum (Requiem)
public enum ModItems {
    // spotless:off

    // make sure to leave a trailing comma
    GLUTTONS_AXE(GluttonsAxeConfig.enable, new ItemGluttonsAxe(), "gluttons_axe"),
    DESTRUCTION_PICKAXE(DestructionPickaxeConfig.enable, new ItemDestructionPickaxe(), "destruction_pickaxe"),
    ANTI_PARTICULATE_SHOVEL(AntiParticulateShovelConfig.enable, new ItemAntiParticulateShovel(), "anti_particulate_shovel"),
    PRECISION_SHEARS(PrecisionShearsConfig.enable, new ItemPrecisionShears(), "precision_shears"),
    ETHERIC_SWORD(EthericSwordConfig.enable, new ItemEthericSword(), "etheric_sword"),
    REVERSING_HOE(ReversingHoeConfig.enable, new ItemReversingHoe(), "reversing_hoe"),
    HEAVENLY_RING(ItemConfig.enableHeavenlyRing, new ItemHeavenlyRing(), "heavenly_ring"),
    MOB_JAR(ItemConfig.enableMobJar, new ItemMobJar(), "mob_jar"),
    WATERING_CAN_BASIC(WateringCanConfig.wateringCan.Tier.enableWateringCanBasic, new ItemWateringCan(1,3), "watering_can_basic"),
    WATERING_CAN_ADVANCED(WateringCanConfig.wateringCan.Tier.enableWateringCanAdvanced, new ItemWateringCan(2,5), "watering_can_advanced"),
    WATERING_CAN_ELITE(WateringCanConfig.wateringCan.Tier.enableWateringCanElite, new ItemWateringCan(3,7), "watering_can_elite"),


    ; // leave trailing semicolon
    // spotless:on

    public static final ModItems[] VALUES = values();

    public static void init() {
        for (ModItems item : VALUES) {
            if (item.isEnabled()) {
                GameRegistry.registerItem(item.get(), item.name);
            } else if (ItemConfig.registerDisabledItems) GameRegistry.registerItem(item.disabledVersion, item.name);
        }
    }

    private final boolean isEnabled;
    private final Item theItem;
    private final String name;
    private final ItemDisabled disabledVersion;

    ModItems(boolean enabled, Item item, String name) {
        this.isEnabled = enabled;
        theItem = item;
        this.name = name;
        if (ItemConfig.registerDisabledItems) disabledVersion = new ItemDisabled(theItem);
        else disabledVersion = null;
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
