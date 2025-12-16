package com.fouristhenumber.utilitiesinexcess;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockAdvancedUpdateDetector;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockBedrockium;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockBlackoutCurtains;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColored;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockCompressed;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockConveyor;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockCursedEarth;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockDecorative;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockDecorativeGlass;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockDrum;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockEnderLotus;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockEtherealGlass;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockFloating;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockInverted;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockLapisAetherius;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockMagicWood;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockMarginallyMaximisedChest;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockPacifistsBench;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockPortalEndOfTime;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockPortalUnderWorld;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockPureLove;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockRadicallyReducedChest;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockRainMuffler;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockRedstoneClock;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockSignificantlyShrunkChest;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockSmartPump;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockSoundMuffler;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockSpike;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockTrashCanEnergy;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockTrashCanFluid;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockTrashCanItem;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockUpdateDetector;
import com.fouristhenumber.utilitiesinexcess.common.blocks.generators.BlockEnderGenerator;
import com.fouristhenumber.utilitiesinexcess.common.blocks.generators.BlockFoodGenerator;
import com.fouristhenumber.utilitiesinexcess.common.blocks.generators.BlockFurnaceGenerator;
import com.fouristhenumber.utilitiesinexcess.common.blocks.generators.BlockHighTemperatureFurnaceGenerator;
import com.fouristhenumber.utilitiesinexcess.common.blocks.generators.BlockLavaGenerator;
import com.fouristhenumber.utilitiesinexcess.common.blocks.generators.BlockLowTemperatureFurnaceGenerator;
import com.fouristhenumber.utilitiesinexcess.common.blocks.generators.BlockNetherStarGenerator;
import com.fouristhenumber.utilitiesinexcess.common.blocks.generators.BlockPinkGenerator;
import com.fouristhenumber.utilitiesinexcess.common.blocks.generators.BlockPotionGenerator;
import com.fouristhenumber.utilitiesinexcess.common.blocks.generators.BlockRedstoneGenerator;
import com.fouristhenumber.utilitiesinexcess.common.blocks.generators.BlockSolarGenerator;
import com.fouristhenumber.utilitiesinexcess.common.blocks.generators.BlockTNTGenerator;
import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;
import com.fouristhenumber.utilitiesinexcess.config.blocks.CursedEarthConfig;
import com.fouristhenumber.utilitiesinexcess.config.blocks.EnderLotusConfig;
import com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig;
import com.fouristhenumber.utilitiesinexcess.config.dimensions.EndOfTimeConfig;
import com.fouristhenumber.utilitiesinexcess.config.dimensions.UnderWorldConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.ItemConfig;

import cpw.mods.fml.common.registry.GameRegistry;

// Credit to Et Futurum (Requiem)
public enum ModBlocks {
    // spotless:off

