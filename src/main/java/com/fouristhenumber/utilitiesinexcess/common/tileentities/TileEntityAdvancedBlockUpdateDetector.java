package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockAdvancedUpdateDetector;

public class TileEntityAdvancedBlockUpdateDetector extends TileEntity {

    private boolean isProvidingPower = false;
    private int pulseTimer = 0;
    private boolean[] scanningOnFace = { true, true, true, true, true, true };
    private Block[] blockOnPreviousTick = new Block[6];
    private int[] blockMetadataOnPreviousTick = new int[6];
    private String[] tileEntityNBTSerializedOnPreviousTick = new String[6];

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
            if (blockOnPreviousTick[i] == null) {
                blockOnPreviousTick[i] = blockOnCurrentTick;
            } else if (blockOnCurrentTick != blockOnPreviousTick[i]
                && !(blockOnCurrentTick instanceof BlockAdvancedUpdateDetector)
                && !(blockOnCurrentTick == Blocks.redstone_wire)) {
                    sendRedstonePulse();
                    blockOnPreviousTick[i] = blockOnCurrentTick;
                    break;
                } else {
                    int blockMetadataOnCurrentTick = worldObj.getBlockMetadata(
                        xCoord + ForgeDirection.getOrientation(i).offsetX,
                        yCoord + ForgeDirection.getOrientation(i).offsetY,
                        zCoord + ForgeDirection.getOrientation(i).offsetZ);
                    if (blockMetadataOnCurrentTick != blockMetadataOnPreviousTick[i]) {
                        sendRedstonePulse();
                        blockOnPreviousTick[i] = blockOnCurrentTick;
                        blockMetadataOnPreviousTick[i] = blockMetadataOnCurrentTick;
                        break;
                    }
                    blockMetadataOnPreviousTick[i] = blockMetadataOnCurrentTick;
                }
            if (blockOnCurrentTick.hasTileEntity(0)) {
                if (blockOnPreviousTick[i] == null) {
                    blockOnPreviousTick[i] = blockOnCurrentTick;
                } else if (!blockOnPreviousTick[i].hasTileEntity(0)) {
                    sendRedstonePulse();
                    blockOnPreviousTick[i] = blockOnCurrentTick;
                    break;
                }
                NBTTagCompound compound = new NBTTagCompound();
                worldObj
                    .getTileEntity(
                        xCoord + ForgeDirection.getOrientation(i).offsetX,
                        yCoord + ForgeDirection.getOrientation(i).offsetY,
                        zCoord + ForgeDirection.getOrientation(i).offsetZ)
                    .writeToNBT(compound);
                String tileEntityNBTSerializedOnCurrentTick = compound.toString();
                if (tileEntityNBTSerializedOnPreviousTick[i] == null) {
                    blockOnPreviousTick[i] = blockOnCurrentTick;
                    tileEntityNBTSerializedOnPreviousTick[i] = tileEntityNBTSerializedOnCurrentTick;
                } else if (!Objects
                    .equals(tileEntityNBTSerializedOnCurrentTick, tileEntityNBTSerializedOnPreviousTick[i])) {
                        sendRedstonePulse();
                        blockOnPreviousTick[i] = blockOnCurrentTick;
                        tileEntityNBTSerializedOnPreviousTick[i] = tileEntityNBTSerializedOnCurrentTick;
                        break;
                    }
                tileEntityNBTSerializedOnPreviousTick[i] = tileEntityNBTSerializedOnCurrentTick;
            }
            blockOnPreviousTick[i] = blockOnCurrentTick;
        }
    }

    private void sendRedstonePulse() {
        if (!isProvidingPower) {
            isProvidingPower = true;
            pulseTimer++;
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
            worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, getBlockType(), 1);
        }
    }

    public int getOutputPower() {
        return isProvidingPower ? 15 : 0;
    }
}
