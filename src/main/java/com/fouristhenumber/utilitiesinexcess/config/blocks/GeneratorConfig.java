package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config.Comment("RF Generators Configuration")
@Config.LangKey("utilitiesinexcess.config.item.generator")
@Config(modid = UtilitiesInExcess.MODID, category = "blocks.generator")
public class GeneratorConfig {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableLowTemperatureFurnaceGenerator;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableFurnaceGenerator;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableHighTemperatureFurnaceGenerator;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableLavaGenerator;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableEnderGenerator;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableRedstoneGenerator;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableFoodGenerator;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enablePotionGenerator;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableSolarGenerator;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableTNTGenerator;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enablePinkGenerator;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableNetherStarGenerator;
}
