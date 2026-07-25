package com.fouristhenumber.utilitiesinexcess.config.items.invertedtools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.inverted_tools.retrograde_hoe")
public class RetrogradeHoeConfig {

    @Config.Ignore
    public static final RetrogradeHoeConfig INSTANCE = new RetrogradeHoeConfig();

    @Config.Order(0)
    @Config.DefaultBoolean(true)
    @Config.Name("Enable")
    @Config.RequiresMcRestart
    public boolean enable;

    @Config.Order(100)
    @Config.DefaultBoolean(true)
    @Config.Name("Unbreakable")
    @Config.RequiresMcRestart
    public boolean unbreakable;

    @Config.Order(200)
    @Config.Comment("Which block transformations the Retrograde Hoe will work on. Format as modid:blockid:meta->modid:blockid:meta - if meta of source block is not specified, will work on any meta. If meta of target block is not specified, will transform into meta 0.")
    @Config.DefaultStringList({ "minecraft:dirt->minecraft:grass", "minecraft:cobblestone->minecraft:stone",
        "utilitiesinexcess:cursed_earth->utilitiesinexcess:blessed_earth",
        "utilitiesinexcess:blessed_earth->utilitiesinexcess:cursed_earth" })
    @Config.RequiresMcRestart
    public String[] blockTransformations;

}
