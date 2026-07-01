package com.fouristhenumber.utilitiesinexcess.config.items.invertedtools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.inverted_tools.destruction_pickaxe")
public class DestructionPickaxeConfig {

    @Config.Ignore
    public static final DestructionPickaxeConfig INSTANCE = new DestructionPickaxeConfig();

    @Config.DefaultBoolean(true)
    @Config.Name("Enable")
    @Config.RequiresMcRestart
    @Config.Order(0)
    public boolean enable;

    @Config.DefaultBoolean(true)
    @Config.Name("Unbreakable")
    @Config.RequiresMcRestart
    @Config.Order(100)
    public boolean unbreakable;

    @Config.DefaultBoolean(false)
    @Config.Order(200)
    public boolean voidMinedBlock;

    @Config.Comment("Which blocks the pickaxe is effective against. Format as modid:blockid:meta - if meta is not specified, will use any meta.")
    @Config.DefaultStringList({ "minecraft:stone", "minecraft:cobblestone", "minecraft:sandstone",
        "minecraft:netherrack", "minecraft:hardened_clay", "minecraft:stained_hardened_clay" })
    @Config.RequiresMcRestart
    @Config.Order(300)
    public String[] includeEffective;

    @Config.DefaultFloat(5)
    @Config.RangeFloat(min = 0, max = 100)
    @Config.Sync
    @Config.Order(400)
    public float effectiveSpeedModifier;

    @Config.DefaultFloat(0.0625f)
    @Config.RangeFloat(min = 0, max = 100)
    @Config.Sync
    @Config.Order(500)
    public float ineffectiveSpeedModifier;
}
