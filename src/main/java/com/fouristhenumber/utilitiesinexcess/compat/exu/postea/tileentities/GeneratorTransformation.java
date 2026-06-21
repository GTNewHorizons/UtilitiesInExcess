package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.blocks.generators.BlockRedstoneGenerator;
import com.gtnewhorizons.postea.api.BlockAccessCompat;
import com.gtnewhorizons.postea.api.BlockReplacementManager;
import com.gtnewhorizons.postea.api.TileEntityReplacementManager;
import com.gtnewhorizons.postea.utility.BlockInfo;
import com.gtnewhorizons.postea.utility.PosteaUtilities;

public class GeneratorTransformation {

    private static final String[] GENERATOR_TE_IDS = { "TileEntityLowTemperatureFurnaceGeneratorUIE",
        "TileEntityFurnaceGeneratorUIE", "TileEntityLavaGeneratorUIE", "TileEntityEnderGeneratorUIE",
        "TileEntityRedstoneGeneratorUIE", "TileEntityFoodGeneratorUIE", "TileEntityPotionGeneratorUIE",
        "TileEntitySolarGeneratorUIE", "TileEntityTNTGeneratorUIE", "TileEntityPinkGeneratorUIE",
        "TileEntityHighTemperatureFurnaceGeneratorUIE", "TileEntityNetherStarGeneratorUIE" };

    private static final String[] GENERATOR_SOURCE_TE_IDS = { "extrautils:generatorstone", "extrautils:generatorbase",
        "extrautils:generatorlava", "extrautils:generatorender", "extrautils:generatorredflux",
        "extrautils:generatorfood", "extrautils:generatorpotion", "extrautils:generatorsolar",
        "extrautils:generatortnt", "extrautils:generatorpink", "extrautils:generatoroverclocked",
        "extrautils:generatornether" };

    private static int generator1xId = -1;
    private static int generator8xId = -1;
    private static int generator64xId = -1;

    public static void postLoad() {
        BlockReplacementManager.registerIDResolver("ExtraUtilities:generator", id -> generator1xId = id);
        BlockReplacementManager.registerIDResolver("ExtraUtilities:generator.8", id -> generator8xId = id);
        BlockReplacementManager.registerIDResolver("ExtraUtilities:generator.64", id -> generator64xId = id);

        for (int meta = 0; meta < GENERATOR_SOURCE_TE_IDS.length; meta++) {
            final int type = meta;
            TileEntityReplacementManager.tileEntityTransformer(
                GENERATOR_SOURCE_TE_IDS[meta],
                (tag, world, chunk) -> transform(type, tag, chunk));
        }
    }

    private static BlockInfo transform(int meta, NBTTagCompound originalTag, Chunk chunk) {
        int blockId = BlockAccessCompat.getBlockIDAtTE(originalTag, chunk);

        final int mult;
        if (blockId == generator64xId) mult = 64;
        else if (blockId == generator8xId) mult = 8;
        else if (blockId == generator1xId) mult = 1;
        else return null;

        Block newBlock = getGeneratorFromMetaAndMult(meta, mult);

        return new BlockInfo(newBlock, 0, (oldTag) -> {
            NBTTagCompound tag = PosteaUtilities.cleanseNBT(GENERATOR_TE_IDS[meta], oldTag);
            tag.setInteger("Energy", oldTag.getInteger("Energy"));
            int burnTime = (int) oldTag.getDouble("coolDown");
            int rfPerTick = oldTag.getInteger("curLevel");
            tag.setInteger("BurnTime", rfPerTick == 0 ? 0 : burnTime);
            tag.setInteger("CurrentRFPerTick", rfPerTick);
            if (oldTag.hasKey("items")) {
                NBTTagCompound items = oldTag.getCompoundTag("items");
                if (items.hasKey("item_0")) {
                    String stackName = newBlock instanceof BlockRedstoneGenerator ? "RedstoneStack" : "FuelStack";
                    NBTTagCompound item = items.getCompoundTag("item_0");
                    if (rfPerTick == 0 && burnTime != 0) {
                        item.setInteger("Count", item.getInteger("Count") + 1);
                    }
                    tag.setTag(stackName, item);
                }
            }
            if (oldTag.hasKey("Tank_0")) {
                NBTTagCompound tank = oldTag.getCompoundTag("Tank_0");
                int amount = tank.getInteger("Amount");
                if (amount > 0 && rfPerTick == 0 && burnTime != 0) {
                    tank.setInteger("Amount", Math.min(amount + 1000, 4000));
                }
                tag.setTag("Fluid", tank);
            }
            return tag;
        });
    }

