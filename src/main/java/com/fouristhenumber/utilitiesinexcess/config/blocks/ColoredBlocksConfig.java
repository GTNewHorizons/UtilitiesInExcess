package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.gtnewhorizon.gtnhlib.config.Config;

public class ColoredBlocksConfig {

    @Config.Ignore
    public static final ColoredBlocksConfig INSTANCE = new ColoredBlocksConfig();

    @Config.Order(0)
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Name("Enable")
    public boolean enableColoredBlocks;

    @Config.Order(100)
    @Config.DefaultBoolean(false)
    @Config.Comment({ "Register oredictionary entries for colored blocks.",
        "Will allow colored blocks to work in recipes, but will also bloat NEI for recipes that people will likely never use..." })
    @Config.RequiresMcRestart
    public boolean coloredBlockOredict;

    @Config.Order(200)
    @Config.DefaultBoolean(true)
    @Config.Comment({
        "Instead of 16 static colors, with this enabled a paintbrush will be added which can be used to dye colored blocks",
        "Requires EndlessIDs" })
    @Config.RequiresMcRestart
    public boolean enableDying;
}
