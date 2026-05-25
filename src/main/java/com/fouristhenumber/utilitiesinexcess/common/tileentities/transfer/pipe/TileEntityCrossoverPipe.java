package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe;

import cofh.api.energy.IEnergyReceiver;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityNetworkComponentBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.CrossOverPipeLogic;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityCrossoverPipe extends TileEntityNetworkComponentBase<CrossOverPipeLogic>
{
    public TileEntityCrossoverPipe()
    {
        logic = new CrossOverPipeLogic(this);
    }

    @Override
    public boolean canConnectToSide(ForgeDirection side)
    {
        ForgeDirection oppositeSide = side.getOpposite();

        int xOffset = xCoord + oppositeSide.offsetX;
        int yOffset = yCoord + oppositeSide.offsetY;
        int zOffset = zCoord + oppositeSide.offsetZ;
        if (worldObj.blockExists(xOffset, yOffset, zOffset))
        {
            TileEntity candidateTE = worldObj.getChunkFromBlockCoords(xOffset, zOffset).getTileEntityUnsafe(xOffset & 15, yOffset, zOffset & 15);
            if (candidateTE instanceof TileEntityNetworkComponentBase<?> networkableTE)
            {
                return networkableTE.canConnectToSide(side);
            }
            return candidateTE instanceof IInventory || candidateTE instanceof IFluidHandler || candidateTE instanceof IEnergyReceiver;
        }
        return false;
    }
}
