package com.fouristhenumber.utilitiesinexcess.common.dimensions.endoftime;

import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import com.fouristhenumber.utilitiesinexcess.common.dimensions.UIEWorldChunkManager;
import com.fouristhenumber.utilitiesinexcess.config.dimensions.EndOfTimeConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WorldProviderEndOfTime extends WorldProvider {

    @Override
    public void registerWorldChunkManager() {
        this.worldChunkMgr = new UIEWorldChunkManager(BiomeGenBase.getBiome(EndOfTimeConfig.defaultBiomeId));
        this.dimensionId = EndOfTimeConfig.endOfTimeDimensionId;
        this.hasNoSky = true;
    }

    @Override
    public IChunkProvider createChunkGenerator() {
        return new ChunkProviderEndOfTime(this.worldObj);
    }

    @Override
    public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_) {
        return 0.0F;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float[] calcSunriseSunsetColors(float p_76560_1_, float p_76560_2_) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isSkyColored() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public Vec3 getSkyColor(Entity p_72833_1_, float p_72833_2_) {
        return Vec3.createVectorHelper(0, 0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3 getFogColor(float p_76562_1_, float p_76562_2_) {
        return Vec3.createVectorHelper(0, 0, 0);
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getCloudHeight() {
        return 8.0F;
    }

    @Override
    public boolean canCoordinateBeSpawn(int x, int z) {
        return this.worldObj.getTopBlock(x, z)
            .getMaterial()
            .blocksMovement();
    }

    @Override
    public ChunkCoordinates getEntrancePortalLocation() {
        return DimensionPortalData.get(this.worldObj)
            .getTarget();
    }

    @Override
    public int getAverageGroundLevel() {
        return 64;
    }

    @Override
    public int getActualHeight() {
        return 256;
    }

    @Override
    public String getDimensionName() {
        return "The End of Time";
    }

}