    private static Block getGeneratorFromMetaAndMult(int meta, int mult) {
        return switch (meta) {
            case 0 -> switch (mult) {
                    case 64 -> ModBlocks.LOW_TEMPERATURE_FURNACE_GENERATOR_PLUSPLUS.get();
                    case 8 -> ModBlocks.LOW_TEMPERATURE_FURNACE_GENERATOR_PLUS.get();
                    default -> ModBlocks.LOW_TEMPERATURE_FURNACE_GENERATOR.get();
                };
            case 1 -> switch (mult) {
                    case 64 -> ModBlocks.FURNACE_GENERATOR_PLUSPLUS.get();
                    case 8 -> ModBlocks.FURNACE_GENERATOR_PLUS.get();
                    default -> ModBlocks.FURNACE_GENERATOR.get();
                };
            case 2 -> switch (mult) {
                    case 64 -> ModBlocks.LAVA_GENERATOR_PLUSPLUS.get();
                    case 8 -> ModBlocks.LAVA_GENERATOR_PLUS.get();
                    default -> ModBlocks.LAVA_GENERATOR.get();
                };
            case 3 -> switch (mult) {
                    case 64 -> ModBlocks.ENDER_GENERATOR_PLUSPLUS.get();
                    case 8 -> ModBlocks.ENDER_GENERATOR_PLUS.get();
                    default -> ModBlocks.ENDER_GENERATOR.get();
                };
            case 4 -> switch (mult) {
                    case 64 -> ModBlocks.REDSTONE_GENERATOR_PLUSPLUS.get();
                    case 8 -> ModBlocks.REDSTONE_GENERATOR_PLUS.get();
                    default -> ModBlocks.REDSTONE_GENERATOR.get();
                };
            case 5 -> switch (mult) {
                    case 64 -> ModBlocks.FOOD_GENERATOR_PLUSPLUS.get();
                    case 8 -> ModBlocks.FOOD_GENERATOR_PLUS.get();
                    default -> ModBlocks.FOOD_GENERATOR.get();
                };
            case 6 -> switch (mult) {
                    case 64 -> ModBlocks.POTION_GENERATOR_PLUSPLUS.get();
                    case 8 -> ModBlocks.POTION_GENERATOR_PLUS.get();
                    default -> ModBlocks.POTION_GENERATOR.get();
                };
            case 7 -> switch (mult) {
                    case 64 -> ModBlocks.SOLAR_GENERATOR_PLUSPLUS.get();
                    case 8 -> ModBlocks.SOLAR_GENERATOR_PLUS.get();
                    default -> ModBlocks.SOLAR_GENERATOR.get();
                };
            case 8 -> switch (mult) {
                    case 64 -> ModBlocks.TNT_GENERATOR_PLUSPLUS.get();
                    case 8 -> ModBlocks.TNT_GENERATOR_PLUS.get();
                    default -> ModBlocks.TNT_GENERATOR.get();
                };
            case 9 -> switch (mult) {
                    case 64 -> ModBlocks.PINK_GENERATOR_PLUSPLUS.get();
                    case 8 -> ModBlocks.PINK_GENERATOR_PLUS.get();
                    default -> ModBlocks.PINK_GENERATOR.get();
                };
            case 10 -> switch (mult) {
                    case 64 -> ModBlocks.HIGH_TEMPERATURE_FURNACE_GENERATOR_PLUSPLUS.get();
                    case 8 -> ModBlocks.HIGH_TEMPERATURE_FURNACE_GENERATOR_PLUS.get();
                    default -> ModBlocks.HIGH_TEMPERATURE_FURNACE_GENERATOR.get();
                };
            case 11 -> switch (mult) {
                    case 64 -> ModBlocks.NETHER_STAR_GENERATOR_PLUSPLUS.get();
                    case 8 -> ModBlocks.NETHER_STAR_GENERATOR_PLUS.get();
                    default -> ModBlocks.NETHER_STAR_GENERATOR.get();
                };
            default -> throw new IllegalStateException("No defined generator with EXU meta: " + meta);
        };
    }
}
