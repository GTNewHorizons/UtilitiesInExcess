package com.fouristhenumber.utilitiesinexcess.config.items;

import com.gtnewhorizon.gtnhlib.config.Config;

public class BuildersWandsConfig {

    @Config.Ignore
    public static final BuildersWandsConfig INSTANCE = new BuildersWandsConfig();

    @Config.Order(0)
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public boolean enableBuildersWand;

    @Config.Order(100)
    @Config.DefaultInt(9)
    @Config.RangeFloat(min = 1)
    @Config.Sync
    public int buildersWandBuildLimit;

    @Config.Order(200)
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public boolean enableSuperBuildersWand;

    @Config.Order(300)
    @Config.DefaultInt(49)
    @Config.RangeFloat(min = 1)
    @Config.Sync
    public int superBuildersWandBuildLimit;

    @Config.Order(400)
    @Config.DefaultInt(200)
    @Config.RangeFloat(min = 1)
    @Config.Sync
    public int buildersWandCreativeBuildLimit;

    @Config.Order(500)
    @Config.DefaultInt(100)
    @Config.Comment("[GT5U] Durability damage dealt to Trowels per block placed by the Builder's Wand. (Set to 0 to disable)")
    public int damageTrowelWithBuildersWand;
}
