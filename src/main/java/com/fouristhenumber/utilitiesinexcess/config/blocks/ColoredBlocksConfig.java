package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.gtnewhorizon.gtnhlib.config.Config;

public class ColoredBlocksConfig {

    @Config.Ignore
    public static final ColoredBlocksConfig INSTANCE = new ColoredBlocksConfig();

    @Config.Order(0)
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Name("Enable Colored Blocks")
    public boolean enableColoredBlocks;

    @Config.Order(50)
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Name("Enable Paint Roller")
    public boolean enablePaintRoller;

    @Config.Order(100)
    @Config.DefaultBoolean(false)
    @Config.Comment({ "Register oredictionary entries for colored blocks.",
        "Will allow colored blocks to work in recipes, but will also bloat NEI for recipes that people will likely never use..." })
    @Config.RequiresMcRestart
    public boolean coloredBlockOredict;

    @Config.Order(200)
    @Config.DefaultBoolean(true)
    @Config.Comment({
        "Instead of 16 static colors, with this enabled a paint roller will be added which can be used to dye colored blocks",
        "Requires EndlessIDs", "Paint roller and colored blocks must be enabled too" })
    @Config.RequiresMcRestart
    public boolean enableDying;

    // spotless:off
    @Config.Order(300)
    @Config.Comment({
        "Add extra blocks to be turned into colored blocks",
        "Each of these will be registered as a new block with a new ID.",
        "",
        "Format: MODID;BLOCKNAME;BLOCKMETA;BRIGHTNESS;TYPE;DISPLAYNAME",
        "   MODID: Mod ID of the mod that adds the block. NOT DOMAIN. (example: \"minecraft\")",
        "   BLOCKNAME: Name of the block (example: \"stained_glass\")",
        "   BLOCKMETA: Block's meta/damage (example: \"0\")",
        "   BRIGHTNESS: Brightness multiplier (example: \"1.5\")",
        "   TYPE: Colored block type selection (example: \"DEFAULT\")",
        "   DISPLAYNAME: (optional) a new display name (example: \"Stained Glass\")",
        "",
        "Available Types:",
        "   DEFAULT: A regular block, no special features",
        "   CTM: Block with connected textures, right click to toggle connecting to different colors (requires angelica)",
        "   ROTATABLE: Block that can be rotated (once) 90 degrees with a right click",
        "   LIGHT: Block that emits light, right click to toggle light",
        "",
        "Example: minecraft;stained_glass;0;1.5;CTM;Stained Glass",
        "Example: minecraft;double_stone_slab;8;1.5;DEFAULT",
        "Example: minecraft;pumpkin;0;1.5;ROTATABLE",
        ""
    })
    // spotless:on
    @Config.DefaultStringList({})
    @Config.RequiresMcRestart
    public String[] extraColoredBlocks;
}
