package com.fouristhenumber.utilitiesinexcess.config.items.invertedtools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.inverted_tools.etheric_sword")
public class EthericSwordConfig {

    @Config.Ignore
    public static final EthericSwordConfig INSTANCE = new EthericSwordConfig();

    @Config.DefaultBoolean(true)
    @Config.Name("Enable")
    @Config.RequiresMcRestart
    @Config.Order(0)
    public boolean enable;

    @Config.DefaultBoolean(true)
    @Config.Name("Unbreakable")
    @Config.RequiresMcRestart
    @Config.Order(100)
    public boolean unbreakable;

    @Config.DefaultFloat(4)
    @Config.RangeFloat(min = 0, max = 20)
    @Config.Sync
    @Config.Order(200)
    public float normalDamage;
    @Config.DefaultFloat(3)
    @Config.RangeFloat(min = 0, max = 20)
    @Config.Sync
    @Config.Order(300)
    public float magicDamage;
}
