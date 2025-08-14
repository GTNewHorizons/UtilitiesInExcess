package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config.Comment("RF Generators Configuration")
@Config.LangKey("utilitiesinexcess.config.item.generator")
@Config(modid = UtilitiesInExcess.MODID, category = "blocks.generator")
public class GeneratorConfig {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableFurnaceGenerator;

}
