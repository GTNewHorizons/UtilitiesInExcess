package com.fouristhenumber.utilitiesinexcess.config.items;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.UnstableTools;
import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

@Config(modid = UtilitiesInExcess.MODID, category = "items")
public class ItemConfig {

    @Config.DefaultBoolean(false)
    public static boolean registerDisabledItems;

    @Config.DefaultBoolean(true)
    public static boolean shiftForDescription;

    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(ItemConfig.class);
        UnstableTools.registerConfig();
        ConfigurationManager.registerConfig(WateringCanConfig.class);
    }

    @Config.DefaultBoolean(true)
    public static boolean enableHeavenlyRing;

    @Config.DefaultBoolean(true)
    public static boolean enableMobJar;
}
