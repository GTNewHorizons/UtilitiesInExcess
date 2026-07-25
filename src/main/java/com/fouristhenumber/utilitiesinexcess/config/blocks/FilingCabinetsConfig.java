package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config.Comment("Filing Cabinet Configuration")
@Config.Sync
public class FilingCabinetsConfig {

    @Config.Order(0)
    @Config.RequiresMcRestart
    @Config.Name("Enable")
    @Config.DefaultBoolean(true)
    public boolean enableFilingCabinets;

    @Config.Order(100)
    @Config.RequiresMcRestart
    @Config.Name("Upgrade Capacity")
    @Config.Comment({ "Amount of additional capacity added per capacity upgrade." })
    @Config.RangeInt(min = 1)
    public int upgradeCapacity = 4096;

    @Config.Order(200)
    @Config.RequiresMcRestart
    @Config.Name("Upgrade Limit")
    @Config.Comment({ "Maximum number of capacity upgrades that can be installed in a single filing cabinet.",
        "If this is reduced, existing cabinets will retain all of their upgrades beyond the limit." })
    @Config.RangeInt(min = 0, max = 64)
    public int upgradeCountMax = 14;

    @Config.Order(300)
    @Config.RequiresMcRestart
    @Config.Name("Basic Cabinet Settings")
    @Config.Comment("Configuration for basic filing cabinets.")
    public final CabinetConfig cabinetBasic = new CabinetConfig(256, 8192, true);

    @Config.Order(400)
    @Config.RequiresMcRestart
    @Config.Name("Advanced Cabinet Settings")
    @Config.Comment("Configuration for advanced filing cabinets.")
    public final CabinetConfig cabinetAdvanced = new CabinetConfig(512, 8192, true);

    @Config.Order(500)
    @Config.RequiresMcRestart
    @Config.Name("Elite Cabinet Settings")
    @Config.Comment("Configuration for elite filing cabinets.")
    public final CabinetConfig cabinetElite = new CabinetConfig(128, 8192, true);

    public static class CabinetConfig {

        CabinetConfig(int numSlots, int numItems, boolean enable) {
            this.numSlots = numSlots;
            this.numItems = numItems;
            this.enable = enable;
        }

        @Config.Name("Number of Slots")
        @Config.Comment({
            "The number of slots in the cabinet; corresponds to the number of distinct item types that can be held.",
            "WARNING: Reducing this may cause data loss in filing cabinets with more than that many item types!",
            "WARNING: Making this too large may make the cabinet GUI difficult to scroll through!" })
        @Config.RangeInt(min = 1)
        public int numSlots;

        @Config.Name("Number of Items")
        @Config.Comment({
            "The capacity of the cabinet; corresponds to the total number of items that can be held across all slots.",
            "WARNING: Reducing this may cause data loss in filing cabinets with more than that many total items!" })
        @Config.RangeInt(min = 1)
        public int numItems;

        @Config.Name("Enable")
        @Config.Comment("To disable the cabinet")
        public boolean enable;

    }
}
