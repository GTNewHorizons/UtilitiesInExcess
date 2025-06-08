package com.fouristhenumber.utilitiesinexcess.config;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items")
public class ItemConfig {

    @Config.Comment("Watering Can Configuration")
    public static final WateringCan wateringCan = new WateringCan();

    @Config.DefaultBoolean(true)
    public static boolean enableHungerAxe;

    @Config.DefaultBoolean(true)
    public static boolean enableHeavenlyRing;

    @Config.DefaultBoolean(true)
    public static boolean enableMobJar;

    @Config.LangKey("utilitiesinexcess.config.item.warningCan")
    public static class WateringCan {

        @Config.DefaultBoolean(false)
        @Config.Name("Allow Automated Watering")
        @Config.Comment("If true, the watering can be used by FakePlayers.")
        public boolean allowAutomatedWatering;

        @Config.DefaultBoolean(true)
        @Config.Name("Allow Watering Can Toggle")
        @Config.Comment("If true, the watering can can be toggled on and off by Crouch + right-clicking.")
        public boolean allowWateringCanToggle;

        @Config.DefaultBoolean(true)
        @Config.Name("Allow Multiply Flower")
        @Config.Comment("If true, using the watering can on a flower will duplicate that flower, allowing for easier flower farming.")
        public boolean allowFlowerDuplication;

        @Config.DefaultBoolean(true)
        @Config.Name("Enable Watering Can")
        @Config.Comment("Enable or disable the Watering Can item.")
        public boolean enableWateringCan;

        @Config.DefaultBoolean(true)
        @Config.Sync
        @Config.Name("Walking Speed Penalty")
        @Config.Comment("If true, the player will be slowed down while using the watering can.")
        public boolean WalkingSpeedPenalty;

    }

}
