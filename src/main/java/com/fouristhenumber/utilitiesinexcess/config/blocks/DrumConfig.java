package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "blocks.drum")

public class DrumConfig {

    @Config.DefaultBoolean(true)
    @Config.Comment("Change it to MB vs L's")
    public static boolean unitToDisplay;

    @Config.RangeInt(min = 16000, max = 256000)
    @Config.Comment("size of the the drum")
    @Config.DefaultInt(16000)
    public static int drumSize;

}
