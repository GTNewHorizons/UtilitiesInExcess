package com.fouristhenumber.utilitiesinexcess.common.recipe;

import static net.minecraft.item.Item.getItemFromBlock;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.api.EnderLocusRegistry;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColored;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockCompressed;
import com.fouristhenumber.utilitiesinexcess.config.RecipeConfig;
import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;

import cpw.mods.fml.common.registry.GameRegistry;

public class RecipeLoader {

    public static void run() {
        loadCompressedBlockRecipes();
        loadInversionRecipes();
        loadBedrockiumRecipes();
        loadEtherealGlassRecipes();
        loadWateringCanRecipes();
        loadLapisAetheriusRecipes();
        loadSpikeRecipes();
        loadGeneratorRecipes();
        loadEnderLocusRecipes();
        loadGlassRecipes();
        loadDecorativeBlocksRecipes();

        // Chandeliers
        if (RecipeConfig.enableChandelierRecipe) {
            addShapedRecipe(
                ModBlocks.CHANDELIER.newItemStack(1, 0),
                "gdg",
                "ttt",
                " t ",
                'g',
                Items.gold_ingot,
                'd',
                Items.diamond,
                't',
                Blocks.torch);
            addShapedRecipe(
                ModBlocks.CHANDELIER.newItemStack(1, 1),
                " e ",
                "ici",
                'c',
                ModBlocks.CHANDELIER.newItemStack(1, 0),
                'i',
                Items.iron_ingot,
                'e',
                Items.ender_pearl);
            addShapedRecipe(
                ModBlocks.CHANDELIER.newItemStack(1, 2),
                " t ",
                "rcr",
                'c',
                ModBlocks.CHANDELIER.newItemStack(1, 0),
                'r',
                Items.redstone,
                't',
                Blocks.redstone_torch);
            addShapedRecipe(
                ModBlocks.CHANDELIER.newItemStack(1, 3),
                " o ",
                "scs",
                'c',
                ModBlocks.CHANDELIER.newItemStack(1, 0),
                's',
                Blocks.soul_sand,
                'o',
                Blocks.obsidian);
        }

        // Collector
        if (RecipeConfig.enableCollectorRecipe) addShapedRecipe(
            ModBlocks.COLLECTOR,
            "eie",
            " i ",
            "ooo",
            'e',
            Items.ender_pearl,
            'i',
            ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5),
            'o',
            Blocks.obsidian);

        // Pacifist's Bench
        if (RecipeConfig.enablePacifistsBenchRecipe) addShapedRecipe(
            ModBlocks.PACIFISTS_BENCH,
            "ewe",
            "wpw",
            "ewe",
            'e',
            Items.emerald,
            'p',
            Items.ender_pearl,
            'w',
            new ItemStack(Blocks.planks, 1, OreDictionary.WILDCARD_VALUE));

        // Smart Pump
        if (RecipeConfig.enableSmartPumpRecipe) {
            addShapedRecipe(
                ModBlocks.SMART_PUMP,
                "odo",
                "lew",
                "opo",
                'o',
                ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5),
                'd',
                Items.diamond,
                'l',
                Items.lava_bucket,
                'e',
                Items.ender_eye,
                'w',
                Items.water_bucket,
                'p',
                Items.iron_pickaxe);

