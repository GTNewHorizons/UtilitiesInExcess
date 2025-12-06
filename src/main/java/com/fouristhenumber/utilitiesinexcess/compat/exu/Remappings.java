package com.fouristhenumber.utilitiesinexcess.compat.exu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.IPosteaTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks.CompressedBlocksTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks.DarkPortalTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks.DecoBlock1Transformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks.SoundMufflerTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks.TrashCanTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items.DivisionSigilTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items.EnderLilyTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items.GoldenBagTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items.GoldenLassoTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items.UnstableIngotTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items.WateringCanTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities.GeneratorTransformation;

public enum Remappings {
    // spotless:off

    // make sure to leave a trailing comma
    // Direct block remappings
    ANGEL_BLOCK("ExtraUtilities:angelBlock", ModBlocks.FLOATING_BLOCK),
    BUD("ExtraUtilities:budoff", ModBlocks.BLOCK_UPDATE_DETECTOR),
    DECO_BLOCK_2("ExtraUtilities:decorativeBlock2", ModBlocks.DECORATIVE_GLASS),
    MINI_CHEST("ExtraUtilities:chestMini", ModBlocks.SIGNIFICANTLY_SHRUNK_CHEST),
    FULL_CHEST("ExtraUtilities:chestFull", ModBlocks.MARGINALLY_MAXIMISED_CHEST),
    CURTAINS("ExtraUtilities:curtains", ModBlocks.BLACKOUT_CURTAINS),
    PURE_LOVE("ExtraUtilities:pureLove", ModBlocks.PURE_LOVE),
    BEDROCKUIM_BLOCK("ExtraUtilities:block_bedrockium", ModBlocks.BEDROCKIUM_BLOCK),
    CONVEYOR("ExtraUtilities:conveyor", ModBlocks.CONVEYOR),
    GREENSCREEN("ExtraUtilities:greenscreen", ModBlocks.LAPIS_AETHERIUS),
//    PEACEFUL_TABLE("ExtraUtilities:peaceful_table_top", ModBlocks.abc), // TODO: add here once merged
    CURSED_EARTH("ExtraUtilities:cursedearthside", ModBlocks.CURSED_EARTH),
    SPIKE_WOOD("ExtraUtilities:spike_base_wood", ModBlocks.SPIKE_WOOD),
    SPIKE_IRON("ExtraUtilities:spike_base", ModBlocks.SPIKE_IRON),
    SPIKE_GOLD("ExtraUtilities:spike_base_gold", ModBlocks.SPIKE_GOLD),
    SPIKE_DIAMOND("ExtraUtilities:spike_base_diamond", ModBlocks.SPIKE_DIAMOND),
    TIMER("ExtraUtilities:timer", ModBlocks.REDSTONE_CLOCK),
    ETHEREAL_GLASS("ExtraUtilities:etherealglass", ModBlocks.ETHEREAL_GLASS),
//    ENDER_PUMP("ExtraUtilities:enderThermicPump", ModBlocks.abc), // TODO: add here once merged
//    COLORED_STONE_BRICK("ExtraUtilities:colorStoneBrick", ModBlocks.abc),
//    COLORED_PLANKS("ExtraUtilities:colorWoodPlanks", ModBlocks.abc),
//    COLORED_GLOWSTONE("ExtraUtilities:color_lightgem", ModBlocks.abc),
//    COLORED_STONE("ExtraUtilities:color_stone", ModBlocks.abc),
//    COLORED_QUARTZ_BLOCK("ExtraUtilities:color_quartzBlock", ModBlocks.abc),
//    COLORED_SOUL_SAND("ExtraUtilities:color_hellsand", ModBlocks.abc),
//    COLORED_REDSTONE_LAMP("ExtraUtilities:color_redstoneLight", ModBlocks.abc),
//    COLORED_BRICKS("ExtraUtilities:color_brick", ModBlocks.abc),
//    COLORED_COBBLESTONE("ExtraUtilities:color_stonebrick", ModBlocks.abc),
//    COLORED_LAPIS_BLOCK("ExtraUtilities:color_blockLapis", ModBlocks.abc),
//    COLORED_OBSIDIAN("ExtraUtilities:color_obsidian", ModBlocks.abc),
//    COLORED_REDSTONE_BLOCK("ExtraUtilities:color_blockRedstone", ModBlocks.abc),
//    COLORED_COAL_BLOCK("ExtraUtilities:color_blockCoal", ModBlocks.abc),