    // make sure to leave a trailing comma
    FLOATING_BLOCK(BlockConfig.enableFloatingBlock, new BlockFloating(), BlockFloating.ItemBlockFloating.class, "floating_block"),
    COMPRESSED_COBBLESTONE(BlockConfig.enableCompressedCobblestone, new BlockCompressed(Blocks.cobblestone, "compressed_cobblestone"), BlockCompressed.ItemCompressedBlock.class, "compressed_cobblestone"),
    COMPRESSED_DIRT(BlockConfig.enableCompressedDirt, new BlockCompressed(Blocks.dirt, "compressed_dirt"), BlockCompressed.ItemCompressedBlock.class, "compressed_dirt"),
    COMPRESSED_SAND(BlockConfig.enableCompressedSand, new BlockCompressed(Blocks.sand, "compressed_sand"), BlockCompressed.ItemCompressedBlock.class, "compressed_sand"),
    COMPRESSED_GRAVEL(BlockConfig.enableCompressedGravel, new BlockCompressed(Blocks.gravel, "compressed_gravel"), BlockCompressed.ItemCompressedBlock.class, "compressed_gravel"),
    COLORED_WOOD_PLANKS(BlockConfig.enableColoredBlocks, new BlockColored(Blocks.planks), BlockColored.ItemBlockColored.class, "colored_planks"),
    COLORED_GLOWSTONE(BlockConfig.enableColoredBlocks, new BlockColored(Blocks.glowstone).setLightLevel(1f), BlockColored.ItemBlockColored.class, "colored_glowstone"),
    COLORED_STONE(BlockConfig.enableColoredBlocks, new BlockColored(Blocks.stone), BlockColored.ItemBlockColored.class, "colored_stone"),
    COLORED_COBBLESTONE(BlockConfig.enableColoredBlocks, new BlockColored(Blocks.cobblestone), BlockColored.ItemBlockColored.class, "colored_cobblestone"),
    COLORED_QUARTZ_BLOCK(BlockConfig.enableColoredBlocks, new BlockColored(Blocks.quartz_block, 1f), BlockColored.ItemBlockColored.class, "colored_quartz_block"),
    COLORED_SOUL_SAND(BlockConfig.enableColoredBlocks, new BlockColored(Blocks.soul_sand), BlockColored.ItemBlockColored.class, "colored_soul_sand"),
    COLORED_REDSTONE_LAMP(BlockConfig.enableColoredBlocks, new BlockColored(Blocks.redstone_lamp).setLightLevel(1f), BlockColored.ItemBlockColored.class, "colored_redstone_lamp"),
    COLORED_BRICKS(BlockConfig.enableColoredBlocks, new BlockColored(Blocks.brick_block), BlockColored.ItemBlockColored.class, "colored_bricks"),
    COLORED_STONE_BRICKS(BlockConfig.enableColoredBlocks, new BlockColored(Blocks.stonebrick), BlockColored.ItemBlockColored.class, "colored_stone_bricks"),
    COLORED_LAPIS_BLOCK(BlockConfig.enableColoredBlocks, new BlockColored(Blocks.lapis_block), BlockColored.ItemBlockColored.class, "colored_lapis_block"),
    COLORED_OBSIDIAN(BlockConfig.enableColoredBlocks, new BlockColored(Blocks.obsidian), BlockColored.ItemBlockColored.class, "colored_obsidian"),
    COLORED_REDSTONE_BLOCK(BlockConfig.enableColoredBlocks, new BlockColored(Blocks.redstone_block, 2f), BlockColored.ItemBlockColored.class, "colored_redstone_block"),
    COLORED_COAL_BLOCK(BlockConfig.enableColoredBlocks, new BlockColored(Blocks.coal_block), BlockColored.ItemBlockColored.class, "colored_coal_block"),
    REDSTONE_CLOCK(BlockConfig.enableRedstoneClock, new BlockRedstoneClock(), "redstone_clock"),
    ETHEREAL_GLASS(BlockConfig.enableEtherealGlass, new BlockEtherealGlass(), BlockEtherealGlass.ItemBlockEtherealGlass.class, "ethereal_glass"),
    TRASH_CAN_ITEM(BlockConfig.enableTrashCanItem, new BlockTrashCanItem(), "trash_can_item"),
    TRASH_CAN_FLUID(BlockConfig.enableTrashCanFluid, new BlockTrashCanFluid(), "trash_can_fluid"),
    TRASH_CAN_ENERGY(BlockConfig.enableTrashCanEnergy, new BlockTrashCanEnergy(), "trash_can_energy"),
    DRUM(BlockConfig.enableDrum, new BlockDrum(16000), BlockDrum.ItemBlockDrum.class, "drum"),
    SOUND_MUFFLER(BlockConfig.soundMuffler.enableSoundMuffler, new BlockSoundMuffler(), BlockSoundMuffler.ItemBlockSoundMuffler.class, "sound_muffler"),
    RAIN_MUFFLER(BlockConfig.rainMuffler.enableRainMuffler, new BlockRainMuffler(), BlockRainMuffler.ItemBlockRainMuffler.class, "rain_muffler"),
    MAGIC_WOOD(BlockConfig.enableMagicWood, new BlockMagicWood(), "magic_wood"),
    PURE_LOVE(BlockConfig.pureLove.enablePureLove, new BlockPureLove(), BlockPureLove.ItemBlockPureLove.class, "pure_love"),
    MARGINALLY_MAXIMISED_CHEST(BlockConfig.enableMarginallyMaximisedChest, new BlockMarginallyMaximisedChest(), "marginally_maximised_chest"),
    SIGNIFICANTLY_SHRUNK_CHEST(BlockConfig.enableSignificantlyShrunkChest, new BlockSignificantlyShrunkChest(), "significantly_shrunk_chest"),
    RADICALLY_REDUCED_CHEST(BlockConfig.enableRadicallyReducedChest, new BlockRadicallyReducedChest(), "radically_reduced_chest"),
    CURSED_EARTH(CursedEarthConfig.enableCursedEarth, new BlockCursedEarth(false), BlockCursedEarth.ItemBlockCursedEarth.class, "cursed_earth"),
    BLESSED_EARTH(CursedEarthConfig.enableBlessedEarth, new BlockCursedEarth(true), BlockCursedEarth.ItemBlockCursedEarth.class, "blessed_earth"),
    LAPIS_AETHERIUS(BlockConfig.enableLapisAetherius, new BlockLapisAetherius(), BlockLapisAetherius.ItemLapisAetherius.class, "lapis_aetherius"),
    BEDROCKIUM_BLOCK(ItemConfig.enableBedrockium, new BlockBedrockium(), BlockBedrockium.ItemBlockBedrockium.class, "bedrockium_block"),
    INVERTED_BLOCK(InversionConfig.enableInvertedIngot, new BlockInverted(), "inverted_block"),
    LOW_TEMPERATURE_FURNACE_GENERATOR(GeneratorConfig.enableLowTemperatureFurnaceGenerator, new BlockLowTemperatureFurnaceGenerator("low_temperature_furnace_generator", 1), "low_temperature_furnace_generator"),
    LOW_TEMPERATURE_FURNACE_GENERATOR_PLUS(GeneratorConfig.enableLowTemperatureFurnaceGenerator, new BlockLowTemperatureFurnaceGenerator("low_temperature_furnace_generator_plus", 8), "low_temperature_furnace_generator_plus"),
    LOW_TEMPERATURE_FURNACE_GENERATOR_PLUSPLUS(GeneratorConfig.enableLowTemperatureFurnaceGenerator, new BlockLowTemperatureFurnaceGenerator("low_temperature_furnace_generator_plusplus", 64), "low_temperature_furnace_generator_plusplus"),
    FURNACE_GENERATOR(GeneratorConfig.enableFurnaceGenerator, new BlockFurnaceGenerator("furnace_generator", 1), "furnace_generator"),
    FURNACE_GENERATOR_PLUS(GeneratorConfig.enableFurnaceGenerator, new BlockFurnaceGenerator("furnace_generator_plus", 8), "furnace_generator_plus"),
    FURNACE_GENERATOR_PLUSPLUS(GeneratorConfig.enableFurnaceGenerator, new BlockFurnaceGenerator("furnace_generator_plusplus", 64), "furnace_generator_plusplus"),
    HIGH_TEMPERATURE_FURNACE_GENERATOR(GeneratorConfig.enableHighTemperatureFurnaceGenerator, new BlockHighTemperatureFurnaceGenerator("high_temperature_furnace_generator", 1), "high_temperature_furnace_generator"),
    HIGH_TEMPERATURE_FURNACE_GENERATOR_PLUS(GeneratorConfig.enableHighTemperatureFurnaceGenerator, new BlockHighTemperatureFurnaceGenerator("high_temperature_furnace_generator_plus", 8), "high_temperature_furnace_generator_plus"),
    HIGH_TEMPERATURE_FURNACE_GENERATOR_PLUSPLUS(GeneratorConfig.enableHighTemperatureFurnaceGenerator, new BlockHighTemperatureFurnaceGenerator("high_temperature_furnace_generator_plusplus", 64), "high_temperature_furnace_generator_plusplus"),
    LAVA_GENERATOR(GeneratorConfig.enableLavaGenerator, new BlockLavaGenerator("lava_generator", 1), "lava_generator"),
    LAVA_GENERATOR_PLUS(GeneratorConfig.enableLavaGenerator, new BlockLavaGenerator("lava_generator_plus", 8), "lava_generator_plus"),
    LAVA_GENERATOR_PLUSPLUS(GeneratorConfig.enableLavaGenerator, new BlockLavaGenerator("lava_generator_plusplus", 64), "lava_generator_plusplus"),
    ENDER_GENERATOR(GeneratorConfig.enableEnderGenerator, new BlockEnderGenerator("ender_generator", 1), "ender_generator"),
    ENDER_GENERATOR_PLUS(GeneratorConfig.enableEnderGenerator, new BlockEnderGenerator("ender_generator_plus", 8), "ender_generator_plus"),
    ENDER_GENERATOR_PLUSPLUS(GeneratorConfig.enableEnderGenerator, new BlockEnderGenerator("ender_generator_plusplus", 64), "ender_generator_plusplus"),
    REDSTONE_GENERATOR(GeneratorConfig.enableRedstoneGenerator, new BlockRedstoneGenerator("redstone_generator", 1), "redstone_generator"),
    REDSTONE_GENERATOR_PLUS(GeneratorConfig.enableRedstoneGenerator, new BlockRedstoneGenerator("redstone_generator_plus", 8), "redstone_generator_plus"),
    REDSTONE_GENERATOR_PLUSPLUS(GeneratorConfig.enableRedstoneGenerator, new BlockRedstoneGenerator("redstone_generator_plusplus", 64), "redstone_generator_plusplus"),
    FOOD_GENERATOR(GeneratorConfig.enableFoodGenerator, new BlockFoodGenerator("food_generator", 1), "food_generator"),
    FOOD_GENERATOR_PLUS(GeneratorConfig.enableFoodGenerator, new BlockFoodGenerator("food_generator_plus", 8), "food_generator_plus"),
    FOOD_GENERATOR_PLUSPLUS(GeneratorConfig.enableFoodGenerator, new BlockFoodGenerator("food_generator_plusplus", 64), "food_generator_plusplus"),
    POTION_GENERATOR(GeneratorConfig.enablePotionGenerator, new BlockPotionGenerator("potion_generator", 1), "potion_generator"),
    POTION_GENERATOR_PLUS(GeneratorConfig.enablePotionGenerator, new BlockPotionGenerator("potion_generator_plus", 8), "potion_generator_plus"),
    POTION_GENERATOR_PLUSPLUS(GeneratorConfig.enablePotionGenerator, new BlockPotionGenerator("potion_generator_plusplus", 64), "potion_generator_plusplus"),
    SOLAR_GENERATOR(GeneratorConfig.enableSolarGenerator, new BlockSolarGenerator("solar_generator", 1), "solar_generator"),
    SOLAR_GENERATOR_PLUS(GeneratorConfig.enableSolarGenerator, new BlockSolarGenerator("solar_generator_plus", 8), "solar_generator_plus"),
    SOLAR_GENERATOR_PLUSPLUS(GeneratorConfig.enableSolarGenerator, new BlockSolarGenerator("solar_generator_plusplus", 64), "solar_generator_plusplus"),
    TNT_GENERATOR(GeneratorConfig.enableTNTGenerator, new BlockTNTGenerator("tnt_generator", 1), "tnt_generator"),
    TNT_GENERATOR_PLUS(GeneratorConfig.enableTNTGenerator, new BlockTNTGenerator("tnt_generator_plus", 8), "tnt_generator_plus"),
    TNT_GENERATOR_PLUSPLUS(GeneratorConfig.enableTNTGenerator, new BlockTNTGenerator("tnt_generator_plusplus", 64), "tnt_generator_plusplus"),
    PINK_GENERATOR(GeneratorConfig.enablePinkGenerator, new BlockPinkGenerator("pink_generator", 1), "pink_generator"),
    PINK_GENERATOR_PLUS(GeneratorConfig.enablePinkGenerator, new BlockPinkGenerator("pink_generator_plus", 8), "pink_generator_plus"),
    PINK_GENERATOR_PLUSPLUS(GeneratorConfig.enablePinkGenerator, new BlockPinkGenerator("pink_generator_plusplus", 64), "pink_generator_plusplus"),
    NETHER_STAR_GENERATOR(GeneratorConfig.enableNetherStarGenerator, new BlockNetherStarGenerator("nether_star_generator", 1), "nether_star_generator"),
    NETHER_STAR_GENERATOR_PLUS(GeneratorConfig.enableNetherStarGenerator, new BlockNetherStarGenerator("nether_star_generator_plus", 8), "nether_star_generator_plus"),
    NETHER_STAR_GENERATOR_PLUSPLUS(GeneratorConfig.enableNetherStarGenerator, new BlockNetherStarGenerator("nether_star_generator_plusplus", 64), "nether_star_generator_plusplus"),
    BLOCK_UPDATE_DETECTOR(BlockConfig.enableBlockUpdateDetector, new BlockUpdateDetector(), "block_update_detector"),
    ADVANCED_BLOCK_UPDATE_DETECTOR(BlockConfig.enableBlockUpdateDetector, new BlockAdvancedUpdateDetector(), "advanced_block_update_detector"),
    ENDER_LOTUS(EnderLotusConfig.enableEnderLotus, new BlockEnderLotus(), null, "ender_lotus"),
    BLACKOUT_CURTAINS(BlockConfig.enableBlackoutCurtains, new BlockBlackoutCurtains(), "blackout_curtains"),
    CONVEYOR(BlockConfig.enableConveyor, new BlockConveyor(), BlockConveyor.ItemBlockConveyor.class, "conveyor"),
    SPIKE_WOOD(BlockConfig.spikes.enableWoodenSpike, new BlockSpike(BlockSpike.SpikeType.WOOD, "wood_spike"), BlockSpike.ItemSpike.class, "wood_spike"),
    SPIKE_IRON(BlockConfig.spikes.enableIronSpike, new BlockSpike(BlockSpike.SpikeType.IRON, "iron_spike"), BlockSpike.ItemSpike.class, "iron_spike"),
    SPIKE_GOLD(BlockConfig.spikes.enableGoldSpike, new BlockSpike(BlockSpike.SpikeType.GOLD, "gold_spike"), BlockSpike.ItemSpike.class, "gold_spike"),
    SPIKE_DIAMOND(BlockConfig.spikes.enableDiamondSpike, new BlockSpike(BlockSpike.SpikeType.DIAMOND, "diamond_spike"), BlockSpike.ItemSpike.class, "diamond_spike"),
    UNDERWORLD_PORTAL(BlockConfig.enableUnderWorldPortal && UnderWorldConfig.enableUnderWorld, new BlockPortalUnderWorld(), "underworld_portal"),
    END_OF_TIME_PORTAL(BlockConfig.enableEndOfTimePortal && EndOfTimeConfig.enableEndOfTime, new BlockPortalEndOfTime(), BlockPortalEndOfTime.ItemBlockPortalEndOfTime.class, "temporal_gate"),
    DECORATIVE_GLASS(BlockConfig.enableDecorativeGlass, new BlockDecorativeGlass(), BlockDecorativeGlass.ItemBlockDecorativeGlass.class, "decorative_glass"),
    PACIFISTS_BENCH(BlockConfig.enablePacifistsBench, new BlockPacifistsBench(), "pacifists_bench"),
    SMART_PUMP(BlockConfig.enableSmartPump, new BlockSmartPump(), "smart_pump"),
    DECORATIVE_BLOCKS(BlockConfig.enableDecorativeBlocks, new BlockDecorative(), BlockDecorative.ItemBlockDecorative.class, "decorative_block"),
    ; // leave trailing semicolon
    // spotless:on

