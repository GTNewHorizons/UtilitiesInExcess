package com.fouristhenumber.utilitiesinexcess.compat.exu;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.FMPItems;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items.DivisionSigilTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items.GoldenBagTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items.GoldenLassoTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities.CollectorTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities.DrumTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities.FullChestTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities.GeneratorTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities.MiniChestTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities.SpikeTransformation;
import com.gtnewhorizons.postea.api.BlockReplacementManager;
import com.gtnewhorizons.postea.api.ItemStackReplacementManager;
import com.gtnewhorizons.postea.api.TileEntityReplacementManager;

public class PosteaTransforms {

    public static void postInit() {
        transformItems();
        transformBlocks();
        transformTileEntities();
        ignoreMissingMappings();
    }

    private static void transformItems() {
        // Simple transformers
        ItemStackReplacementManager.addSimpleReplacement("ExtraUtilities:glove", ModItems.GLOVE.get(), true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:angelRing", 0, ModItems.HEAVENLY_RING_MAGIC.get(), 0, true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:angelRing", 1, ModItems.HEAVENLY_RING_FEATHER.get(), 0, true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:angelRing", 2, ModItems.HEAVENLY_RING_FAIRY.get(), 0, true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:angelRing", 3, ModItems.HEAVENLY_RING_DRAGON.get(), 0, true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:angelRing", 4, ModItems.HEAVENLY_RING_METAL.get(), 0, true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:heatingElement", ModItems.FIRE_BATTERY.get(), true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:builderswand", ModItems.ARCHITECTS_WAND.get(), true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:creativebuilderswand", ModItems.SUPER_ARCHITECTS_WAND.get(), true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:ethericsword", ModItems.ETHERIC_SWORD.get(), true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:destructionpickaxe", ModItems.DESTRUCTION_PICKAXE.get(), true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:defoliageAxe", ModItems.GOURMANDS_AXE.get(), true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:erosionShovel", ModItems.ANTI_PARTICULATE_SHOVEL.get(), true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:temporalHoe", ModItems.REVERSING_HOE.get(), true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:shears", ModItems.PRECISION_SHEARS.get(), true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:sonar_goggles", ModItems.XRAY_GLASSES.get(), true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:bedrockiumIngot", ModItems.BEDROCKIUM_INGOT.get(), true);
        ItemStackReplacementManager.addSimpleReplacement("ExtraUtilities:scanner", ModItems.BLOCK_ANALYZER.get(), true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:unstableingot", 0, ModItems.INVERTED_INGOT.get(), 0, true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:unstableingot", 2, ModItems.INVERTED_INGOT.get(), 0, true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:unstableingot", 1, ModItems.INVERTED_NUGGET.get(), 0, true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:watering_can", 0, ModItems.WATERING_CAN_BASIC.get(), 0, true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:watering_can", 1, ModItems.WATERING_CAN_BASIC.get(), 0, true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:watering_can", 2, ModItems.WATERING_CAN_BASIC.get(), 0, true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:watering_can", 3, ModItems.WATERING_CAN_ELITE.get(), 0, true);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:plant/ender_lilly", ModItems.ENDER_LOTUS_SEED.get(), true);
        ItemStackReplacementManager.addSimpleReplacement("ExtraUtilities:lawSword", ModItems.CHUNCHUNMARU.get(), true);
        ItemStackReplacementManager.addSimpleReplacement("ExtraUtilities:drum", 0, ModBlocks.DRUM.getItem(), 0);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:drum", 1, ModBlocks.BEDROCKIUM_DRUM.getItem(), 0);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:microblocks", 1, FMPItems.UE_MULTI_PART.get(), 0);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:microblocks", 2, FMPItems.UE_MULTI_PART.get(), 1);
        ItemStackReplacementManager
            .addSimpleReplacement("ExtraUtilities:microblocks", 3, FMPItems.UE_MULTI_PART.get(), 2);
        // Custom transformers
        ItemStackReplacementManager
            .addTransformationHandler("ExtraUtilities:divisionSigil", new DivisionSigilTransformation());
        ItemStackReplacementManager
            .addTransformationHandler("ExtraUtilities:golden_bag", new GoldenBagTransformation());
        ItemStackReplacementManager
            .addTransformationHandler("ExtraUtilities:golden_lasso", new GoldenLassoTransformation());
    }

    private static void transformBlocks() {
        compressedBlocks();
        coloredBlocks();
        decoBlocks();
        generatorBlocks();
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:chestFull", ModBlocks.MARGINALLY_MAXIMISED_CHEST.get());
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:chestMini", ModBlocks.SIGNIFICANTLY_SHRUNK_CHEST.get());
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:enderCollector", ModBlocks.COLLECTOR.get());
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:decorativeBlock2", ModBlocks.DECORATIVE_GLASS.get());
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:sound_muffler", 0, ModBlocks.SOUND_MUFFLER.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:sound_muffler", 1, ModBlocks.RAIN_MUFFLER.get(), 0);
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:trashcan", 0, ModBlocks.TRASH_CAN_ITEM.get(), 0);
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:trashcan", 1, ModBlocks.TRASH_CAN_FLUID.get(), 0);
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:trashcan", 2, ModBlocks.TRASH_CAN_ENERGY.get(), 0);
        // Our conveyors have inverted meta for whatever reason
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:conveyor", 0, ModBlocks.CONVEYOR.get(), 2);
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:conveyor", 1, ModBlocks.CONVEYOR.get(), 3);
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:conveyor", 2, ModBlocks.CONVEYOR.get(), 0);
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:conveyor", 3, ModBlocks.CONVEYOR.get(), 1);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:dark_portal", 0, ModBlocks.UNDERWORLD_PORTAL.get());
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:dark_portal", 2, ModBlocks.END_OF_TIME_PORTAL.get());
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:angelBlock", ModBlocks.FLOATING_BLOCK.get());
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:budoff", 3, ModBlocks.ADVANCED_BLOCK_UPDATE_DETECTOR.get(), 0);
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:budoff", ModBlocks.BLOCK_UPDATE_DETECTOR.get());
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:curtains", ModBlocks.BLACKOUT_CURTAINS.get());
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:pureLove", ModBlocks.PURE_LOVE.get());
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:block_bedrockium", ModBlocks.BEDROCKIUM_BLOCK.get());
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:greenscreen", ModBlocks.LAPIS_AETHERIUS.get());
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:peaceful_table_top", ModBlocks.PACIFISTS_BENCH.get());
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:cursedearthside", ModBlocks.CURSED_EARTH.get());
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:spike_base_wood", ModBlocks.SPIKE_WOOD.get(), 0);
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:spike_base", ModBlocks.SPIKE_IRON.get(), 0);
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:spike_base_gold", ModBlocks.SPIKE_GOLD.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:spike_base_diamond", ModBlocks.SPIKE_DIAMOND.get(), 0);
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:timer", ModBlocks.REDSTONE_CLOCK.get());
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:etherealglass", ModBlocks.ETHEREAL_GLASS.get());
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:enderThermicPump", ModBlocks.SMART_PUMP.get());
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:plant/ender_lilly", ModBlocks.ENDER_LOTUS.get(), true);
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:magnumTorch", ModBlocks.GIGA_TORCH.get());
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:chandelier", ModBlocks.CHANDELIER.get());
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:trading_post", ModBlocks.TRADING_POST.get());
    }

    private static void transformTileEntities() {
        TileEntityReplacementManager.tileEntityTransformer("TileFullChest", FullChestTransformation::transform);
        TileEntityReplacementManager.tileEntityTransformer("TileMiniChest", MiniChestTransformation::transform);
        TileEntityReplacementManager.tileEntityTransformer("TileEnderCollector", CollectorTransformation::transform);
        TileEntityReplacementManager.tileEntityTransformer("drum", DrumTransformation::transform);

        SpikeTransformation.registerIDResolvers();
        TileEntityReplacementManager.tileEntityTransformer("TileEntityEnchantedSpike", SpikeTransformation::transform);

        GeneratorTransformation.postLoad();
    }

    private static void compressedBlocks() {
        for (int i = 0; i < 8; i++) {
            BlockReplacementManager.addSimpleReplacement(
                "ExtraUtilities:cobblestone_compressed",
                i,
                ModBlocks.COMPRESSED_COBBLESTONE.get(),
                i);
        }
        for (int i = 8; i < 12; i++) {
            BlockReplacementManager.addSimpleReplacement(
                "ExtraUtilities:cobblestone_compressed",
                i,
                ModBlocks.COMPRESSED_DIRT.get(),
                i - 8);
        }
        for (int i = 12; i < 14; i++) {
            BlockReplacementManager.addSimpleReplacement(
                "ExtraUtilities:cobblestone_compressed",
                i,
                ModBlocks.COMPRESSED_GRAVEL.get(),
                i - 12);
        }
        for (int i = 14; i < 16; i++) {
            BlockReplacementManager.addSimpleReplacement(
                "ExtraUtilities:cobblestone_compressed",
                i,
                ModBlocks.COMPRESSED_SAND.get(),
                i - 14);
        }
    }

    private static void decoBlocks() {
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:decorativeBlock1", 0, ModBlocks.DECORATIVE_BLOCKS.get(), 7);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:decorativeBlock1", 1, ModBlocks.DECORATIVE_BLOCKS.get(), 5);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:decorativeBlock1", 2, ModBlocks.DECORATIVE_BLOCKS.get(), 2);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:decorativeBlock1", 3, ModBlocks.DECORATIVE_BLOCKS.get(), 4);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:decorativeBlock1", 4, ModBlocks.DECORATIVE_BLOCKS.get(), 8);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:decorativeBlock1", 5, ModBlocks.INVERTED_BLOCK.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:decorativeBlock1", 6, ModBlocks.DECORATIVE_BLOCKS.get(), 6);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:decorativeBlock1", 7, ModBlocks.DECORATIVE_BLOCKS.get(), 9);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:decorativeBlock1", 8, ModBlocks.MAGIC_WOOD.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:decorativeBlock1", 9, ModBlocks.DECORATIVE_BLOCKS.get(), 3);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:decorativeBlock1", 10, ModBlocks.DECORATIVE_BLOCKS.get(), 10);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:decorativeBlock1", 11, ModBlocks.ENDSPARK.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:decorativeBlock1", 12, ModBlocks.DECORATIVE_BLOCKS.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:decorativeBlock1", 13, ModBlocks.DECORATIVE_BLOCKS.get(), 1);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:decorativeBlock1", 14, ModBlocks.DECORATIVE_BLOCKS.get(), 11);
    }

    private static void coloredBlocks() {
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:colorStoneBrick", ModBlocks.COLORED_STONE_BRICKS.get());
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:colorWoodPlanks", ModBlocks.COLORED_WOOD_PLANKS.get());
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:color_lightgem", ModBlocks.COLORED_GLOWSTONE.get());
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:color_stone", ModBlocks.COLORED_STONE.get());
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:color_quartzBlock", ModBlocks.COLORED_QUARTZ_BLOCK.get());
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:color_hellsand", ModBlocks.COLORED_SOUL_SAND.get());
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:color_redstoneLight", ModBlocks.COLORED_REDSTONE_LAMP.get());
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:color_brick", ModBlocks.COLORED_BRICKS.get());
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:color_stonebrick", ModBlocks.COLORED_COBBLESTONE.get());
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:color_blockLapis", ModBlocks.COLORED_LAPIS_BLOCK.get());
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:color_obsidian", ModBlocks.COLORED_OBSIDIAN.get());
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:color_blockRedstone", ModBlocks.COLORED_REDSTONE_BLOCK.get());
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:color_blockCoal", ModBlocks.COLORED_COAL_BLOCK.get());
    }

    private static void generatorBlocks() {
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator", 0, ModBlocks.LOW_TEMPERATURE_FURNACE_GENERATOR.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator", 1, ModBlocks.FURNACE_GENERATOR.get(), 0);
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:generator", 2, ModBlocks.LAVA_GENERATOR.get(), 0);
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:generator", 3, ModBlocks.ENDER_GENERATOR.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator", 4, ModBlocks.REDSTONE_GENERATOR.get(), 0);
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:generator", 5, ModBlocks.FOOD_GENERATOR.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator", 6, ModBlocks.POTION_GENERATOR.get(), 0);
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:generator", 7, ModBlocks.SOLAR_GENERATOR.get(), 0);
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:generator", 8, ModBlocks.TNT_GENERATOR.get(), 0);
        BlockReplacementManager.addSimpleReplacement("ExtraUtilities:generator", 9, ModBlocks.PINK_GENERATOR.get(), 0);
        BlockReplacementManager.addSimpleReplacement(
            "ExtraUtilities:generator",
            10,
            ModBlocks.HIGH_TEMPERATURE_FURNACE_GENERATOR.get(),
            0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator", 11, ModBlocks.NETHER_STAR_GENERATOR.get(), 0);

        BlockReplacementManager.addSimpleReplacement(
            "ExtraUtilities:generator.8",
            0,
            ModBlocks.LOW_TEMPERATURE_FURNACE_GENERATOR_PLUS.get(),
            0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.8", 1, ModBlocks.FURNACE_GENERATOR_PLUS.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.8", 2, ModBlocks.LAVA_GENERATOR_PLUS.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.8", 3, ModBlocks.ENDER_GENERATOR_PLUS.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.8", 4, ModBlocks.REDSTONE_GENERATOR_PLUS.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.8", 5, ModBlocks.FOOD_GENERATOR_PLUS.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.8", 6, ModBlocks.POTION_GENERATOR_PLUS.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.8", 7, ModBlocks.SOLAR_GENERATOR_PLUS.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.8", 8, ModBlocks.TNT_GENERATOR_PLUS.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.8", 9, ModBlocks.PINK_GENERATOR_PLUS.get(), 0);
        BlockReplacementManager.addSimpleReplacement(
            "ExtraUtilities:generator.8",
            10,
            ModBlocks.HIGH_TEMPERATURE_FURNACE_GENERATOR.get(),
            0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.8", 11, ModBlocks.NETHER_STAR_GENERATOR.get(), 0);

        BlockReplacementManager.addSimpleReplacement(
            "ExtraUtilities:generator.64",
            0,
            ModBlocks.LOW_TEMPERATURE_FURNACE_GENERATOR_PLUSPLUS.get(),
            0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.64", 1, ModBlocks.FURNACE_GENERATOR_PLUSPLUS.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.64", 2, ModBlocks.LAVA_GENERATOR_PLUSPLUS.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.64", 3, ModBlocks.ENDER_GENERATOR_PLUSPLUS.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.64", 4, ModBlocks.REDSTONE_GENERATOR_PLUSPLUS.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.64", 5, ModBlocks.FOOD_GENERATOR_PLUSPLUS.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.64", 6, ModBlocks.POTION_GENERATOR_PLUSPLUS.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.64", 7, ModBlocks.SOLAR_GENERATOR_PLUSPLUS.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.64", 8, ModBlocks.TNT_GENERATOR_PLUSPLUS.get(), 0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.64", 9, ModBlocks.PINK_GENERATOR_PLUSPLUS.get(), 0);
        BlockReplacementManager.addSimpleReplacement(
            "ExtraUtilities:generator.64",
            10,
            ModBlocks.HIGH_TEMPERATURE_FURNACE_GENERATOR.get(),
            0);
        BlockReplacementManager
            .addSimpleReplacement("ExtraUtilities:generator.64", 11, ModBlocks.NETHER_STAR_GENERATOR.get(), 0);
    }

    private static void ignoreMissingMappings() {
        // Items
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:angelRing");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:bedrockiumIngot");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:builderswand");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:creativebuilderswand");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:defoliageAxe");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:destructionpickaxe");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:divisionSigil");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:erosionShovel");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:ethericsword");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:glove");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:golden_bag");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:golden_lasso");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:heatingElement");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:lawSword");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:scanner");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:shears");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:sonar_goggles");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:temporalHoe");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:unstableingot");
        ItemStackReplacementManager.ignoreMissingMapping("ExtraUtilities:watering_can");
        // Blocks
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:angelBlock");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:block_bedrockium");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:budoff");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:chandelier");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:chestFull");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:chestMini");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:cobblestone_compressed");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:colorStoneBrick");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:colorWoodPlanks");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:color_blockCoal");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:color_blockLapis");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:color_blockRedstone");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:color_brick");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:color_hellsand");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:color_lightgem");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:color_obsidian");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:color_quartzBlock");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:color_redstoneLight");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:color_stone");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:color_stonebrick");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:conveyor");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:cursedearthside");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:curtains");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:dark_portal");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:decorativeBlock1");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:decorativeBlock2");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:drum");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:enderCollector");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:enderThermicPump");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:etherealglass");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:generator");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:generator.8");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:generator.64");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:greenscreen");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:magnumTorch");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:peaceful_table_top");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:plant/ender_lilly");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:pureLove");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:sound_muffler");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:spike_base");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:spike_base_diamond");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:spike_base_gold");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:spike_base_wood");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:timer");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:trading_post");
        BlockReplacementManager.ignoreMissingMapping("ExtraUtilities:trashcan");
    }
}
