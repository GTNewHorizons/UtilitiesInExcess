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
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int northChestRequiredItems;

    @Config.Comment("Amount of unique items in the list the east chest has to contain for the pseudo-inversion ritual")
    @Config.DefaultInt(12)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int eastChestRequiredItems;

    @Config.Comment("Amount of unique items in the list the south chest has to contain for the pseudo-inversion ritual")
    @Config.DefaultInt(12)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int southChestRequiredItems;

    @Config.Comment("Amount of unique items in the list the west chest has to contain for the pseudo-inversion ritual")
    @Config.DefaultInt(12)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int westChestRequiredItems;

    @Config.Comment("Amount of mobs needed to kill to pass the siege of the ritual")
    @Config.DefaultInt(7)
    @Config.RangeInt(min = 4)
    @Config.RequiresMcRestart
    public static int SiegeRequiredMobsKill;

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
