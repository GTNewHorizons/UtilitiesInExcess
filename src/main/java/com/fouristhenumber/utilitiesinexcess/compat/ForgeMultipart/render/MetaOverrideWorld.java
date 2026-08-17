package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public record MetaOverrideWorld(IBlockAccess world, int x, int y, int z, int meta) implements IBlockAccess {

    @Override
    public Block getBlock(int x, int y, int z) {
        return world.getBlock(x, y, z);
    }

    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        return world.getTileEntity(x, y, z);
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int _default) {
        return world.getLightBrightnessForSkyBlocks(x, y, z, _default);
    }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
        if (x == this.x && y == this.y && z == this.z) {
            return meta;
        }
        return world.getBlockMetadata(x, y, z);
    }

    @Override
    public int isBlockProvidingPowerTo(int x, int y, int z, int directionIn) {
        return world.isBlockProvidingPowerTo(x, y, z, directionIn);
    }

    @Override
    public boolean isAirBlock(int x, int y, int z) {
        return world.isAirBlock(x, y, z);
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(int x, int z) {
        return world.getBiomeGenForCoords(x, y);
    }

    @Override
    public int getHeight() {
        return world.getHeight();
    }

    @Override
    public boolean extendedLevelsInChunkCache() {
        return world.extendedLevelsInChunkCache();
    }

    @Override
    public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
        return world.isSideSolid(x, y, z, side, _default);
    }
}
