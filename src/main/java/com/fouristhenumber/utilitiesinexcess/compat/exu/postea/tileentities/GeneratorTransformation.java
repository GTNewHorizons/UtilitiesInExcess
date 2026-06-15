package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
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
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.DummyBlock;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.IPosteaTransformation;
import com.fouristhenumber.utilitiesinexcess.utils.UIEUtils;
import com.gtnewhorizons.postea.api.IDExtenderCompat;
import com.gtnewhorizons.postea.api.ItemStackReplacementManager;
import com.gtnewhorizons.postea.api.TileEntityReplacementManager;
import com.gtnewhorizons.postea.utility.BlockInfo;
import com.gtnewhorizons.postea.utility.PosteaUtilities;

import cpw.mods.fml.common.registry.GameRegistry;

public class GeneratorTransformation implements IPosteaTransformation {

    private final Block dummyBlock1;
    private final Block dummyBlock8;
    private final Block dummyBlock64;

    private static final String dummyName1 = "dummy_generator1x";
    private static final String dummyName8 = "dummy_generator8x";
    private static final String dummyName64 = "dummy_generator64x";

    public GeneratorTransformation() {
        dummyBlock1 = new DummyBlock(Material.ground);
        dummyBlock8 = new DummyBlock(Material.ground);
        dummyBlock64 = new DummyBlock(Material.ground);
    }

    @Override
    public void registerDummies() {
        GameRegistry.registerBlock(dummyBlock1, dummyName1);
        GameRegistry.registerBlock(dummyBlock8, dummyName8);
        GameRegistry.registerBlock(dummyBlock64, dummyName64);
        UIEUtils.hideInNei(dummyBlock1);
        UIEUtils.hideInNei(dummyBlock8);
        UIEUtils.hideInNei(dummyBlock64);
    }

    @Override
    public void addItemRemappings(Map<String, Item> remappings) {
        remappings.put("ExtraUtilities:generator", Item.getItemFromBlock(dummyBlock1));
        remappings.put("ExtraUtilities:generator.8", Item.getItemFromBlock(dummyBlock8));
        remappings.put("ExtraUtilities:generator.64", Item.getItemFromBlock(dummyBlock64));
    }

    @Override
    public void addBlockRemappings(Map<String, Block> remappings) {
        remappings.put("ExtraUtilities:generator", dummyBlock1);
        remappings.put("ExtraUtilities:generator.8", dummyBlock8);
        remappings.put("ExtraUtilities:generator.64", dummyBlock64);
    }

    @Override
    public void registerTransformations() {
        ItemStackReplacementManager
            .addItemReplacement(UtilitiesInExcess.MODID + ":" + dummyName1, (tag) -> this.doItemTransformation(tag, 1));
        ItemStackReplacementManager
            .addItemReplacement(UtilitiesInExcess.MODID + ":" + dummyName8, (tag) -> this.doItemTransformation(tag, 8));
        ItemStackReplacementManager.addItemReplacement(
            UtilitiesInExcess.MODID + ":" + dummyName64,
            (tag) -> this.doItemTransformation(tag, 64));
        TileEntityReplacementManager.tileEntityTransformer(
            "extrautils:generatorstone",
            (tag, world, chunk) -> this.doGeneratorTransformation(0, tag, world, chunk));
        TileEntityReplacementManager.tileEntityTransformer(
            "extrautils:generatorbase",
            (tag, world, chunk) -> this.doGeneratorTransformation(1, tag, world, chunk));
        TileEntityReplacementManager.tileEntityTransformer(
            "extrautils:generatorlava",
            (tag, world, chunk) -> this.doGeneratorTransformation(2, tag, world, chunk));
        TileEntityReplacementManager.tileEntityTransformer(
            "extrautils:generatorender",
            (tag, world, chunk) -> this.doGeneratorTransformation(3, tag, world, chunk));
        TileEntityReplacementManager.tileEntityTransformer(
            "extrautils:generatorredflux",
            (tag, world, chunk) -> this.doGeneratorTransformation(4, tag, world, chunk));
        TileEntityReplacementManager.tileEntityTransformer(
            "extrautils:generatorfood",
            (tag, world, chunk) -> this.doGeneratorTransformation(5, tag, world, chunk));
        TileEntityReplacementManager.tileEntityTransformer(
            "extrautils:generatorpotion",
            (tag, world, chunk) -> this.doGeneratorTransformation(6, tag, world, chunk));
        TileEntityReplacementManager.tileEntityTransformer(
            "extrautils:generatorsolar",
            (tag, world, chunk) -> this.doGeneratorTransformation(7, tag, world, chunk));
        TileEntityReplacementManager.tileEntityTransformer(
            "extrautils:generatortnt",
            (tag, world, chunk) -> this.doGeneratorTransformation(8, tag, world, chunk));
        TileEntityReplacementManager.tileEntityTransformer(
            "extrautils:generatorpink",
            (tag, world, chunk) -> this.doGeneratorTransformation(9, tag, world, chunk));
        TileEntityReplacementManager.tileEntityTransformer(
            "extrautils:generatoroverclocked",
            (tag, world, chunk) -> this.doGeneratorTransformation(10, tag, world, chunk));
        TileEntityReplacementManager.tileEntityTransformer(
            "extrautils:generatornether",
            (tag, world, chunk) -> this.doGeneratorTransformation(11, tag, world, chunk));
    }

