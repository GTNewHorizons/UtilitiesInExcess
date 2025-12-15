package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "blocks.drum")

public class DrumConfig {

    @Config.DefaultBoolean(true)
    @Config.Comment("Change it to MB vs L's")
    public static boolean unitToDisplay;


}
