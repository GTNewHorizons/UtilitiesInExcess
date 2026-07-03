package com.fouristhenumber.utilitiesinexcess.config.items;

import com.gtnewhorizon.gtnhlib.config.Config;

public class ArchitectsWandsConfig {

    @Config.Ignore
    public static final ArchitectsWandsConfig INSTANCE = new ArchitectsWandsConfig();

    @Config.Order(0)
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public boolean enableArchitectsWand;

    @Config.Order(100)
    @Config.DefaultInt(9)
    @Config.RangeFloat(min = 1)
    @Config.Sync
    public int architectsWandBuildLimit;

    @Config.Order(200)
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public boolean enableSuperArchitectsWand;

    @Config.Order(300)
    @Config.DefaultInt(49)
    @Config.RangeFloat(min = 1)
    @Config.Sync
    public int superArchitectsWandBuildLimit;

    @Config.Order(400)
    @Config.DefaultInt(200)
    @Config.RangeFloat(min = 1)
    @Config.Sync
    public int architectsWandCreativeBuildLimit;

    @Config.Order(500)
    @Config.DefaultInt(100)
    @Config.Comment("[GT5U] Durability damage dealt to Trowels per block placed by the Architect's Wand. (Set to 0 to disable)")
    public int damageTrowelWithArchitectsWand;
}
