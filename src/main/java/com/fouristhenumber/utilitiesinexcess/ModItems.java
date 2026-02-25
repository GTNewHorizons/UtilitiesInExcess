package com.fouristhenumber.utilitiesinexcess;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.common.items.ItemAnalyzer;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemArchitectsWand;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemBedrockiumIngot;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemDisabled;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemEnderLotusSeed;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemFireBattery;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemGlove;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemGoldenBag;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemHeavenlyRing;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemInversionSigilActive;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemInversionSigilInactive;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemInvertedIngot;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemMobJar;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemPseudoInversionSigil;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemWateringCan;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemXRayGlasses;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemAntiParticulateShovel;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemDestructionPickaxe;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemEthericSword;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemGluttonsAxe;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemPrecisionShears;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemReversingHoe;
import com.fouristhenumber.utilitiesinexcess.config.blocks.EnderLotusConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.FireBatteryConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.ItemConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.WateringCanConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.AntiParticulateShovelConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.DestructionPickaxeConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.EthericSwordConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.GluttonsAxeConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.PrecisionShearsConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.ReversingHoeConfig;

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
    INVERSION_SIGIL_INACTIVE(InversionConfig.enableInversionSigil, new ItemInversionSigilInactive(), "inversion_sigil_inactive"),
    INVERSION_SIGIL_ACTIVE(InversionConfig.enableInversionSigil, new ItemInversionSigilActive(), "inversion_sigil_active"),
    PSEUDO_INVERSION_SIGIL(InversionConfig.enableInversionSigil, new ItemPseudoInversionSigil(), "pseudo_inversion_sigil"),
    INVERTED_INGOT(InversionConfig.enableInvertedIngot, new ItemInvertedIngot(), "inverted_ingot"),
    INVERTED_NUGGET(InversionConfig.enableInvertedIngot, new ItemInvertedIngot.InvertedNugget(), "inverted_nugget"),
    ARCHITECTS_WAND(ItemConfig.enableArchitectsWand, new ItemArchitectsWand(ItemConfig.architectsWandBuildLimit).setTextureName("utilitiesinexcess:architects_wand"), "architects_wand"),
    SUPER_ARCHITECTS_WAND(ItemConfig.enableSuperArchitectsWand, new ItemArchitectsWand(ItemConfig.superArchitectsWandBuildLimit).setTextureName("utilitiesinexcess:super_architects_wand"), "super_architects_wand"),
    BEDROCKIUM_INGOT(ItemConfig.enableBedrockium, new ItemBedrockiumIngot().setUnlocalizedName("bedrockium_ingot").setTextureName("utilitiesinexcess:bedrockium_ingot"), "bedrockium_ingot"),
    FIRE_BATTERY(FireBatteryConfig.enableFireBattery, new ItemFireBattery(), "fire_battery"),
    GOLDEN_BAG(ItemConfig.enableGoldenBagOfHolding, new ItemGoldenBag(), "golden_bag"),
    ENDER_LOTUS_SEED(EnderLotusConfig.enableEnderLotus, new ItemEnderLotusSeed(ModBlocks.ENDER_LOTUS.get()), "ender_lotus_seed"),
    XRAY_GLASSES(ItemConfig.enableXRayGlasses, new ItemXRayGlasses(ItemArmor.ArmorMaterial.IRON, 0, 0), "xray_glasses"),
    BLOCK_ANALYZER(ItemConfig.enableBlockAnalyzer, new ItemAnalyzer(), "block_analyzer"),
    GLOVE(ItemConfig.enableGlove, new ItemGlove(), "glove"),
    ; // leave trailing semicolon
    // spotless:on

    public static final ModItems[] VALUES = values();

    public static void init() {
        for (ModItems item : VALUES) {
            if (item.isEnabled()) {
                item.theItem.setCreativeTab(UtilitiesInExcess.uieTab);
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