            addShapedRecipe(
                ModBlocks.SMART_PUMP,
                "odo",
                "wel",
                "opo",
                'o',
                ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5),
                'd',
                Items.diamond,
                'l',
                Items.lava_bucket,
                'e',
                Items.ender_eye,
                'w',
                Items.water_bucket,
                'p',
                Items.iron_pickaxe);
        }

        // Block Analyzer
        if (RecipeConfig.enableBlockAnalyzerRecipe) addShapedRecipe(
            ModItems.BLOCK_ANALYZER,
            "iii",
            "eri",
            "iii",
            'i',
            Items.iron_ingot,
            'r',
            Items.redstone,
            'e',
            Items.ender_eye);

        // X-Ray Glasses
        if (RecipeConfig.enableXRayGlassesRecipe) addShapedRecipe(
            ModItems.XRAY_GLASSES,
            "iii",
            "eie",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            'e',
            Items.ender_eye);

        // Floating Block
        if (RecipeConfig.enableFloatingBlockRecipe) addShapedRecipe(
            ModBlocks.FLOATING_BLOCK,
            " g ",
            "fof",
            'g',
            Items.gold_ingot,
            'f',
            Items.feather,
            'o',
            Blocks.obsidian);

        // Heavenly Rings
        if (RecipeConfig.enableHeavenlyRingRecipes) {
            addShapedRecipe(
                ModItems.HEAVENLY_RING_FEATHER,
                "bgb",
                "gsg",
                "igi",
                'b',
                Items.feather,
                'g',
                Items.gold_ingot,
                's',
                Items.nether_star,
                'i',
                ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE));
            addShapedRecipe(
                ModItems.HEAVENLY_RING_DRAGON,
                "bgb",
                "gsg",
                "igi",
                'b',
                Items.leather,
                'g',
                Items.gold_ingot,
                's',
                Items.nether_star,
                'i',
                ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE));
            addShapedRecipe(
                ModItems.HEAVENLY_RING_FAIRY,
                "pgd",
                "gsg",
                "igi",
                'p',
                new ItemStack(Items.dye, 1, 5),
                'd',
                new ItemStack(Items.dye, 1, 9),
                'g',
                Items.gold_ingot,
                's',
                Items.nether_star,
                'i',
                ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE));
            addShapedRecipe(
                ModItems.HEAVENLY_RING_METAL,
                "bgb",
                "gsg",
                "igi",
                'b',
                Items.gold_nugget,
                'g',
                Items.gold_ingot,
                's',
                Items.nether_star,
                'i',
                ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE));
            addShapedRecipe(
                ModItems.HEAVENLY_RING_MAGIC,
                "bgb",
                "gsg",
                "igi",
                'b',
                Blocks.glass,
                'g',
                Items.gold_ingot,
                's',
                Items.nether_star,
                'i',
                ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE));
        }

        // Redstone Clock
        if (RecipeConfig.enableRedstoneClockRecipe) addShapedRecipe(
            ModBlocks.REDSTONE_CLOCK,
            "rsr",
            "sts",
            "rsr",
            'r',
            Items.redstone,
            's',
            Blocks.stone,
            't',
            Blocks.redstone_torch);

        // Block Update Detector
        if (RecipeConfig.enableBlockUpdateDetectorRecipe) addShapedRecipe(
            ModBlocks.BLOCK_UPDATE_DETECTOR,
            "srs",
            "sps",
            "sts",
            'r',
            Items.redstone,
            's',
            Blocks.stone,
            't',
            Blocks.redstone_torch,
            'p',
            Blocks.sticky_piston);

        // Advanced Block Update Detector
        if (RecipeConfig.enableAdvancedBlockUpdateDetectorRecipe) addShapedRecipe(
            ModBlocks.ADVANCED_BLOCK_UPDATE_DETECTOR,
            "srs",
            "rbr",
            "srs",
            's',
            Blocks.stone,
            'r',
            Blocks.redstone_block,
            'b',
            ModBlocks.BLOCK_UPDATE_DETECTOR);

        // Trash Can (Items)
        if (RecipeConfig.enableTrashCanItemRecipe) addShapedRecipe(
            ModBlocks.TRASH_CAN_ITEM,
            "sss",
            "c#c",
            "ccc",
            's',
            Blocks.stone,
            'c',
            Blocks.cobblestone,
            '#',
            Blocks.chest);

        // Trash Can (Fluids)
        if (RecipeConfig.enableTrashCanFluidRecipe)
            addShapelessRecipe(ModBlocks.TRASH_CAN_FLUID, ModBlocks.TRASH_CAN_ITEM, Items.bucket);

        // Trash Can (Energy)
        if (RecipeConfig.enableTrashCanEnergyRecipe) addShapelessRecipe(
            ModBlocks.TRASH_CAN_ENERGY,
            ModBlocks.TRASH_CAN_ITEM,
            Items.redstone,
            Items.redstone,
            Items.gold_ingot,
            Items.gold_ingot);

        // Drum
        if (RecipeConfig.enableDrumRecipe) addShapedRecipe(
            ModBlocks.DRUM,
            "ipi",
            "ici",
            "ipi",
            'i',
            Items.iron_ingot,
            'p',
            Blocks.heavy_weighted_pressure_plate,
            'c',
            Items.cauldron);

        // Bedrockium Drum
        if (RecipeConfig.enableBedrockiumDrumRecipe) addShapedRecipe(
            ModBlocks.BEDROCKIUM_DRUM,
            "ipi",
            "ici",
            "ipi",
            'i',
            ModItems.BEDROCKIUM_INGOT,
            'p',
            Blocks.light_weighted_pressure_plate,
            'c',
            Items.cauldron);

        // Sound Muffler
        if (RecipeConfig.enableSoundMufflerRecipe) addShapedRecipe(
            ModBlocks.SOUND_MUFFLER,
            "www",
            "wjw",
            "www",
            'w',
            new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE),
            'j',
            Blocks.noteblock);

        // Rain Muffler
        if (RecipeConfig.enableRainMufflerRecipe) addShapedRecipe(
            ModBlocks.RAIN_MUFFLER,
            "www",
            "wbw",
            "www",
            'w',
            new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE),
            'b',
            Items.water_bucket);

        // Fire Battery
        if (RecipeConfig.enableFireBatteryRecipe) addShapedRecipe(
            ModItems.FIRE_BATTERY,
            "ppp",
            "p p",
            "prp",
            'p',
            Blocks.heavy_weighted_pressure_plate,
            'r',
            Items.redstone);

        // Underworld Portal
        if (RecipeConfig.enableUnderworldPortalRecipe) addShapedRecipe(
            ModBlocks.UNDERWORLD_PORTAL,
            "qiq",
            "iui",
            "qiq",
            'q',
            ModBlocks.COMPRESSED_COBBLESTONE.newItemStack(1, 3),
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            'u',
            ModBlocks.COMPRESSED_COBBLESTONE.newItemStack(1, 4));

        // Blackout Curtains
        if (RecipeConfig.enableBlackoutCurtainsRecipe) addShapedRecipe(
            ModBlocks.BLACKOUT_CURTAINS.newItemStack(12),
            "ww",
            "ww",
            "ww",
            'w',
            new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE));

        // Conveyor
        if (RecipeConfig.enableConveyorRecipe) addShapedRecipe(
            ModBlocks.CONVEYOR.newItemStack(8),
            "rrr",
            "isi",
            "rrr",
            'r',
            Blocks.rail,
            'i',
            Items.iron_ingot,
            's',
            Items.redstone);

        // Magic Wood
        if (RecipeConfig.enableMagicWoodRecipe) addShapedRecipe(
            ModBlocks.MAGIC_WOOD,
            "gbg",
            "bsb",
            "gbg",
            'g',
            Items.gold_ingot,
            'b',
            Items.enchanted_book,
            's',
            Blocks.bookshelf);

        // Endspark
        if (RecipeConfig.enableEndsparkRecipe) addShapedRecipe(
            ModBlocks.ENDSPARK,
            "wow",
            "oeo",
            "wow",
            'w',
            ModBlocks.MAGIC_WOOD,
            'o',
            ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5),
            'e',
            Items.ender_eye);

        // Pure Love
        if (RecipeConfig.enablePureLoveRecipe) addShapedRecipe(
            ModBlocks.PURE_LOVE,
            "rcr",
            "rir",
            "wrs",
            'r',
            new ItemStack(Blocks.wool, 1, 14),
            'c',
            Items.carrot,
            'w',
            Items.wheat,
            's',
            Items.wheat_seeds,
            'i',
            ModBlocks.INVERTED_BLOCK);

        // Marginally Maximized Chest
        if (RecipeConfig.enableMarginallyMaximisedChestRecipe) addShapedRecipe(
            ModBlocks.MARGINALLY_MAXIMISED_CHEST,
            "sss",
            "scs",
            "sss",
            's',
            Items.stick,
            'c',
            Blocks.chest);

        // Significantly Shrunk Chest
        if (RecipeConfig.enableSignificantlyShrunkChestRecipe) {
            addShapelessRecipe(new DisableableItemStack(ModBlocks.SIGNIFICANTLY_SHRUNK_CHEST, 9), Blocks.chest);
            addShapelessRecipe(
                new DisableableItemStack(ModBlocks.SIGNIFICANTLY_SHRUNK_CHEST, 9),
                ModBlocks.MARGINALLY_MAXIMISED_CHEST);
        }

        // Radically Reduced Chest
        if (RecipeConfig.enableRadicallyReducedChestRecipe) addShapelessRecipe(
            new DisableableItemStack(ModBlocks.RADICALLY_REDUCED_CHEST, 9),
            ModBlocks.SIGNIFICANTLY_SHRUNK_CHEST);

        // Mob Jar
        if (RecipeConfig.enableMobJarRecipe) addShapedRecipe(
            ModItems.MOB_JAR,
            "geg",
            "gbg",
            "ggg",
            'g',
            Items.gold_nugget,
            'e',
            Items.ender_eye,
            'b',
            Items.glass_bottle);

        // Golden Bag of Holding
        if (RecipeConfig.enableGoldenBagRecipe) addShapedRecipe(
            ModItems.GOLDEN_BAG,
            "wdw",
            "gcg",
            "wbw",
            'g',
            Items.gold_ingot,
            'c',
            Blocks.chest,
            'w',
            new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE),
            'd',
            Items.diamond,
            'b',
            Blocks.gold_block);

        // Temporal Gate
        if (RecipeConfig.enableEndOfTimePortalRecipe) addShapedRecipe(
            ModBlocks.END_OF_TIME_PORTAL,
            "qeq",
            "ece",
            "qeq",
            'q',
            ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 2),
            'e',
            ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 11),
            'c',
            Items.clock);

        // Trading Post
        if (RecipeConfig.enableTradingPostRecipe) addShapedRecipe(
            ModBlocks.TRADING_POST,
            "pep",
            "pjp",
            "ppp",
            'p',
            Blocks.planks,
            'e',
            Blocks.emerald_block,
            'j',
            Blocks.jukebox);

        addShapedRecipe(
            ModBlocks.TRUE_GREENSCREEN,
            "ege",
            "gig",
            "ege",
            'e',
            ModBlocks.ETHEREAL_GLASS.get(),
            'g',
            ModBlocks.LAPIS_AETHERIUS.newItemStack(1, 5),
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE));

        // Filing Cabinets
        if (RecipeConfig.enableFilingCabinetRecipes) {
            addShapedRecipe(
                ModBlocks.FILING_CABINET.newItemStack(1, 0),
                "ici",
                "ici",
                "ici",
                'i',
                Items.iron_ingot,
                'c',
                Blocks.chest);

            addShapedRecipe(
                ModBlocks.FILING_CABINET.newItemStack(1, 1),
                "mcm",
                "mcm",
                "mcm",
                'm',
                ModBlocks.MAGIC_WOOD,
                'c',
                ModBlocks.FILING_CABINET.newItemStack(1, 0));

            addShapedRecipe(
                ModBlocks.FILING_CABINET.newItemStack(1, 2),
                "ici",
                "ici",
                "ici",
                'i',
                ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
                'c',
                ModBlocks.FILING_CABINET.newItemStack(1, 1));
        }

        // Filing Cabinet Capacity Upgrade
        if (RecipeConfig.enableFilingCabinetUpgradeRecipe) addShapedRecipe(
            ModItems.CAPACITY_UPGRADE,
            " d ",
            "gcg",
            " d ",
            'g',
            Items.gold_ingot,
            'c',
            ModBlocks.FILING_CABINET.newItemStack(1, 0),
            'd',
            Items.diamond);

        // Void Quarry
        if (RecipeConfig.enableVoidQuarryRecipe) addShapedRecipe(
            ModBlocks.VOID_QUARRY,
            "oso",
            "ede",
            "pap",
            'o',
            ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5),
            's',
            Blocks.sapling,
            'e',
            ModBlocks.ENDSPARK,
            'd',
            ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 0),
            'p',
            ModBlocks.SMART_PUMP,
            'a',
            Items.diamond_pickaxe);

        // Paint Roller
        if (RecipeConfig.enablePaintRollerRecipe) addShapedRecipe(
            ModItems.PAINT_ROLLER,
            "ws",
            " s",
            "s ",
            's',
            Items.stick,
            'w',
            new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE));
    }

    private static void loadGeneratorRecipes() {
        if (!RecipeConfig.enableGeneratorRecipes) return;

        // Low-Temp Furnace Generator
        addShapedRecipe(
            ModBlocks.LOW_TEMPERATURE_FURNACE_GENERATOR,
            "ccc",
            "ipi",
            "rfr",
            'c',
            Blocks.cobblestone,
            'i',
            Items.iron_ingot,
            'p',
            Blocks.piston,
            'r',
            Items.redstone,
            'f',
            Blocks.furnace);

        // Furnace Generator
        addShapedRecipe(
            ModBlocks.FURNACE_GENERATOR,
            "iii",
            "ipi",
            "rfr",
            'i',
            Items.iron_ingot,
            'p',
            Blocks.iron_block,
            'r',
            Items.redstone,
            'f',
            Blocks.furnace);

        // High-Temp Furnace Generator
        addShapedRecipe(
            ModBlocks.HIGH_TEMPERATURE_FURNACE_GENERATOR,
            "iii",
            "ipi",
            "rfr",
            'i',
            Items.iron_ingot,
            'p',
            ModBlocks.FURNACE_GENERATOR,
            'r',
            Items.redstone,
            'f',
            Blocks.furnace);

        // Lava Generator
        addShapedRecipe(
            ModBlocks.LAVA_GENERATOR,
            "iii",
            "ipi",
            "rfr",
            'i',
            Items.gold_ingot,
            'p',
            Blocks.iron_block,
            'r',
            Items.redstone,
            'f',
            Blocks.furnace);

        // Ender Generator
        addShapedRecipe(
            ModBlocks.ENDER_GENERATOR,
            "iii",
            "epe",
            "rfr",
            'i',
            Items.ender_pearl,
            'e',
            Items.ender_eye,
            'p',
            Blocks.iron_block,
            'r',
            Items.redstone,
            'f',
            Blocks.furnace);

        // Redstone Generator
        addShapedRecipe(
            ModBlocks.REDSTONE_GENERATOR,
            "iii",
            "ipi",
            "rfr",
            'i',
            Blocks.redstone_block,
            'p',
            ModBlocks.LAVA_GENERATOR,
            'r',
            Items.redstone,
            'f',
            Blocks.furnace);

        // Food Generator
        addShapedRecipe(
            ModBlocks.FOOD_GENERATOR,
            "iii",
            "ipi",
            "rfr",
            'i',
            Items.iron_ingot,
            'p',
            ModBlocks.LOW_TEMPERATURE_FURNACE_GENERATOR,
            'r',
            Items.redstone,
            'f',
            Blocks.furnace);

        // Potion Generator
        addShapedRecipe(
            ModBlocks.POTION_GENERATOR,
            "iii",
            "ipi",
            "rfr",
            'i',
            Blocks.obsidian,
            'p',
            Blocks.enchanting_table,
            'r',
            Items.redstone,
            'f',
            Blocks.furnace);

        // Solar Generator
        addShapedRecipe(
            ModBlocks.SOLAR_GENERATOR,
            "iqi",
            "qpq",
            "rfr",
            'i',
            new ItemStack(Items.dye, 1, 4),
            'q',
            Items.quartz,
            'p',
            Blocks.diamond_block,
            'r',
            Items.redstone,
            'f',
            Blocks.furnace);

        // TNT Generator
        addShapedRecipe(
            ModBlocks.TNT_GENERATOR,
            "iii",
            "ipi",
            "rfr",
            'i',
            Blocks.tnt,
            'p',
            Blocks.iron_block,
            'r',
            Items.redstone,
            'f',
            Blocks.furnace);

        // Pink Generator
        addShapedRecipe(
            ModBlocks.PINK_GENERATOR,
            "iii",
            "ipi",
            "rfr",
            'i',
            new ItemStack(Blocks.wool, 1, 6),
            'p',
            ModBlocks.LOW_TEMPERATURE_FURNACE_GENERATOR,
            'r',
            Items.redstone,
            'f',
            Blocks.furnace);

        // Nether Star Generator
        addShapedRecipe(
            ModBlocks.NETHER_STAR_GENERATOR,
            "sis",
            "sps",
            "rfr",
            'i',
            Items.nether_star,
            's',
            new ItemStack(Items.skull, 1, 1),
            'p',
            ModBlocks.INVERTED_BLOCK,
            'r',
            Items.redstone,
            'f',
            Blocks.furnace);
    }

    private static void loadSpikeRecipes() {
        if (RecipeConfig.enableSpikeRecipes) {
            addShapedRecipe(
                ModBlocks.SPIKE_WOOD.newItemStack(4),
                " a ",
                "aba",
                "bcb",
                'a',
                Items.wooden_sword,
                'b',
                Blocks.planks,
                'c',
                Blocks.log);

            addShapedRecipe(
                ModBlocks.SPIKE_IRON.newItemStack(4),
                " a ",
                "aba",
                "bcb",
                'a',
                Items.iron_sword,
                'b',
                Items.iron_ingot,
                'c',
                Blocks.iron_block);

            addShapedRecipe(
                ModBlocks.SPIKE_GOLD.newItemStack(4),
                " a ",
                "aba",
                "bcb",
                'a',
                Items.golden_sword,
                'b',
                ModBlocks.MAGIC_WOOD,
                'c',
                Blocks.gold_block);

            addShapedRecipe(
                ModBlocks.SPIKE_DIAMOND.newItemStack(4),
                " a ",
                "aba",
                "bcb",
                'a',
                Items.diamond_sword,
                'b',
                ModBlocks.SPIKE_GOLD,
                'c',
                Blocks.diamond_block);
        }
    }

    private static void loadLapisAetheriusRecipes() {
        if (!RecipeConfig.enableLapisAetheriusRecipes) return;

        for (int i = 0; i < 16; i++) {
            addShapedRecipe(
                new DisableableItemStack(ModBlocks.LAPIS_AETHERIUS, 4, i),
                "sds",
                "did",
                "sds",
                's',
                Blocks.stonebrick,
                'i',
                ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
                'd',
                new ItemStack(Items.dye, 1, 15 - i));
        }
    }

    private static void loadWateringCanRecipes() {
        // Basic Watering Can
        if (RecipeConfig.enableWateringCanBasicRecipe) addShapedRecipe(
            ModItems.WATERING_CAN_BASIC,
            "im ",
            "ibi",
            " i ",
            'i',
            Items.iron_ingot,
            'b',
            Items.bowl,
            'm',
            new ItemStack(Items.dye, 1, 15));

        // Advanced Watering Can
        if (RecipeConfig.enableWateringCanAdvancedRecipe) addShapedRecipe(
            ModItems.WATERING_CAN_ADVANCED,
            "im ",
            "ibi",
            " i ",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            'b',
            Items.bowl,
            'm',
            new ItemStack(Items.dye, 1, 15));

        // Elite Watering Can
        if (RecipeConfig.enableWateringCanEliteRecipe) addShapedRecipe(
            ModItems.WATERING_CAN_ELITE,
            "im ",
            "ibi",
            " i ",
            'i',
            ModItems.BEDROCKIUM_INGOT,
            'b',
            Items.bowl,
            'm',
            new ItemStack(Items.dye, 1, 15));
    }

    private static void loadDecorativeBlocksRecipes() {
        if (!RecipeConfig.enableDecorativeBlockRecipes) return;

        // Ender-Infused Obsidian
        addShapedRecipe(
            new DisableableItemStack(ModBlocks.DECORATIVE_BLOCKS, 4, 5),
            " o ",
            "oeo",
            " o ",
            'o',
            Blocks.obsidian,
            'e',
            Items.ender_pearl);
        // Burnt Quartz
        addFurnaceRecipe(getItemFromBlock(Blocks.quartz_block), ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 2), 0.7F);
        // Diamond-Etched Computational Matrix
        addShapedRecipe(
            new DisableableItemStack(ModBlocks.DECORATIVE_BLOCKS, 1, 0),
            "bdb",
            "ded",
            "bdb",
            'b',
            ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 2),
            'd',
            Items.diamond,
            'e',
            ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5));
        // Sand-Infused Endstone
        addShapedRecipe(
            new DisableableItemStack(ModBlocks.DECORATIVE_BLOCKS, 4, 1),
            "es",
            "se",
            'e',
            Blocks.end_stone,
            's',
            Blocks.sandstone);
        // Sandy Glass
        addShapedRecipe(
            new DisableableItemStack(ModBlocks.DECORATIVE_BLOCKS, 4, 3),
            "sg",
            "gs",
            's',
            Blocks.sand,
            'g',
            Blocks.glass);
        // Frosted Stone
        addShapedRecipe(
            new DisableableItemStack(ModBlocks.DECORATIVE_BLOCKS, 5, 4),
            " i ",
            "isi",
            " i ",
            'i',
            Blocks.ice,
            's',
            Blocks.stone);
        // Gravel Bricks
        addShapedRecipe(new DisableableItemStack(ModBlocks.DECORATIVE_BLOCKS, 1, 6), "gg", "gg", 'g', Blocks.gravel);
        // Edged Stone Bricks
        addShapedRecipe(
            new DisableableItemStack(ModBlocks.DECORATIVE_BLOCKS, 9, 7),
            "sbs",
            "bbb",
            "sbs",
            's',
            Blocks.stone,
            'b',
            new ItemStack(Blocks.stonebrick));
        // Border Stone
        addShapedRecipe(
            new DisableableItemStack(ModBlocks.DECORATIVE_BLOCKS, 4, 8),
            "bb",
            "bb",
            'b',
            ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 7));
        // Border Stone (Alternate)
        addShapedRecipe(
            new DisableableItemStack(ModBlocks.DECORATIVE_BLOCKS, 4, 9),
            "bb",
            "bb",
            'b',
            ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 8));
        // Gravel Road
        addShapedRecipe(
            new DisableableItemStack(ModBlocks.DECORATIVE_BLOCKS, 8, 10),
            "sgs",
            "ggg",
            "sgs",
            's',
            new ItemStack(Blocks.stone_slab, 1, 5),
            'g',
            ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 6));
        // Carved 'Eminence' Stone
        addShapedRecipe(
            new DisableableItemStack(ModBlocks.DECORATIVE_BLOCKS, 4, 11),
            "mpe",
            "ss ",
            "ss ",
            'm',
            new ItemStack(Items.dye, 1, 13),
            'p',
            new ItemStack(Items.dye, 1, 5),
            'e',
            Items.ender_pearl,
            's',
            Blocks.stone);
    }

    private static void loadEtherealGlassRecipes() {
        if (!RecipeConfig.enableEtherealGlassRecipes) return;

        // Ethereal Glass
        addShapedRecipe(
            new DisableableItemStack(ModBlocks.ETHEREAL_GLASS, 1, 0),
            "ggg",
            "gig",
            "ggg",
            'g',
            Blocks.glass,
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE));

        // Ineffable Glass
        addShapedRecipe(
            new DisableableItemStack(ModBlocks.ETHEREAL_GLASS, 1, 1),
            "ggg",
            "gig",
            "ggg",
            'g',
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 0),
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE));

        // Dark Ethereal Glass
        addShapedRecipe(
            new DisableableItemStack(ModBlocks.ETHEREAL_GLASS, 1, 2),
            "ggg",
            "gig",
            "ggg",
            'g',
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 10),
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE));

        // Ethereal Glass (Inverted)
        addShapelessRecipe(
            new DisableableItemStack(ModBlocks.ETHEREAL_GLASS, 1, 3),
            new DisableableItemStack(ModBlocks.ETHEREAL_GLASS, 1, 0),
            Blocks.redstone_torch);

        // Ineffable Glass (Inverted)
        addShapelessRecipe(
            new DisableableItemStack(ModBlocks.ETHEREAL_GLASS, 1, 4),
            new DisableableItemStack(ModBlocks.ETHEREAL_GLASS, 1, 1),
            Blocks.redstone_torch);

        // Dark Ethereal Glass (Inverted)
        addShapelessRecipe(
            new DisableableItemStack(ModBlocks.ETHEREAL_GLASS, 1, 5),
            new DisableableItemStack(ModBlocks.ETHEREAL_GLASS, 1, 2),
            Blocks.redstone_torch);
    }

    private static void loadBedrockiumRecipes() {
        // Bedrockium Ingot
        if (RecipeConfig.enableBedrockiumIngotRecipe) addShapedRecipe(
            ModItems.BEDROCKIUM_INGOT,
            "tqt",
            "qdq",
            "tqt",
            't',
            new DisableableItemStack(ModBlocks.COMPRESSED_COBBLESTONE, 1, 2),
            'q',
            new DisableableItemStack(ModBlocks.COMPRESSED_COBBLESTONE, 1, 3),
            'd',
            Blocks.diamond_block);

        // Bedrockium Ingot -> Block
        if (RecipeConfig.enableBedrockiumBlockRecipe) {
            addShapedRecipe(ModBlocks.BEDROCKIUM_BLOCK, "iii", "iii", "iii", 'i', ModItems.BEDROCKIUM_INGOT);
            // Bedrockium Block -> Ingot
            addShapedRecipe(
                new DisableableItemStack(ModItems.BEDROCKIUM_INGOT, 9),
                "b",
                'b',
                ModBlocks.BEDROCKIUM_BLOCK);
            // Direct Block Recipe
            addFurnaceRecipe(
                new DisableableItemStack(ModBlocks.COMPRESSED_COBBLESTONE, 1, 7),
                ModBlocks.BEDROCKIUM_BLOCK,
                1F);
        }
    }

    private static void loadGlassRecipes() {
        if (!RecipeConfig.enableDecorativeGlassRecipes) return;

        // Smooth Glass
        addFurnaceRecipe(
            ModBlocks.DECORATIVE_BLOCKS.newItemStack(8, 3),
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 0),
            0.5F);

        // Rimmed Glass
        addShapedRecipe(
            ModBlocks.DECORATIVE_GLASS.newItemStack(8, 1),
            "ggg",
            "g g",
            "ggg",
            'g',
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 0));

        // Bricked Glass
        addShapedRecipe(
            ModBlocks.DECORATIVE_GLASS.newItemStack(4, 2),
            "gg",
            "gg",
            'g',
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 0));

        // Creepy Glass
        addShapelessRecipe(
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 3),
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 0),
            Items.gunpowder);

        // Gilded Glass
        addShapedRecipe(
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 4),
            "nnn",
            "ngn",
            "nnn",
            'g',
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 0),
            'n',
            Items.gold_nugget);

        // Obsidian Glass
        addShapedRecipe(
            ModBlocks.DECORATIVE_GLASS.newItemStack(4, 5),
            "gog",
            "o o",
            "gog",
            'g',
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 0),
            'o',
            Blocks.obsidian);

        // Vortex Glass
        addShapedRecipe(
            ModBlocks.DECORATIVE_GLASS.newItemStack(5, 6),
            " g ",
            "ggg",
            " g ",
            'g',
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 0));

        // Glowing Glass
        addShapedRecipe(
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 7),
            " d ",
            "dgd",
            " d ",
            'g',
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 0),
            'd',
            Items.glowstone_dust);

        // Beloved Glass
        addShapedRecipe(
            ModBlocks.DECORATIVE_GLASS.newItemStack(6, 8),
            "gpg",
            "ggg",
            " g ",
            'g',
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 0),
            'p',
            new ItemStack(Items.dye, 1, 9));

        // Tiled Glass
        addShapelessRecipe(
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 9),
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 0));

        // Dark Glass
        addShapedRecipe(
            ModBlocks.DECORATIVE_GLASS.newItemStack(5, 10),
            "gcg",
            "cgc",
            "gcg",
            'g',
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 0),
            'c',
            ModBlocks.BLACKOUT_CURTAINS);

        // Reinforced Glass
        addShapedRecipe(
            ModBlocks.DECORATIVE_GLASS.newItemStack(4, 11),
            "gog",
            "o o",
            "gog",
            'g',
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 10),
            'o',
            Blocks.obsidian);

        // Latticed Glass
        addShapedRecipe(
            ModBlocks.DECORATIVE_GLASS.newItemStack(4, 12),
            "gg",
            "gg",
            'g',
            ModBlocks.DECORATIVE_GLASS.newItemStack(1, 2));
    }

    // DO NOT call from inside RecipeLoader, must be called after config colored blocks get initialized
    public static void loadColoredBlockRecipes() {
        if (!BlockConfig.coloredBlocks.enableColoredBlocks || !RecipeConfig.enableColoredBlockRecipes) return;

        if (BlockColored.allowDyingBlocks()) {
            for (BlockColored block : BlockColored.COLORED_BLOCKS) {
                loadDyeableColoredBlockRecipe(block);
            }
        } else {
            ItemStack[] dyes = new ItemStack[16];
            for (int i = 0; i < 16; ++i) {
                dyes[i] = new ItemStack(Items.dye, 1, i);
            }

            for (BlockColored block : BlockColored.COLORED_BLOCKS) {
                loadColoredBlockRecipe(block, dyes);
            }
        }
    }

    private static void loadDyeableColoredBlockRecipe(BlockColored block) {
        ItemStack paintRoller = new ItemStack(ModItems.PAINT_ROLLER.get());

        ItemStack any8 = new ItemStack(block, 8, OreDictionary.WILDCARD_VALUE);
        // To base using water
        GameRegistry.addShapedRecipe(
            new ItemStack(block.getBase(), 8, block.ignoreBaseMeta() ? OreDictionary.WILDCARD_VALUE : 0),
            "bbb",
            "bdb",
            "bbb",
            'b',
            any8,
            'd',
            new ItemStack(Items.water_bucket));

        ItemStack anyDyed = new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack baseItem = new ItemStack(
            Item.getItemFromBlock(block.getBase()),
            1,
            block.ignoreBaseMeta() ? OreDictionary.WILDCARD_VALUE : 0);
        // From base to dyed using roller
        GameRegistry.addRecipe(
            new RecipePaintRollerToPaint(
                3,
                3,
                new ItemStack[] { baseItem, baseItem, baseItem, baseItem, paintRoller, baseItem, baseItem, baseItem,
                    baseItem },
                any8));

        // From dyed to dyed using roller
        GameRegistry.addRecipe(
            new RecipePaintRollerToPaint(
                3,
                3,
                new ItemStack[] { anyDyed, anyDyed, anyDyed, anyDyed, paintRoller, anyDyed, anyDyed, anyDyed, anyDyed },
                any8));
    }

    private static void loadColoredBlockRecipe(BlockColored block, ItemStack[] dyes) {
        ItemStack paintRoller = new ItemStack(ModItems.PAINT_ROLLER.get());

        ItemStack water = new ItemStack(Items.water_bucket);
        for (int i = 0; i < 16; ++i) {
            addShapedRecipe(
                new ItemStack(block, 7, i),
                "bbb",
                "bdb",
                "bpb",
                'b',
                block.getBase(),
                'd',
                dyes[15 - i],
                'p',
                paintRoller);
        }

        addShapedRecipe(
            new ItemStack(block.getBase(), 8),
            "bbb",
            "bdb",
            "bbb",
            'b',
            new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE),
            'd',
            water);
    }

    private static void loadInversionRecipes() {
        // Inverted Ingot (unstable)
        // Has to use a special recipe adder to check for vanilla crafting table
        if (ModItems.INVERSION_SIGIL_ACTIVE.isEnabled() && ModItems.INVERTED_INGOT.isEnabled()
            && RecipeConfig.enableInvertedIngotRecipe) {
            GameRegistry.addRecipe(
                new RecipeInvertedIngot(
                    1,
                    3,
                    new ItemStack[] { new ItemStack(Items.iron_ingot), ModItems.INVERSION_SIGIL_ACTIVE.newItemStack(),
                        new ItemStack(Items.diamond), },
                    ModItems.INVERTED_INGOT.newItemStack()));
        }

        if (RecipeConfig.enableInvertedIngotQuasiNormalizedRecipe) addShapedRecipe(
            ModItems.INVERTED_INGOT.newItemStack(1, 2),
            "i",
            "s",
            "d",
            'i',
            Items.iron_ingot,
            's',
            ModItems.PSEUDO_REVERSION_SIGIL,
            'd',
            Items.diamond);

        // Inverted Nugget
        if (RecipeConfig.enableInvertedNuggetRecipe) addShapedRecipe(
            ModItems.INVERTED_NUGGET,
            "g",
            "s",
            "d",
            'g',
            Items.gold_nugget,
            's',
            ModItems.INVERSION_SIGIL_ACTIVE,
            'd',
            Items.diamond);

        // Inverted Ingot (stable, from nuggets)
        if (RecipeConfig.enableInvertedIngotFromNuggetsRecipe) addShapedRecipe(
            ModItems.INVERTED_INGOT.newItemStack(1, 1),
            "nnn",
            "nnn",
            "nnn",
            'n',
            ModItems.INVERTED_NUGGET);

        // Sating Axe
        if (RecipeConfig.enableSatingAxeRecipe) addShapedRecipe(
            ModItems.SATING_AXE,
            "ii",
            "is",
            " s",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            's',
            Blocks.obsidian);

        // Destruction Pickaxe
        if (RecipeConfig.enableDestructionPickaxeRecipe) addShapedRecipe(
            ModItems.DESTRUCTION_PICKAXE,
            "iii",
            " s ",
            " s ",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            's',
            Blocks.obsidian);

        // Retrograde Hoe
        if (RecipeConfig.enableRetrogradeHoeRecipe) addShapedRecipe(
            ModItems.RETROGRADE_HOE,
            "ii",
            " s",
            " s",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            's',
            Blocks.obsidian);

        // Anti-Gravity Shovel
        if (RecipeConfig.enableAntiGravityShovelRecipe) addShapedRecipe(
            ModItems.ANTI_GRAVITY_SHOVEL,
            "i",
            "s",
            "s",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            's',
            Blocks.obsidian);

        // Liminal Sword
        if (RecipeConfig.enableLiminalSwordRecipe) addShapedRecipe(
            ModItems.LIMINAL_SWORD,
            "i",
            "i",
            "s",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            's',
            Blocks.obsidian);

        // Chunchunmaru
        if (RecipeConfig.enableChunchunmaruRecipe) addShapedRecipe(
            ModItems.CHUNCHUNMARU,
            "i",
            "i",
            "s",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, 2),
            's',
            ModItems.LIMINAL_SWORD);

        // Precision Shears
        if (RecipeConfig.enablePrecisionShearsRecipe) addShapedRecipe(
            ModItems.PRECISION_SHEARS,
            "fi",
            "if",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            'f',
            ModBlocks.FLOATING_BLOCK);

        // Builder's Wand
        if (RecipeConfig.enableBuildersWandRecipe) addShapedRecipe(
            ModItems.BUILDERS_WAND,
            " i",
            "s ",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            's',
            Blocks.obsidian);

        // Super Builder's Wand
        if (RecipeConfig.enableSuperBuildersWandRecipe) addShapedRecipe(
            ModItems.SUPER_BUILDERS_WAND,
            " i",
            "s ",
            'i',
            ModItems.BEDROCKIUM_INGOT,
            's',
            ModItems.BUILDERS_WAND);

        // Inverted Ingot -> Block
        if (RecipeConfig.enableInvertedBlockRecipe) addShapedRecipe(
            ModBlocks.INVERTED_BLOCK,
            "iii",
            "iii",
            "iii",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE));

        // Glove
        if (ModItems.GLOVE.isEnabled() && RecipeConfig.enableGloveRecipe) {
            GameRegistry.addRecipe(
                new RecipeGlove(
                    2,
                    2,
                    new ItemStack[] { new ItemStack(Items.string),
                        new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE),
                        new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.string) },
                    ModItems.GLOVE.newItemStack()));
        }
    }

    private static void loadEnderLocusRecipes() {
        if (!ModBlocks.ENDER_LOCUS.isEnabled()) return;

        // Ender Locus
        if (RecipeConfig.enableEnderLocusRecipe) addShapedRecipe(
            ModBlocks.ENDER_LOCUS,
            "ece",
            "omo",
            "ooo",
            'e',
            Items.ender_eye,
            'c',
            Blocks.crafting_table,
            'o',
            ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5),
            'm',
            ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 0));

        // Convergence Crystal
        if (RecipeConfig.enableConvergenceCrystalRecipe) addShapedRecipe(
            ModBlocks.CONVERGENCE_CRYSTAL,
            " e ",
            " o ",
            "ooo",
            'e',
            Items.ender_eye,
            'o',
            ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5));

        if (RecipeConfig.enableEnderLocusOreRecipes) {
            // Ore + Coal -> Ingot (Ender Locus)
            EnderLocusRegistry.instance()
                .addRecipe(
                    new ItemStack(Items.gold_ingot, 3),
                    new String[] { "OC ", "   ", "   " },
                    'O',
                    Blocks.gold_ore,
                    'C',
                    Items.coal);

            EnderLocusRegistry.instance()
                .addRecipe(
                    new ItemStack(Items.iron_ingot, 3),
                    new String[] { "OC ", "   ", "   " },
                    'O',
                    Blocks.iron_ore,
                    'C',
                    Items.coal);
        }

        // Giga Torch (Ender Locus)
        if (RecipeConfig.enableGigaTorchRecipe) EnderLocusRegistry.instance()
            .addRecipe(
                ModBlocks.GIGA_TORCH.newItemStack(),
                new String[] { "RCH", "CWC", "CWC" },
                'R',
                new ItemStack(Items.potionitem, 1, 8225),
                'H',
                new ItemStack(Items.potionitem, 1, 8229),
                'W',
                "logWood",
                'C',
                ModBlocks.CHANDELIER);

        // Void Marker (Ender Locus)
        if (RecipeConfig.enableVoidMarkerRecipe) EnderLocusRegistry.instance()
            .addRecipe(
                ModBlocks.VOID_MARKER.newItemStack(1),
                new String[] { " E ", " O ", " O " },
                'O',
                ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5),
                'E',
                Items.ender_pearl);

        // Void Quarry Upgrades (Ender Locus)
        if (!RecipeConfig.enableVoidQuarryUpgradeRecipes) return;

        // World Hole
        EnderLocusRegistry.instance()
            .addRecipe(
                ModBlocks.VOID_QUARRY_UPGRADE.newItemStack(1, 0),
                new String[] { " T ", "QBQ", " E " },
                'T',
                ModBlocks.TRASH_CAN_ITEM,
                'Q',
                Blocks.quartz_block,
                'B',
                ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 2),
                'E',
                ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5));

        // Silk Touch
        ItemStack pickaxe = new ItemStack(Items.golden_pickaxe);
        pickaxe.addEnchantment(Enchantment.silkTouch, 1);
        EnderLocusRegistry.instance()
            .addRecipe(
                ModBlocks.VOID_QUARRY_UPGRADE.newItemStack(1, 1),
                new String[] { " P ", "RBR", " E " },
                'P',
                pickaxe,
                'R',
                Items.redstone,
                'B',
                ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 2),
                'E',
                ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5));

        // Fluid Pump
        EnderLocusRegistry.instance()
            .addRecipe(
                ModBlocks.VOID_QUARRY_UPGRADE.newItemStack(1, 2),
                new String[] { " T ", "RBR", " E " },
                'T',
                Items.bucket,
                'R',
                Items.redstone,
                'B',
                ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 2),
                'E',
                ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5));

        // Speed 1
        // TODO: Redstone -> Transfer Node Speed Upgrade
        pickaxe = new ItemStack(Items.diamond_pickaxe);
        pickaxe.addEnchantment(Enchantment.efficiency, 1);
        EnderLocusRegistry.instance()
            .addRecipe(
                ModBlocks.VOID_QUARRY_UPGRADE.newItemStack(1, 3),
                new String[] { " P ", "RBR", " E " },
                'P',
                pickaxe,
                'R',
                Items.redstone,
                'B',
                ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 2),
                'E',
                ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5));

        // Speed 2
        // TODO: Redstone -> Transfer Node Speed Upgrade
        pickaxe = new ItemStack(Items.diamond_pickaxe);
        pickaxe.addEnchantment(Enchantment.efficiency, 3);
        EnderLocusRegistry.instance()
            .addRecipe(
                ModBlocks.VOID_QUARRY_UPGRADE.newItemStack(1, 4),
                new String[] { " P ", "RBR", " E " },
                'P',
                pickaxe,
                'R',
                Items.redstone,
                'B',
                ModBlocks.VOID_QUARRY_UPGRADE.newItemStack(1, 3),
                'E',
                ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5));

        // Speed 3
        // TODO: Redstone -> Transfer Node Stack Upgrade
        pickaxe = new ItemStack(Items.diamond_pickaxe);
        pickaxe.addEnchantment(Enchantment.efficiency, 5);
        EnderLocusRegistry.instance()
            .addRecipe(
                ModBlocks.VOID_QUARRY_UPGRADE.newItemStack(1, 5),
                new String[] { "P P", "RBR", " E " },
                'P',
                pickaxe,
                'R',
                Items.redstone,
                'B',
                ModBlocks.VOID_QUARRY_UPGRADE.newItemStack(1, 4),
                'E',
                ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5));

        // Fortune 1
        pickaxe = new ItemStack(Items.iron_pickaxe);
        pickaxe.addEnchantment(Enchantment.fortune, 1);
        EnderLocusRegistry.instance()
            .addRecipe(
                ModBlocks.VOID_QUARRY_UPGRADE.newItemStack(1, 6),
                new String[] { " P ", "RBR", " E " },
                'P',
                pickaxe,
                'R',
                Items.redstone,
                'B',
                ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 2),
                'E',
                ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5));

        // Fortune 2
        pickaxe = new ItemStack(Items.golden_pickaxe);
        pickaxe.addEnchantment(Enchantment.fortune, 1);
        EnderLocusRegistry.instance()
            .addRecipe(
                ModBlocks.VOID_QUARRY_UPGRADE.newItemStack(1, 7),
                new String[] { " P ", "RBR", " E " },
                'P',
                pickaxe,
                'R',
                Items.redstone,
                'B',
                ModBlocks.VOID_QUARRY_UPGRADE.newItemStack(1, 6),
                'E',
                ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5));

        // Fortune 3
        pickaxe = new ItemStack(Items.diamond_pickaxe);
        pickaxe.addEnchantment(Enchantment.fortune, 1);
        EnderLocusRegistry.instance()
            .addRecipe(
                ModBlocks.VOID_QUARRY_UPGRADE.newItemStack(1, 8),
                new String[] { " P ", "RBR", " E " },
                'P',
                pickaxe,
                'R',
                Items.redstone,
                'B',
                ModBlocks.VOID_QUARRY_UPGRADE.newItemStack(1, 7),
                'E',
                ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 5));
    }

    private static boolean addShapedRecipe(Object outputObject, Object... params) {
        return DisableableItemStack.addShapedRecipe(outputObject, params);
    }

    private static boolean addShapelessRecipe(Object outputObject, Object... params) {
        return DisableableItemStack.addShapelessRecipe(outputObject, params);
    }

    private static boolean addFurnaceRecipe(Object inputObject, Object outputObject, float exp) {
        return DisableableItemStack.addFurnaceRecipe(inputObject, outputObject, exp);
    }

    private static void loadCompressedBlockRecipes() {
        if (!RecipeConfig.enableCompressedBlockRecipes) return;

        ModBlocks[] blocks = { ModBlocks.COMPRESSED_COBBLESTONE, ModBlocks.COMPRESSED_DIRT, ModBlocks.COMPRESSED_GRAVEL,
            ModBlocks.COMPRESSED_SAND, };

        for (ModBlocks modBlock : blocks) {
            if (!(modBlock.get() instanceof BlockCompressed block) || !modBlock.isEnabled()) continue;
            addShapedRecipe(
                new DisableableItemStack(modBlock),
                "###",
                "###",
                "###",
                '#',
                new ItemStack(block.getBase(), 9));
            addShapelessRecipe(new DisableableItemStack(modBlock, 9), new DisableableItemStack(modBlock, 1));
            for (int i = 0; i < 7; i++) {
                addShapedRecipe(
                    new DisableableItemStack(modBlock, 1, i + 1),
                    "###",
                    "###",
                    "###",
                    '#',
                    new DisableableItemStack(modBlock, 1, i));
                addShapelessRecipe(
                    new DisableableItemStack(modBlock, 9, i),
                    new DisableableItemStack(modBlock, 1, i + 1));
            }
        }
    }
}
