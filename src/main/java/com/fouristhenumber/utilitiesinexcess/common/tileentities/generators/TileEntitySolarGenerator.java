package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.solarGeneratorNoSkyGeneration;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.solarGeneratorRFCapacity;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.solarGeneratorTopGeneration;

import com.fouristhenumber.utilitiesinexcess.common.blocks.generators.BlockBaseGenerator;

public class TileEntitySolarGenerator extends TileEntityBaseGenerator {

    public TileEntitySolarGenerator() {
        super(solarGeneratorRFCapacity);
    }

    @Override
    protected boolean consumeFuel() {
        if (!worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord) || canExtract()) return false;
        currentRFPerTick = getRFPerTick();
        return true;
    }

    @Override
    protected boolean canExtract() {
        return (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord));
    }

    @Override
    public void updateEntity() {
        if (multiplier == -1) {
            if (worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockBaseGenerator generator) {
                multiplier = generator.multiplier;
            }
        }

        if (worldObj.isRemote) return;
        boolean dirty = false;

        if (receiversDirty) refreshEnergyReceivers();

        if (consumeFuel()) {
            energyStorage.receiveEnergy(currentRFPerTick * multiplier, false);
            dirty = true;
        }

        onBurnTick();

        if (canExtract()) pushEnergyToReceivers();

        if (dirty) markDirty();
    }

    protected int getRFPerTick() {
        // For dimensions like the end with no time, but where the sky can be seen, output constant rate
        if (worldObj.provider.hasNoSky) return solarGeneratorNoSkyGeneration;

        return (int) interpolate((int) (((worldObj.getWorldTime() % 24000) + 24000) % 24000));
    }

    public static double interpolate(int time) {
        if (time >= 14095 && time <= 21905) {
            return 0.0;
        } else if (time >= 4925 && time <= 7075) {
            return solarGeneratorTopGeneration;
        } else if (time > 7075 && time < 14095) {
            double t = (double) (time - 7075) / (14095 - 7075);
            return solarGeneratorTopGeneration * (1 - t);
        } else {
            double start = 21905;
            double end = 24000 + 4925;
            double xWrapped = (time < 4925) ? time + 24000 : time;

            double t = (xWrapped - start) / (end - start);
            return solarGeneratorTopGeneration * t;
        }
    }

    @Override
    protected String getGUIName() {
        return "tile.solar_generator.name";
    }

    @Override
    protected boolean showBurnTime() {
        return false;
    }
}
