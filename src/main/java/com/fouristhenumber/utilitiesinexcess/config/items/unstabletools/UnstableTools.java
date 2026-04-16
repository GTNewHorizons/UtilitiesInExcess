package com.fouristhenumber.utilitiesinexcess.config.items.unstabletools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

@Config.Comment("Unstable Tools Configuration")
@Config.LangKey("utilitiesinexcess.config.item.unstable_tools")
@Config(modid = UtilitiesInExcess.MODID, category = "items.unstable_tools")
public class UnstableTools {

    // Not sure abt nested categories, lets disccuss that later
    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(UnstableTools.class);
        ConfigurationManager.registerConfig(GluttonsAxeConfig.class);
        ConfigurationManager.registerConfig(EthericSwordConfig.class);
        ConfigurationManager.registerConfig(AntiParticulateShovelConfig.class);
        ConfigurationManager.registerConfig(DestructionPickaxeConfig.class);
        ConfigurationManager.registerConfig(ReversingHoeConfig.class);
        ConfigurationManager.registerConfig(PrecisionShearsConfig.class);
    }

}