    public NBTTagCompound doItemTransformation(NBTTagCompound tag, int mult) {
        int dmg = tag.getInteger("Damage");
        ModBlocks block = getGeneratorFromMetaAndMult(dmg, mult);
        IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(block.getItem()));
        tag.setInteger("Damage", 0);
        return tag;
    }

    public BlockInfo doGeneratorTransformation(int meta, NBTTagCompound originalTag, World world, Chunk chunk) {
        Block block = chunk
            .getBlock(originalTag.getInteger("x") & 15, originalTag.getInteger("y"), originalTag.getInteger("z") & 15);

        int mult = 1;
        if (block == dummyBlock8) {
            mult = 8;
        } else if (block == dummyBlock64) {
            mult = 64;
        }

        Block newBlock = getGeneratorFromMetaAndMult(meta, mult).get();

        return new BlockInfo(newBlock, 0, (oldTag) -> {
            NBTTagCompound tag = PosteaUtilities.cleanseNBT(getGeneratorTEID(newBlock), oldTag);
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
                        // If we know that the generator is running, but we don't know at what rf/t
                        // Add an extra item to the input stack
                        item.setInteger("Count", item.getInteger("Count") + 1);
                    }

                    tag.setTag(stackName, item);
                }
            }

            if (oldTag.hasKey("Tank_0")) {
                NBTTagCompound tank = oldTag.getCompoundTag("Tank_0");
                int amount = tank.getInteger("Amount");
                if (amount > 0 && rfPerTick == 0 && burnTime != 0) {
                    // If we know that the generator is running, but we don't know at what rf/t
                    // Add an extra bucket to the input fluid
                    tank.setInteger("Amount", Math.min(amount + 1000, 4000));
                }
                tag.setTag("Fluid", tank);
            }
            return tag;
        });
    }

    public static ModBlocks getGeneratorFromMetaAndMult(int meta, int mult) {
        return switch (meta) {
            case 0 -> switch (mult) {
                    case 64 -> ModBlocks.LOW_TEMPERATURE_FURNACE_GENERATOR_PLUSPLUS;
                    case 8 -> ModBlocks.LOW_TEMPERATURE_FURNACE_GENERATOR_PLUS;
                    default -> ModBlocks.LOW_TEMPERATURE_FURNACE_GENERATOR;
                };
            case 1 -> switch (mult) {
                    case 64 -> ModBlocks.FURNACE_GENERATOR_PLUSPLUS;
                    case 8 -> ModBlocks.FURNACE_GENERATOR_PLUS;
                    default -> ModBlocks.FURNACE_GENERATOR;
                };
            case 2 -> switch (mult) {
                    case 64 -> ModBlocks.LAVA_GENERATOR_PLUSPLUS;
                    case 8 -> ModBlocks.LAVA_GENERATOR_PLUS;
                    default -> ModBlocks.LAVA_GENERATOR;
                };
            case 3 -> switch (mult) {
                    case 64 -> ModBlocks.ENDER_GENERATOR_PLUSPLUS;
                    case 8 -> ModBlocks.ENDER_GENERATOR_PLUS;
                    default -> ModBlocks.ENDER_GENERATOR;
                };
            case 4 -> switch (mult) {
                    case 64 -> ModBlocks.REDSTONE_GENERATOR_PLUSPLUS;
                    case 8 -> ModBlocks.REDSTONE_GENERATOR_PLUS;
                    default -> ModBlocks.REDSTONE_GENERATOR;
                };
            case 5 -> switch (mult) {
                    case 64 -> ModBlocks.FOOD_GENERATOR_PLUSPLUS;
                    case 8 -> ModBlocks.FOOD_GENERATOR_PLUS;
                    default -> ModBlocks.FOOD_GENERATOR;
                };
            case 6 -> switch (mult) {
                    case 64 -> ModBlocks.POTION_GENERATOR_PLUSPLUS;
                    case 8 -> ModBlocks.POTION_GENERATOR_PLUS;
                    default -> ModBlocks.POTION_GENERATOR;
                };
            case 7 -> switch (mult) {
                    case 64 -> ModBlocks.SOLAR_GENERATOR_PLUSPLUS;
                    case 8 -> ModBlocks.SOLAR_GENERATOR_PLUS;
                    default -> ModBlocks.SOLAR_GENERATOR;
                };
            case 8 -> switch (mult) {
                    case 64 -> ModBlocks.TNT_GENERATOR_PLUSPLUS;
                    case 8 -> ModBlocks.TNT_GENERATOR_PLUS;
                    default -> ModBlocks.TNT_GENERATOR;
                };
            case 9 -> switch (mult) {
                    case 64 -> ModBlocks.PINK_GENERATOR_PLUSPLUS;
                    case 8 -> ModBlocks.PINK_GENERATOR_PLUS;
                    default -> ModBlocks.PINK_GENERATOR;
                };
            case 10 -> switch (mult) {
                    case 64 -> ModBlocks.HIGH_TEMPERATURE_FURNACE_GENERATOR_PLUSPLUS;
                    case 8 -> ModBlocks.HIGH_TEMPERATURE_FURNACE_GENERATOR_PLUS;
                    default -> ModBlocks.HIGH_TEMPERATURE_FURNACE_GENERATOR;
                };
            case 11 -> switch (mult) {
                    case 64 -> ModBlocks.NETHER_STAR_GENERATOR_PLUSPLUS;
                    case 8 -> ModBlocks.NETHER_STAR_GENERATOR_PLUS;
                    default -> ModBlocks.NETHER_STAR_GENERATOR;
                };

            default -> throw new IllegalStateException("No defined generator with EXU meta: " + meta);
        };
    }

    // TODO replace this once malteez's quarry PR gets merged with the TEID enum
    private static String getGeneratorTEID(Block block) {
        if (block instanceof BlockEnderGenerator) {
            return "TileEntityEnderGeneratorUIE";
        } else if (block instanceof BlockFoodGenerator) {
            return "TileEntityFoodGeneratorUIE";
        } else if (block instanceof BlockFurnaceGenerator) {
            return "TileEntityFurnaceGeneratorUIE";
        } else if (block instanceof BlockHighTemperatureFurnaceGenerator) {
            return "TileEntityHighTemperatureFurnaceGenerator";
        } else if (block instanceof BlockLavaGenerator) {
            return "TileEntityLowTemperatureFurnaceGeneratorUIE";
        } else if (block instanceof BlockLowTemperatureFurnaceGenerator) {
            return "TileEntityNetherStarGeneratorUIE";
        } else if (block instanceof BlockNetherStarGenerator) {
            return "TileEntityPinkGeneratorUIE";
        } else if (block instanceof BlockPinkGenerator) {
            return "TileEntityPotionGeneratorUIE";
        } else if (block instanceof BlockPotionGenerator) {
            return "TileEntityRedstoneGeneratorUIE";
        } else if (block instanceof BlockRedstoneGenerator) {
            return "TileEntitySolarGeneratorUIE";
        } else if (block instanceof BlockSolarGenerator) {
            return "TileEntityTNTGeneratorUIE";
        }

        return "";
    }
}
