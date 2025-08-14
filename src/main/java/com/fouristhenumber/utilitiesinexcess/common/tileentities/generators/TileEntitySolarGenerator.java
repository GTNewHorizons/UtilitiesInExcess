package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.solarGeneratorRFCapacity;

public class TileEntitySolarGenerator extends TileEntityBaseGenerator {

    public TileEntitySolarGenerator() {
        super(solarGeneratorRFCapacity);
    }

    @Override
    protected boolean consumeFuel() {
        return worldObj.isDaytime() && getWorldObj().canBlockSeeTheSky(xCoord, yCoord + 1, zCoord) && !canExtract();
    }

    @Override
    protected boolean canExtract() {
        return (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord));
    }

    @Override
    public void updateEntity() {
        boolean dirty = false;

        if (receiversDirty) refreshEnergyReceivers();

        if (consumeFuel()) {
            energyStorage.receiveEnergy(getRFPerTick(), false);
            dirty = true;
        }

        onBurnTick();

        if (canExtract()) pushEnergyToReceivers();

        if (dirty) markDirty();
    }

    protected int getRFPerTick() {

        // For dimensions like the end with no time, but where the sky can be seen, output constant rate
        if (worldObj.provider.hasNoSky) return 40;

        // Interpolate between 20-60
        long time = worldObj.getWorldTime();

        if (time >= 0 && time <= 6000) {
            return (int) (20 + (40.0 * time / 6000.0));
        } else {
            return (int) (60 - (40.0 * (time - 6000) / 6000.0));
        }
    }

    @Override
    protected String getGUIName() {
        return "tile.solar_generator.name";
    }
}