    // Direct item remappings
    GLOVE("ExtraUtilities:glove", ModItems.GLOVE),
    HEAVENLY_RING("ExtraUtilities:angelRing", ModItems.HEAVENLY_RING), // TODO validate when ring variations are merged
    FIRE_BATTERY("ExtraUtilities:heatingElement", ModItems.FIRE_BATTERY),
    ARCHITECTS_WAND("ExtraUtilities:buildersWand", ModItems.ARCHITECTS_WAND),
    SUPER_ARCHITECTS_WAND("ExtraUtilities:creativeBuildersWand", ModItems.ARCHITECTS_WAND),
    INVERTED_SWORD("ExtraUtilities:ethericSword", ModItems.ETHERIC_SWORD),
    INVERTED_PICKAXE("ExtraUtilities:destructionpickaxe", ModItems.DESTRUCTION_PICKAXE),
    INVERTED_AXE("ExtraUtilities:defoliageAxe", ModItems.GLUTTONS_AXE),
    INVERTED_SHOVEL("ExtraUtilities:erosionShovel", ModItems.ANTI_PARTICULATE_SHOVEL),
    INVERTED_HOE("ExtraUtilities:temporalHoe", ModItems.REVERSING_HOE),
    INVERTED_SHEARS("ExtraUtilities:shears", ModItems.PRECISION_SHEARS),
    XRAY_GLASSES("ExtraUtilities:sonar_goggles", ModItems.XRAY_GLASSES),
    BEDROCKUIM_INGOT("ExtraUtilities:bedrockiumIngot", ModItems.BEDROCKIUM_INGOT),
    SCANNER("ExtraUtilities:scanner", ModItems.BLOCK_ANALYZER),
    // Item Transformations
    GOLDEN_BAG(new GoldenBagTransformation()),
    WATERING_CAN(new WateringCanTransformation()),
    INVERSION_SIGIL(new DivisionSigilTransformation()),
    MOB_JAR(new GoldenLassoTransformation()),
    INVERTED_INGOT(new UnstableIngotTransformation()),
    // Block Transformations
    SOUND_MUFFLER(new SoundMufflerTransformation()),
    DECO_BLOCK_1(new DecoBlock1Transformation()),
    COMPRESSED_BLOCKS(new CompressedBlocksTransformation()),
    DARK_PORTAL(new DarkPortalTransformation()),
    TRASH_CANS(new TrashCanTransformation()),
    ENDER_LOTUS(new EnderLilyTransformation()),
    // Tile Entity Transformation
    GENERATORS(new GeneratorTransformation()),

    ; // leave trailing semicolon
    // spotless:on

    public static final Remappings[] VALUES = values();

    public static final HashMap<String, Item> itemMappings = new HashMap<>();
    public static final HashMap<String, Block> blockMappings = new HashMap<>();
    public static final List<IPosteaTransformation> transformations = new ArrayList<>();

    public static void init() {
        for (Remappings remapping : VALUES) {
            if (remapping.replacementItem != null) {
                itemMappings.put(remapping.getName(), remapping.replacementItem);
            }
            if (remapping.replacementBlock != null) {
                blockMappings.put(remapping.getName(), remapping.replacementBlock);
            }
            if (remapping.transformation != null) {
                transformations.add(remapping.transformation);
            }
        }
    }

    public static void initTransformationMappings() {
        for (Remappings remapping : VALUES) {
            if (remapping.transformation != null) {
                remapping.transformation.addItemRemappings(itemMappings);
                remapping.transformation.addBlockRemappings(blockMappings);
            }
        }
    }

    private String oldName;
    private Block replacementBlock = null;
    private Item replacementItem = null;
    private IPosteaTransformation transformation = null;

    Remappings(String oldName, ModBlocks modBlock) {
        this.oldName = oldName;
        this.replacementBlock = modBlock.get();
        this.replacementItem = modBlock.getItem();
    }

    Remappings(String oldName, ModItems modItem) {
        this.oldName = oldName;
        this.replacementItem = modItem.get();
    }

    Remappings(IPosteaTransformation posteaTransformation) {
        this.transformation = posteaTransformation;
    }

    public String getName() {
        return oldName;
    }
}
