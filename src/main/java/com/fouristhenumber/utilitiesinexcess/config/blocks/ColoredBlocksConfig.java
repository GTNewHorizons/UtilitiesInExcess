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
        "Instead of 16 static colors, with this enabled a paint_roller will be added which can be used to dye colored blocks",
        "Requires EndlessIDs" })
    @Config.RequiresMcRestart
    public boolean enableDying;

    @Config.Order(300)
    @Config.Comment({ "WARNING! THIS IS AN ADVANCED CONFIG OPTION!",
        "If you don't know what you're doing do not touch.", "", "Add extra blocks to be turned into colored blocks",
        "", "Format: MODID;BLOCKNAME;BRIGHTNESS;TEXTUREDOMAIN;TEXTURENAME",
        "   MODID: Mod ID of the mod that adds the block. NOT DOMAIN. (example: \"minecraft\")",
        "   BLOCKNAME: Name of the block (example: \"wool\")",
        "   BRIGHTNESS: Brightness multiplier (example: \"1.5\")",
        "   TEXTUREDOMAIN: Domain for texture (example: \"utilitiesinexcess\")",
        "   TEXTURENAME: Texture location (example: \"textures/blocks/block_update_detector_active.png\")", "",
        "Example: minecraft;double_stone_slab;1;minecraft;textures/blocks/stone_slab_top.png",
        "Example: utilitiesinexcess;block_update_detector;1.5;utilitiesinexcess;textures/blocks/block_update_detector_active.png",
        "" })
    @Config.DefaultStringList({})
    @Config.RequiresMcRestart
    public String[] extraColoredBlocks;
}
