package com.fouristhenumber.utilitiesinexcess.config.items;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config.Comment("Inversion Ritual Configuration")
@Config.LangKey("utilitiesinexcess.config.item.inversion")
@Config(modid = UtilitiesInExcess.MODID, category = "items.inversion")
public class InversionConfig {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableInversionSigil;

    @Config.Comment("Set to 0 for unlimited uses")
    @Config.DefaultInt(256)
    @Config.RangeInt(min = 0)
    @Config.RequiresMcRestart
    public static int awakenedInversionDurability;

    @Config.Comment("Amount of unique items in the list the north chest has to contain for the pseudo-inversion ritual")
    @Config.DefaultInt(12)
    @Config.RangeInt(min = 1, max = 10000)
    public static int northChestRequiredItems;

    @Config.Comment("List of valid items the north chest can contain. Must be item ids in the form 'modid:itemname:meta'")
    @Config.DefaultStringList(
        value = { "minecraft:stone", "minecraft:brick", "minecraft:glass", "minecraft:cooked_fished",
            "minecraft:hardened_clay", "minecraft:dye:2", "minecraft:coal:1", "minecraft:cooked_beef",
            "minecraft:iron_ingot", "minecraft:cooked_chicken", "minecraft:gold_ingot", "minecraft:baked_potato",
            "minecraft:cooked_porkchop", "minecraft:netherbrick" })
    public static String[] northChestValidItems;

    @Config.Comment("Amount of unique items in the list the east chest has to contain for the pseudo-inversion ritual")
    @Config.DefaultInt(12)
    @Config.RangeInt(min = 1, max = 10000)
    public static int eastChestRequiredItems;

    @Config.Comment("List of valid items the east chest can contain. Must be item ids in the form 'modid:itemname:meta'. Enable splash potions with chestSplashPotionsValid (applies for other chests too).")
    @Config.DefaultStringList(
        value = { "minecraft:potion:8193", "minecraft:potion:8194", "minecraft:potion:8195", "minecraft:potion:8196",
            "minecraft:potion:8197", "minecraft:potion:8198", "minecraft:potion:8200", "minecraft:potion:8201",
            "minecraft:potion:8202", "minecraft:potion:8204", "minecraft:potion:8205", "minecraft:potion:8206",
            "minecraft:potion:8225", "minecraft:potion:8226", "minecraft:potion:8228", "minecraft:potion:8229",
            "minecraft:potion:8232", "minecraft:potion:8233", "minecraft:potion:8234", "minecraft:potion:8236",
            "minecraft:potion:8257", "minecraft:potion:8258", "minecraft:potion:8259", "minecraft:potion:8260",
            "minecraft:potion:8262", "minecraft:potion:8264", "minecraft:potion:8265", "minecraft:potion:8267",
            "minecraft:potion:8268", "minecraft:potion:8269", "minecraft:potion:8270" })
    public static String[] eastChestValidItems;

    @Config.Comment("Amount of unique items in the list the south chest has to contain for the pseudo-inversion ritual")
    @Config.DefaultInt(12)
    @Config.RangeInt(min = 1, max = 10000)
    public static int southChestRequiredItems;

    @Config.Comment("List of valid items the south chest can contain. Must be item ids in the form 'modid:itemname:meta'")
    @Config.DefaultStringList(
        value = { "minecraft:grass", "minecraft:lapis_ore", "minecraft:dirt", "minecraft:obsidian", "minecraft:sand",
            "minecraft:diamond_ore", "minecraft:gravel", "minecraft:redstone_ore", "minecraft:gold_ore",
            "minecraft:clay", "minecraft:iron_ore", "minecraft:emerald_ore", "minecraft:coal_ore" })
    public static String[] southChestValidItems;

    @Config.Comment("Amount of unique items in the list the west chest has to contain for the pseudo-inversion ritual")
    @Config.DefaultInt(12)
    @Config.RangeInt(min = 1, max = 10000)
    public static int westChestRequiredItems;

    @Config.Comment("List of valid items the west chest can contain. Must be item ids in the form 'modid:itemname:meta'")
    @Config.DefaultStringList(
        value = { "minecraft:record_13", "minecraft:record_mellohi", "minecraft:record_cat", "minecraft:record_stal",
            "minecraft:record_blocks", "minecraft:record_strad", "minecraft:record_chirp", "minecraft:record_ward",
            "minecraft:record_far", "minecraft:record_11", "minecraft:record_mall", "minecraft:record_wait" })
    public static String[] westChestValidItems;

    @Config.Comment("Whether or not vanilla splash potions should also be valid if a regular potion of the same type is found.")
    @Config.DefaultBoolean(true)
    public static boolean chestSplashPotionsValid;

    @Config.Comment("Amount of mobs needed to kill to pass the siege of the ritual")
    @Config.DefaultInt(100)
    @Config.RangeInt(min = 4)
    public static int siegeRequiredMobsKill;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableInvertedIngot;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean invertedIngotsImplode;

    @Config.DefaultInt(300)
    @Config.RequiresMcRestart
    public static int invertedIngotImplosionTimer;
}
