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

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableInvertedIngot;
}
