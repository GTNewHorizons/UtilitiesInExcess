package com.fouristhenumber.utilitiesinexcess.common.recipe;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.api.QEDRegistry;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockCompressed;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;

import cpw.mods.fml.common.registry.GameRegistry;

public class RecipeLoader {

    public static void run() {
        if (Mods.Dreamcraft.isLoaded()) return;
        loadCompressedBlockRecipes();
        loadInversionRecipes();
        loadBedrockiumRecipes();
        loadEtherealGlassRecipes();
        loadWateringCanRecipes();
        loadLapisAetheriusRecipes();
        loadSpikeRecipes();
        loadGeneratorRecipes();
        loadQEDRecipes();
        loadGlassRecipes();

        // Pacifist's Bench
        addShapedRecipe(
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
        // TODO: Replace obsidian with "ender-infused obsidian" equivalent
        addShapedRecipe(
            ModBlocks.SMART_PUMP,
            "odo",
            "lew",
            "opo",
            'o',
            Blocks.obsidian,
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
            Blocks.obsidian,
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

        // Block Analyzer
        addShapedRecipe(
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
        addShapedRecipe(
            ModItems.XRAY_GLASSES,
            "iii",
            "eie",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            'e',
            Items.ender_eye);

        // Floating Block
        addShapedRecipe(
            ModBlocks.FLOATING_BLOCK,
            " g ",
            "fof",
            'g',
            Items.gold_ingot,
            'f',
            Items.feather,
            'o',
            Blocks.obsidian);

        // Heavenly Ring
        addShapedRecipe(
            ModItems.HEAVENLY_RING,
            "#g#",
            "g*g",
            "igi",
            '#',
            Blocks.glass,
            'g',
            Items.gold_ingot,
            '*',
            Items.nether_star,
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE));

        // Redstone Clock
        addShapedRecipe(
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
        addShapedRecipe(
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

        // Trash Can (Items)
        addShapedRecipe(
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
        addShapelessRecipe(ModBlocks.TRASH_CAN_FLUID, ModBlocks.TRASH_CAN_ITEM, Items.bucket);

        // Trash Can (Energy)
        addShapelessRecipe(
            ModBlocks.TRASH_CAN_ENERGY,
            ModBlocks.TRASH_CAN_ITEM,
            Items.redstone,
            Items.redstone,
            Items.gold_ingot,
            Items.gold_ingot);

        // Drum
        addShapedRecipe(
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

        // Sound Muffler
        addShapedRecipe(
            ModBlocks.SOUND_MUFFLER,
            "www",
            "wjw",
            "www",
            'w',
            new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE),
            'j',
            Blocks.noteblock);

        // Rain Muffler
        addShapedRecipe(
            ModBlocks.RAIN_MUFFLER,
            "www",
            "wbw",
            "www",
            'w',
            new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE),
            'b',
            Items.water_bucket);

        // Fire Battery
        addShapedRecipe(
            ModItems.FIRE_BATTERY,
            "ppp",
            "p p",
            "prp",
            'p',
            Blocks.heavy_weighted_pressure_plate,
            'r',
            Items.redstone);

        // Underworld Portal
        addShapedRecipe(
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
        addShapedRecipe(
            ModBlocks.BLACKOUT_CURTAINS.newItemStack(12),
            "ww",
            "ww",
            "ww",
            'w',
            new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE));

        // Conveyor
        addShapedRecipe(
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
        addShapedRecipe(
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

        // Pure Love
        addShapedRecipe(
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
        addShapedRecipe(ModBlocks.MARGINALLY_MAXIMISED_CHEST, "sss", "scs", "sss", 's', Items.stick, 'c', Blocks.chest);

        // Significantly Shrunk Chest
        addShapelessRecipe(new DisableableItemStack(ModBlocks.SIGNIFICANTLY_SHRUNK_CHEST, 9), Blocks.chest);
        addShapelessRecipe(
            new DisableableItemStack(ModBlocks.SIGNIFICANTLY_SHRUNK_CHEST, 9),
            ModBlocks.MARGINALLY_MAXIMISED_CHEST);

        // Radically Reduced Chest
        addShapelessRecipe(
            new DisableableItemStack(ModBlocks.RADICALLY_REDUCED_CHEST, 9),
            ModBlocks.SIGNIFICANTLY_SHRUNK_CHEST);

        // Mob Jar
        addShapedRecipe(
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
        addShapedRecipe(
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
        addShapedRecipe(
            ModBlocks.END_OF_TIME_PORTAL,
            "qgq",
            "geg",
            "qcq",
            'q',
            Blocks.quartz_block, // TODO use burnt quartz replacement instead?
            'e',
            Items.ender_pearl,
            'c',
            Items.clock,
            'g',
            Blocks.glass_pane);
    }

    private static void loadGeneratorRecipes() {
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
        addShapedRecipe(
            ModBlocks.SPIKE_WOOD,
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
            ModBlocks.SPIKE_IRON,
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
            ModBlocks.SPIKE_GOLD,
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
            ModBlocks.SPIKE_DIAMOND,
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

    private static void loadLapisAetheriusRecipes() {
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
        addShapedRecipe(
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
        addShapedRecipe(
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
        addShapedRecipe(
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

    private static void loadEtherealGlassRecipes() {
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

        // Dark Ethereal Glass (Inverted)
        addShapelessRecipe(
            new DisableableItemStack(ModBlocks.ETHEREAL_GLASS, 1, 5),
            new DisableableItemStack(ModBlocks.ETHEREAL_GLASS, 1, 2),
            Blocks.redstone_torch);
    }

    private static void loadBedrockiumRecipes() {
        // Bedrockium Ingot
        addShapedRecipe(
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
        addShapedRecipe(ModBlocks.BEDROCKIUM_BLOCK, "iii", "iii", "iii", 'i', ModItems.BEDROCKIUM_INGOT);
        // Bedrockium Block -> Ingot
        addShapedRecipe(new DisableableItemStack(ModItems.BEDROCKIUM_INGOT, 9), "b", 'b', ModBlocks.BEDROCKIUM_BLOCK);
        // Direct Block Recipe
        addFurnaceRecipe(
            new DisableableItemStack(ModBlocks.COMPRESSED_COBBLESTONE, 1, 7),
            ModBlocks.BEDROCKIUM_BLOCK,
            1F);
    }

    private static void loadGlassRecipes() {
        // TODO: Smooth Glass reliant on "sandy glass"

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

        // Latticed Glass
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
    }

    private static void loadInversionRecipes() {
        // Inverted Ingot (unstable)
        // Has to use a special recipe adder to check for vanilla crafting table
        if (ModItems.INVERSION_SIGIL_ACTIVE.isEnabled() && ModItems.INVERTED_INGOT.isEnabled()) {
            GameRegistry.addRecipe(
                new RecipeInvertedIngot(
                    1,
                    3,
                    new ItemStack[] { new ItemStack(Items.iron_ingot), ModItems.INVERSION_SIGIL_ACTIVE.newItemStack(),
                        new ItemStack(Items.diamond), },
                    ModItems.INVERTED_INGOT.newItemStack()));
        }

        addShapedRecipe(
            ModItems.INVERTED_INGOT.newItemStack(1, 1),
            "i",
            "s",
            "d",
            'i',
            Items.iron_ingot,
            's',
            ModItems.PSEUDO_INVERSION_SIGIL,
            'd',
            Items.diamond);

        // Inverted Nugget
        addShapedRecipe(
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

        // Inverted Ingot (stable)
        addShapedRecipe(ModItems.INVERTED_INGOT.newItemStack(1, 1), "nnn", "nnn", "nnn", 'n', ModItems.INVERTED_NUGGET);

        // Diamond Stick
        addShapedRecipe(new DisableableItemStack(ModItems.DIAMOND_STICK, 4), "#", "#", '#', Items.diamond);

        // Glutton's Axe
        addShapedRecipe(
            ModItems.GLUTTONS_AXE,
            "ii",
            "is",
            " s",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            's',
            ModItems.DIAMOND_STICK);

        // Destruction Pickaxe
        addShapedRecipe(
            ModItems.DESTRUCTION_PICKAXE,
            "iii",
            " s ",
            " s ",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            's',
            ModItems.DIAMOND_STICK);

        // Reversing Hoe
        addShapedRecipe(
            ModItems.REVERSING_HOE,
            "ii",
            " s",
            " s",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            's',
            ModItems.DIAMOND_STICK);

        // Anti-Particulate Shovel
        addShapedRecipe(
            ModItems.ANTI_PARTICULATE_SHOVEL,
            "i",
            "s",
            "s",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            's',
            ModItems.DIAMOND_STICK);

        // Etheric Sword
        addShapedRecipe(
            ModItems.ETHERIC_SWORD,
            "i",
            "i",
            "s",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            's',
            ModItems.DIAMOND_STICK);

        // Precision Shears
        addShapedRecipe(
            ModItems.PRECISION_SHEARS,
            " i",
            "i ",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE));

        // Architect's Wand
        addShapedRecipe(
            ModItems.ARCHITECTS_WAND,
            " i",
            "s ",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE),
            's',
            ModItems.DIAMOND_STICK);

        // Super Architect's Wand
        addShapedRecipe(
            ModItems.SUPER_ARCHITECTS_WAND,
            " i",
            "s ",
            'i',
            ModItems.BEDROCKIUM_INGOT,
            's',
            ModItems.ARCHITECTS_WAND);

        // Inverted Ingot -> Block
        addShapedRecipe(
            ModBlocks.INVERTED_BLOCK,
            "iii",
            "iii",
            "iii",
            'i',
            ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE));

        // Glove
        addShapedRecipe(ModItems.GLOVE, "is", "si", 'i', Blocks.wool, 's', Items.string);
    }

    private static void loadQEDRecipes() {
        // todo test recipe, remove later
        QEDRegistry.instance()
            .addRecipe(
                new ItemStack(Items.gold_ingot),
                new String[] { "NNN", "NGN", "NNN" },
                'N',
                new ItemStack(Items.gold_nugget),
                'G',
                "blockGlass");
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
