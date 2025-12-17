package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityAdvancedBlockUpdateDetector extends TileEntity {

    private boolean isProvidingPower = false;
    private int pulseTimer = 0;
    private final boolean[] scanningOnFace = { true, true, true, true, true, true };
    private final Block[] blockOnPreviousTick = new Block[6];
    private final int[] blockMetadataOnPreviousTick = new int[6];
    private final NBTTagCompound[] tileEntityNBTCompoundOnPreviousTick = new NBTTagCompound[6];

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) {
            return;
        }

        if (isProvidingPower && pulseTimer > 0) {
            pulseTimer--;
            isProvidingPower = false;
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
        }

        for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
            if (!scanningOnFace[i]) {
                continue;
            }
            ForgeDirection neighborDirection = ForgeDirection.getOrientation(i);
            Block blockOnCurrentTick = worldObj.getBlock(
                xCoord + neighborDirection.offsetX,
                yCoord + neighborDirection.offsetY,
                zCoord + neighborDirection.offsetZ);
            int blockMetadataOnCurrentTick = worldObj.getBlockMetadata(
                xCoord + neighborDirection.offsetX,
                yCoord + neighborDirection.offsetY,
                zCoord + neighborDirection.offsetZ);
            if (Objects.equals(blockOnPreviousTick[i], null)) {
                blockOnPreviousTick[i] = blockOnCurrentTick;
            } else if (blockOnCurrentTick != blockOnPreviousTick[i] && !(blockOnCurrentTick == Blocks.redstone_wire)) {
                sendRedstonePulse();
                blockOnPreviousTick[i] = blockOnCurrentTick;
                blockMetadataOnPreviousTick[i] = blockMetadataOnCurrentTick;
                break;
            } else {
                if (blockMetadataOnCurrentTick != blockMetadataOnPreviousTick[i]) {
                    sendRedstonePulse();
                    blockOnPreviousTick[i] = blockOnCurrentTick;
                    blockMetadataOnPreviousTick[i] = blockMetadataOnCurrentTick;
                    break;
                }
                blockMetadataOnPreviousTick[i] = blockMetadataOnCurrentTick;
            }
            if (blockOnCurrentTick.hasTileEntity(blockMetadataOnPreviousTick[i])) {
                if (!blockOnPreviousTick[i].hasTileEntity(blockMetadataOnPreviousTick[i])) {
                    sendRedstonePulse();
                    blockOnPreviousTick[i] = blockOnCurrentTick;
                    break;
                }
                NBTTagCompound tileEntityNBTCompoundOnCurrentTick = new NBTTagCompound();
                worldObj
                    .getTileEntity(
                        xCoord + neighborDirection.offsetX,
                        yCoord + neighborDirection.offsetY,
                        zCoord + neighborDirection.offsetZ)
                    .writeToNBT(tileEntityNBTCompoundOnCurrentTick);
                if (tileEntityNBTCompoundOnPreviousTick[i] == null) {
                    blockOnPreviousTick[i] = blockOnCurrentTick;
                    tileEntityNBTCompoundOnPreviousTick[i] = tileEntityNBTCompoundOnCurrentTick;
                } else
                    if (!Objects.equals(tileEntityNBTCompoundOnCurrentTick, tileEntityNBTCompoundOnPreviousTick[i])) {
                        sendRedstonePulse();
                        blockOnPreviousTick[i] = blockOnCurrentTick;
                        tileEntityNBTCompoundOnPreviousTick[i] = tileEntityNBTCompoundOnCurrentTick;
                        break;
                    }
                tileEntityNBTCompoundOnPreviousTick[i] = tileEntityNBTCompoundOnCurrentTick;
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
