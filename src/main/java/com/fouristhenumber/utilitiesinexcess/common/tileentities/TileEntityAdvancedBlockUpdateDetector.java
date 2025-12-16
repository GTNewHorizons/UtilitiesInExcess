package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockAdvancedUpdateDetector;

public class TileEntityAdvancedBlockUpdateDetector extends TileEntity {

    private boolean isProvidingPower = false;
    private int pulseTimer = 0;
    private boolean[] scanningOnFace = { true, true, true, true, true, true };
    private Block[] blockOnPreviousTick = new Block[6];

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) {
            return;
        }

        if (isProvidingPower && pulseTimer > 0) {
            pulseTimer--;
            if (pulseTimer <= 0) {
                isProvidingPower = false;
                worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
            }
        }

        for (int i = 0; i < 6; i++) {
            if (!scanningOnFace[i]) {
                continue;
            }
            Block blockOnCurrentTick = worldObj.getBlock(
                xCoord + ForgeDirection.getOrientation(i).offsetX,
                yCoord + ForgeDirection.getOrientation(i).offsetY,
                zCoord + ForgeDirection.getOrientation(i).offsetZ);
            if (blockOnCurrentTick != blockOnPreviousTick[i]
                && !(blockOnCurrentTick instanceof BlockAdvancedUpdateDetector)) {
                if (!isProvidingPower) {
                    isProvidingPower = true;
                    pulseTimer++;
                    worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
                    worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, getBlockType(), 1);
                }
            }
            blockOnPreviousTick[i] = blockOnCurrentTick;
        }
    }

    public int getOutputPower() {
        return isProvidingPower ? 15 : 0;
    }
}
