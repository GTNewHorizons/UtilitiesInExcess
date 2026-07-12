package com.fouristhenumber.utilitiesinexcess.config.items.invertedtools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.inverted_tools")
public class InvertedTools {

    @Config.Ignore
    public static final InvertedTools INSTANCE = new InvertedTools();

    @Config.LangKey("item.etheric_sword.name")
    @Config.Order(0)
    public final EthericSwordConfig ethericSwordConfig = EthericSwordConfig.INSTANCE;

    @Config.LangKey("item.destruction_pickaxe.name")
    @Config.Order(100)
    public final DestructionPickaxeConfig destructionPickaxeConfig = DestructionPickaxeConfig.INSTANCE;

    @Config.LangKey("item.gourmands_axe.name")
    @Config.Order(200)
    public final GourmandsAxeConfig gourmandsAxeConfig = GourmandsAxeConfig.INSTANCE;

    @Config.LangKey("item.anti_gravity_shovel.name")
    @Config.Order(300)
    public final AntiGravityShovelConfig antiGravityShovelConfig = AntiGravityShovelConfig.INSTANCE;

    @Config.LangKey("item.reversing_hoe.name")
    @Config.Order(400)
    public final ReversingHoeConfig reversingHoeConfig = ReversingHoeConfig.INSTANCE;

    @Config.LangKey("item.precision_shears.name")
    @Config.Order(500)
    public final PrecisionShearsConfig precisionShearsConfig = PrecisionShearsConfig.INSTANCE;

}
