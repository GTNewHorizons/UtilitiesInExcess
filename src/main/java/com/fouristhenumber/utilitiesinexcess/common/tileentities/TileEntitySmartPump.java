package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.uieInstance;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidHandler;

import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;

public class TileEntitySmartPump extends TileEntity implements IEnergyReceiver, IFluidHandler {

    private ForgeChunkManager.Ticket ticket;

    protected EnergyStorage energyStorage = new EnergyStorage(BlockConfig.smartPumpEnergyStorage);

    boolean stalled = false;
    boolean finished = false;

    private final int chunkX;
    private final int chunkZ;

    private byte xInChunk = 0;
    private byte zInChunk = 0;

    private int currentY = Integer.MIN_VALUE;

    private byte currentChunk = 0;

    FluidTank tank = new FluidTank(1000);

    private static final int[][] CHUNK_OFFSETS = { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 }, { 0, 2 },
        { 1, 1 }, { 2, 0 }, { 1, -1 }, { 0, -2 }, { -1, -1 }, { -2, 0 }, { -1, 1 }, { 0, 3 }, { 1, 2 }, { 2, 1 },
        { 3, 0 }, { 2, -1 }, { 1, -2 }, { 0, -3 }, { -1, -2 }, { -2, -1 }, { -3, 0 }, { -2, 1 }, { -1, 2 }, { 0, 4 },
        { 1, 3 }, { 2, 2 }, { 3, 1 }, { 4, 0 }, { 3, -1 }, { 2, -2 }, { 1, -3 }, { 0, -4 }, { -1, -3 }, { -2, -2 },
        { -3, -1 }, { -4, 0 }, { -3, 1 }, { -2, 2 }, { -1, 3 }, { 0, 5 }, { 1, 4 }, { 2, 3 }, { 3, 2 }, { 4, 1 },
        { 5, 0 }, { 4, -1 }, { 3, -2 }, { 2, -3 }, { 1, -4 }, { 0, -5 }, { -1, -4 }, { -2, -3 }, { -3, -2 }, { -4, -1 },
        { -5, 0 }, { -4, 1 }, { -3, 2 }, { -2, 3 }, { -1, 4 } };

    public TileEntitySmartPump() {
        chunkX = xCoord >> 4;
        chunkZ = zCoord >> 4;
    }

    @Override
    public void updateEntity() {
        if (worldObj.isRemote || finished) return;

        if (ticket == null) {
            requestTicket();
            ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(chunkX, chunkZ));
        }

        if (currentY == Integer.MIN_VALUE) {
            currentY = yCoord - 1;
        }
        if (!stalled || worldObj.getTotalWorldTime() % BlockConfig.smartPumpStallCooldownInTicks == 0) {
            stalled = false;

            if (tank.getFluidAmount() > 0) {
                if (worldObj.getTileEntity(xCoord, yCoord + 1, zCoord) instanceof IFluidHandler fluidHandler) {
                    int canDrain = fluidHandler.fill(ForgeDirection.DOWN, tank.getFluid(), false);
                    fluidHandler.fill(
                        ForgeDirection.DOWN,
                        drain(
                            ForgeDirection.UP,
                            new FluidStack(
                                tank.getFluid()
                                    .getFluid(),
                                canDrain),
                            true),
                        true);
                }
            }

            scanStep();
        }
    }

    private void scanStep() {
        int worldX = ((chunkX + CHUNK_OFFSETS[currentChunk][0]) * 16) + xInChunk;
        int worldZ = ((chunkZ + CHUNK_OFFSETS[currentChunk][1]) * 16) + zInChunk;

        Block block = worldObj.getBlock(worldX, currentY, worldZ);
        FluidStack fluid;

        if (getEnergyStored(ForgeDirection.UNKNOWN) < BlockConfig.smartPumpEnergyUsePerBlock) {
            stalled = true;
            return;
        }

        // Gotta hardcode the vanilla ones ugh
        if (block == Blocks.water) {
            fluid = new FluidStack(FluidRegistry.WATER, 1000);
        } else if (block == Blocks.lava) {
            fluid = new FluidStack(FluidRegistry.LAVA, 1000);
        } else if (block instanceof IFluidBlock fluidBlock) {
            fluid = fluidBlock.drain(worldObj, worldX, currentY, worldZ, false);
        } else {
            advanceColumn();
            return;
        }

        energyStorage.extractEnergy(BlockConfig.smartPumpEnergyUsePerBlock, true);

        if (tank.fill(fluid, false) >= fluid.amount) {
            tank.fill(fluid, true);

            worldObj.setBlock(worldX, currentY, worldZ, Blocks.stone, 1, 2);
            currentY--;

            if (currentY < 0) {
                advanceColumn();
            }
        } else {
            stalled = true;
        }
    }

    private void advanceColumn() {
        currentY = yCoord - 1;

        xInChunk++;

        if (xInChunk >= 16) {
            xInChunk = 0;
            zInChunk++;

            if (zInChunk >= 16) {

                // Release old chunk
                ForgeChunkManager.unforceChunk(
                    ticket,
                    new ChunkCoordIntPair(
                        chunkX + CHUNK_OFFSETS[currentChunk][0],
                        chunkZ + CHUNK_OFFSETS[currentChunk][1]));

                if (currentChunk < CHUNK_OFFSETS.length - 1) {
                    zInChunk = 0;
                    currentChunk++;

                    // Load new chunk
                    ForgeChunkManager.forceChunk(
                        ticket,
                        new ChunkCoordIntPair(
                            chunkX + CHUNK_OFFSETS[currentChunk][0],
                            chunkZ + CHUNK_OFFSETS[currentChunk][1]));

                } else {
                    finished = true;

                    // Release own chunk
                    ForgeChunkManager.unforceChunk(ticket, new ChunkCoordIntPair(chunkX, chunkZ));
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setBoolean("finished", finished);
        tag.setByte("xInChunk", xInChunk);
        tag.setByte("zInChunk", zInChunk);
        tag.setInteger("currentY", currentY);
        tag.setByte("currentChunk", currentChunk);

        energyStorage.writeToNBT(tag);
        tank.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        finished = tag.getBoolean("finished");
        xInChunk = tag.getByte("xInChunk");
        zInChunk = tag.getByte("zInChunk");
        currentY = tag.getInteger("currentY");
        currentChunk = tag.getByte("currentChunk");

        energyStorage.readFromNBT(tag);
        tank.readFromNBT(tag);
    }

    // Chunkloading stuff
    private void requestTicket() {
        ticket = ForgeChunkManager.requestTicket(uieInstance, worldObj, ForgeChunkManager.Type.NORMAL);

        if (ticket != null) {
            NBTTagCompound tag = ticket.getModData();
            tag.setInteger("teX", xCoord);
            tag.setInteger("teY", yCoord);
            tag.setInteger("teZ", zCoord);
        }
    }

    // Called when world reloads
    public void receiveTicketOnLoad(ForgeChunkManager.Ticket t) {
        this.ticket = t;
        if (!finished) {
            ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(chunkX, chunkZ));
            ForgeChunkManager.forceChunk(
                ticket,
                new ChunkCoordIntPair(
                    chunkX + CHUNK_OFFSETS[currentChunk][0],
                    chunkZ + CHUNK_OFFSETS[currentChunk][1]));
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        unload();
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        unload();
    }

    private void unload() {
        if (ticket != null) {
            ForgeChunkManager.releaseTicket(ticket);
            ticket = null;
        }
    }

    // IEnergyReceiver
    @Override
    public int receiveEnergy(ForgeDirection forgeDirection, int i, boolean b) {
        return energyStorage.receiveEnergy(i, b);
    }

    @Override
    public int getEnergyStored(ForgeDirection forgeDirection) {
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection forgeDirection) {
        return energyStorage.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection forgeDirection) {
        return true;
    }

    // IFluidHandler
    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
            return null;
        }
        return tank.drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { tank.getInfo() };
    }
}
