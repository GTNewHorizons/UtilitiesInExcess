package com.fouristhenumber.utilitiesinexcess.config.items.unstabletools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.unstable_tools.reversing_hoe")
public class ReversingHoeConfig {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enable;
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean unbreakable;

    @Config.Comment("Which block transformations the reversing hoe will work on. Format as modid:blockid:meta->modid:blockid:meta - if meta of source block is not specified, will work on any meta. If meta of target block is not specified, will transform into meta 0.")
    @Config.DefaultStringList({ "minecraft:dirt->minecraft:grass", "minecraft:cobblestone->minecraft:stone",
        "utilitiesinexcess:cursed_earth->utilitiesinexcess:blessed_earth",
        "utilitiesinexcess:blessed_earth->utilitiesinexcess:cursed_earth" })
    @Config.RequiresMcRestart
    public static String[] blockTransformations;

}
