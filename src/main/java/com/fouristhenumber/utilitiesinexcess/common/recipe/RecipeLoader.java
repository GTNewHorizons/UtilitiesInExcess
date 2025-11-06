package com.fouristhenumber.utilitiesinexcess.common.recipe;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockCompressed;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;

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

        if (ModItems.INVERSION_SIGIL_ACTIVE.isEnabled() && ModItems.INVERTED_INGOT.isEnabled()) {
            GameRegistry.addRecipe(new RecipeInvertedIngot(new ItemStack(ModItems.INVERTED_INGOT.get())));
        }

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
            ModItems.INVERTED_INGOT);

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

        // Redstone Clock
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
            new ItemStack(Blocks.wool, 1, 32767),
            'j',
            Blocks.jukebox);

        // Rain Muffler
        addShapedRecipe(
            ModBlocks.RAIN_MUFFLER,
            "www",
            "wbw",
            "www",
            'w',
            new ItemStack(Blocks.wool, 1, 32767),
            'b',
            Items.bucket);

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
            new ItemStack(Blocks.wool, 1, 14),
            'd',
            Items.diamond,
            'b',
            Blocks.gold_block);
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
                ModItems.INVERTED_INGOT,
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
            ModItems.INVERTED_INGOT,
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
            ModItems.INVERTED_INGOT);

        // Dark Ethereal Glass
        // TODO: Reliant on: Blackout Curtains, Dark Glass

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

    private static void loadInversionRecipes() {
        // Diamond Stick
        addShapedRecipe(new DisableableItemStack(ModItems.DIAMOND_STICK, 4), "#", "#", '#', Items.diamond);

        /*
        // Inverted Ingot
        addShapedRecipe(
            ModItems.INVERTED_INGOT,
            "i",
            "#",
            "d",
            'i',
            Items.iron_ingot,
            '#',
            ModItems.INVERSION_SIGIL_ACTIVE,
            'd',
            Items.diamond);

         */

        // Glutton's Axe
        addShapedRecipe(
            ModItems.GLUTTONS_AXE,
            "ii",
            "is",
            " s",
            'i',
            ModItems.INVERTED_INGOT,
            's',
            ModItems.DIAMOND_STICK);

        // Destruction Pickaxe
        addShapedRecipe(
            ModItems.DESTRUCTION_PICKAXE,
            "iii",
            " s ",
            " s ",
            'i',
            ModItems.INVERTED_INGOT,
            's',
            ModItems.DIAMOND_STICK);

        // Reversing Hoe
        addShapedRecipe(
            ModItems.REVERSING_HOE,
            "ii",
            " s",
            " s",
            'i',
            ModItems.INVERTED_INGOT,
            's',
            ModItems.DIAMOND_STICK);

        // Anti-Particulate Shovel
        addShapedRecipe(
            ModItems.ANTI_PARTICULATE_SHOVEL,
            "i",
            "s",
            "s",
            'i',
            ModItems.INVERTED_INGOT,
            's',
            ModItems.DIAMOND_STICK);

        // Etheric Sword
        addShapedRecipe(
            ModItems.ETHERIC_SWORD,
            "i",
            "i",
            "s",
            'i',
            ModItems.INVERTED_INGOT,
            's',
            ModItems.DIAMOND_STICK);

        // Precision Shears
        addShapedRecipe(ModItems.PRECISION_SHEARS, " i", "i ", 'i', ModItems.INVERTED_INGOT);

        // Architect's Staff
        addShapedRecipe(
            ModItems.ARCHITECTS_WAND,
            " i",
            "s ",
            'i',
            ModItems.INVERTED_INGOT,
            's',
            ModItems.DIAMOND_STICK);

        // Inverted Ingot -> Block
        addShapedRecipe(ModBlocks.INVERTED_BLOCK, "iii", "iii", "iii", 'i', ModItems.INVERTED_INGOT);
        // Inverted Block -> Ingot
        addShapedRecipe(new DisableableItemStack(ModItems.INVERTED_INGOT, 9), "b", 'b', ModBlocks.INVERTED_BLOCK);
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
