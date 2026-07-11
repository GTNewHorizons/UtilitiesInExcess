package com.fouristhenumber.utilitiesinexcess.config.items.invertedtools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.inverted_tools")
public class InvertedTools {

    @Config.Ignore
    public static final InvertedTools INSTANCE = new InvertedTools();

    @Config.LangKey("item.liminal_sword.name")
    @Config.Order(0)
    public final LiminalSwordConfig liminalSwordConfig = LiminalSwordConfig.INSTANCE;

    @Config.LangKey("item.erasure_pickaxe.name")
    @Config.Order(100)
    public final ErasurePickaxeConfig erasurePickaxeConfig = ErasurePickaxeConfig.INSTANCE;

    @Config.LangKey("item.sating_axe.name")
    @Config.Order(200)
    public final SatingAxeConfig satingAxeConfig = SatingAxeConfig.INSTANCE;

    @Config.LangKey("item.anti_gravity_shovel.name")
    @Config.Order(300)
    public final AntiGravityShovelConfig antiGravityShovelConfig = AntiGravityShovelConfig.INSTANCE;

    @Config.LangKey("item.retrograde_hoe.name")
    @Config.Order(400)
    public final RetrogradeHoeConfig retrogradeHoeConfig = RetrogradeHoeConfig.INSTANCE;

    @Config.LangKey("item.precision_shears.name")
    @Config.Order(500)
    public final PrecisionShearsConfig precisionShearsConfig = PrecisionShearsConfig.INSTANCE;

}
