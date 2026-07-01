package com.fouristhenumber.utilitiesinexcess.config.items;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.wateringcan")
public class WateringCanConfig {

    @Config.Ignore
    public static final WateringCanConfig INSTANCE = new WateringCanConfig();

    @Config.LangKey("utilitiesinexcess.config.item.watering_can_tier")
    public final WateringCanTier Tier = new WateringCanTier();

    @Config.LangKey("utilitiesinexcess.config.item.watering_can_flowering")
    public final WateringCanFlowering Flowering = new WateringCanFlowering();

    @Config.DefaultBoolean(false)
    @Config.Name("Allow Automated Watering")
    @Config.Comment("If true, the watering can can be used by FakePlayers.")
    public boolean allowAutomatedWatering;

    @Config.DefaultBoolean(true)
    @Config.Name("Allow Watering Can Toggle")
    @Config.Comment("If true, the watering can can be toggled on and off by Crouch + right-clicking.")
    @Config.Sync
    public boolean allowWateringCanToggle;

    @Config.DefaultBoolean(true)
    @Config.Name("Walking Speed Penalty")
    @Config.Comment("If true, the player will be slowed down while using the watering can.")
    @Config.Sync
    public boolean walkingSpeedPenalty;

    public static class WateringCanFlowering {

        @Config.DefaultBoolean(true)
        @Config.Name("Allow Flower Duplication")
        @Config.Comment("If true, using the watering can on a flower will duplicate that flower.")
        public boolean allowFlowerDuplication;

        @Config.DefaultBoolean(true)
        @Config.Name("Allow Flower Spawning")
        @Config.Comment("If true, using the watering can on a grass block will spawn random flowers on that block.")
        public boolean allowFlowerSpawning;

        @Config.DefaultBoolean(true)
        @Config.Name("Spawn Flowers Beside Short Blocks")
        @Config.Comment("If true, when targeting short blocks (height < 0.85), the flower’s spawn height is lowered by the block’s height to attempt spawning beside the block instead of directly on top.")
        public boolean adjustFlowerSpawnHeightForShortBlocks;

    }

    public static class WateringCanTier {

        @Config.DefaultBoolean(true)
        @Config.Name("Enable Basic Watering Can")
        @Config.Comment("Enable or disable the Basic Watering Can item.")
        @Config.RequiresMcRestart
        public boolean enableWateringCanBasic;

        @Config.DefaultBoolean(true)
        @Config.Name("Enable Advanced Watering Can")
        @Config.Comment("Enable or disable the Advanced Watering Can item.")
        @Config.RequiresMcRestart
        public boolean enableWateringCanAdvanced;

        @Config.DefaultBoolean(true)
        @Config.Name("Enable Elite Watering Can")
        @Config.Comment("Enable or disable the Elite Watering Can item.")
        @Config.RequiresMcRestart
        public boolean enableWateringCanElite;

    }
}
