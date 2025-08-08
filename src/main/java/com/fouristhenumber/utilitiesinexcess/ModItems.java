package com.fouristhenumber.utilitiesinexcess;

import com.fouristhenumber.utilitiesinexcess.common.items.ItemDisabled;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.AntiParticulateShovelConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.common.items.ItemHeavenlyRing;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemMobJar;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemAntiParticulateShovel;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemDestructionPickaxe;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemEthericSword;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemGluttonsAxe;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemPrecisionShears;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemReversingHoe;
import com.fouristhenumber.utilitiesinexcess.config.items.ItemConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.EthericSwordConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.GluttonsAxeConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.UnstableTools;

import cpw.mods.fml.common.registry.GameRegistry;

// Credit to Et Futurum (Requiem)
public enum ModItems {
    // spotless:off

    // make sure to leave a trailing comma
    GLUTTONS_AXE(GluttonsAxeConfig.enable, new ItemGluttonsAxe(), "gluttons_axe"),
    DESTRUCTION_PICKAXE(UnstableTools.enableDestructionPickaxe, new ItemDestructionPickaxe(), "destruction_pickaxe"),
    ANTI_PARTICULATE_SHOVEL(AntiParticulateShovelConfig.enable, new ItemAntiParticulateShovel(), "anti_particulate_shovel"),
    PRECISION_SHEARS(UnstableTools.enablePrecisionShears, new ItemPrecisionShears(), "precision_shears"),
    ETHERIC_SWORD(EthericSwordConfig.enable, new ItemEthericSword(), "etheric_sword"),
    REVERSING_HOE(UnstableTools.enableReversingHoe, new ItemReversingHoe(), "reversing_hoe"),
    HEAVENLY_RING(ItemConfig.enableHeavenlyRing, new ItemHeavenlyRing(), "heavenly_ring"),
    MOB_JAR(ItemConfig.enableMobJar, new ItemMobJar(), "mob_jar"),


    ; // leave trailing semicolon
    // spotless:on

    public static final ModItems[] VALUES = values();

    private static final ItemDisabled disabledItem=new ItemDisabled();

    public static void init() {
        for (ModItems item : VALUES) {
            if (item.isEnabled()) {
                GameRegistry.registerItem(item.get(), item.name);
            }
            else GameRegistry.registerItem(disabledItem,item.name);
        }
    }

    private final boolean isEnabled;
    private final Item theItem;
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
