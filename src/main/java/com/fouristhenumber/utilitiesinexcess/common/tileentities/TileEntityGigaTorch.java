package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.tileentity.TileEntity;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;

public class TileEntityGigaTorch extends TileEntity {

    @Override
    public boolean canUpdate() {
        return false;
    }

    // placed or loaded from chunk
    @Override
    public void validate() {
        super.validate();
        UtilitiesInExcess.proxy.mobSpawnBlockChecks
            .put(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, BlockConfig.gigaTorch.gigaTorchRange);
    }

    // removed or unloaded
    @Override
    public void invalidate() {
        UtilitiesInExcess.proxy.mobSpawnBlockChecks.remove(worldObj.provider.dimensionId, xCoord, yCoord, zCoord);
        super.invalidate();
    }
}
