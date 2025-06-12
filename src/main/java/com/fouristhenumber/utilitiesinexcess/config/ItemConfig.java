package com.fouristhenumber.utilitiesinexcess.config;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items")
public class ItemConfig {

    @Config.Comment("Unstable Tools Configuration")
    public static final UnstableTools unstableTools = new UnstableTools();

    @Config.LangKey("utilitiesinexcess.config.item.unstable_tools")
    public static class UnstableTools {
        @Config.DefaultBoolean(true)
        public boolean enableGluttonsAxe;

        @Config.DefaultBoolean(true)
        public boolean enableDestructionPickaxe;

        @Config.DefaultBoolean(true)
        public boolean enableAntiParticulateShovel;
    }

    @Config.DefaultBoolean(true)
    public static boolean enableHeavenlyRing;

    @Config.DefaultBoolean(true)
    public static boolean enableMobJar;
}