    public static final ModBlocks[] VALUES = values();

    public static void init() {
        for (ModBlocks block : VALUES) {
            if (block.isEnabled()) {
                block.theBlock.setCreativeTab(UtilitiesInExcess.uieTab);
                if (block.getItemBlock() != null || !block.getHasItemBlock()) {
                    GameRegistry.registerBlock(block.get(), block.getItemBlock(), block.name);
                    // This part is used if the getItemBlock() is not ItemBlock.class, so we register a custom ItemBlock
                    // class as the ItemBlock
                    // It is also used if the getItemBlock() == null and getHasItemBlock() is false, meaning we WANT to
                    // register it as null, making the block have no inventory item.
                } else {
                    GameRegistry.registerBlock(block.get(), block.name);
                    // Used if getItemBlock() == null but getHasItemBlock() is true, registering it with a default
                    // inventory item.
                }
            }
        }
    }

    private final boolean isEnabled;
    private final Block theBlock;
    /**
     * null == default ItemBlock
     */
    private final Class<? extends ItemBlock> itemBlock;
    /**
     * Determines if we should register the block with an ItemBlock.
     * Set to false when the constructor that specifies the ItemBlock is specifically set to false.
     */
    private boolean hasItemBlock;
    private final String name;

    ModBlocks(Boolean enabled, Block block, String name) {
        this(enabled, block, null, name);
        hasItemBlock = true;
    }

    ModBlocks(Boolean enabled, Block block, Class<? extends ItemBlock> iblock, String name) {
        isEnabled = enabled;
        theBlock = block;
        itemBlock = iblock;
        hasItemBlock = iblock != null;
        this.name = name;
    }

    /**
     * If this is false, the block is initialized without an inventory item, or ItemBlock.
     */
    public boolean getHasItemBlock() {
        return hasItemBlock;
    }

    public Block get() {
        return theBlock;
    }

    public Class<? extends ItemBlock> getItemBlock() {
        return itemBlock;
    }

    public Item getItem() {
        return Item.getItemFromBlock(get());
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public ItemStack newItemStack() {
        return newItemStack(1);
    }

    public ItemStack newItemStack(int count) {
        return newItemStack(count, 0);
    }

    public ItemStack newItemStack(int count, int meta) {
        return new ItemStack(this.get(), count, meta);
    }
}
