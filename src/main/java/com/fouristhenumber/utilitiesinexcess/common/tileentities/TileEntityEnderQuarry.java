package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.utils.LoadableTE;
import com.fouristhenumber.utilitiesinexcess.config.blocks.EnderQuarryConfig;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;

public class TileEntityEnderQuarry extends LoadableTE implements IEnergyReceiver, IFluidHandler {

    public static final int STEPS_PER_TICK = 400;
    public static final int ITEM_BUFFER_CAPACITY = 256;
    public static final Block REPLACE_BLOCK = Blocks.dirt;
    private int storedItems;
    public ForgeDirection facing;
    private Area2d workArea;
    public QuarryWorkState state;
    private int dx;
    private int dy;
    private int dz;
    private int chunkX;
    private int chunkZ;
    private int brokenBlocksTotal;
    private final HashMap<ForgeDirection, @Nullable IInventory> sidedItemAcceptors = new HashMap<>();
    private final HashMap<ForgeDirection, IFluidHandler> sidedFluidAcceptors = new HashMap<>();

    protected final EnergyStorage energyStorage = new EnergyStorage(EnderQuarryConfig.enderQuarryEnergyStorage);
    protected final List<FluidTank> fluidStorage = Stream
        .generate(() -> new FluidTank(EnderQuarryConfig.enderQuarryFluidTankStorage))
        .limit(EnderQuarryConfig.enderQuarryFluidTankAmount)
        .collect(Collectors.toList());
    protected final Object2IntMap<@NotNull ItemStack> itemStorage = new Object2IntOpenCustomHashMap<>(
        ITEM_BUFFER_CAPACITY,
        ItemStackHashStrategy.instance);

    public TileEntityEnderQuarry() {
        resetState();
        storedItems = 0;
    }

    public void resetState() {
        state = QuarryWorkState.STOPPED;
        brokenBlocksTotal = 0;
    }

    public String getState() {
        return switch (state) {
            case RUNNING -> String
                .format("Quarry is currently mining at %d %d %d, has already mined %d", dx, dy, dz, brokenBlocksTotal);
            case STOPPED_WAITING_FOR_FLUID_SPACE -> "Quarry is full on fluids";
            case STOPPED_WAITING_FOR_ITEM_SPACE -> "Quarry is full on items";
            case STOPPED_WAITING_FOR_ENERGY -> "Quarry is missing energy";
            case THROTTLED_BY_ENERGY -> "Quarry is running, but not at full speed because of missing energy";
            case STOPPED -> "Quarry is stopped.";
            case FINISHED -> String.format(
                "Quarry has finished after mining %d blocks%s",
                brokenBlocksTotal,
                String.format(storedItems > 0 ? ", still holding %d items" : "", storedItems));
        };
    }

    public Area2d getWorkArea() {
        return this.workArea;
    }

    public void setWorkArea(Area2d area) {
        workArea = area;
        dx = area.low.x;
        dy = yCoord + 5;
        dz = area.low.y;
        chunkX = dx >> 4;
        chunkZ = dz >> 4;
    }

    /**
     * Are the current dx & dy & dz in work area bounds
     */
    private boolean isInBounds() {
        return dy > 1 && this.workArea.isInBounds(dx, dz);
    }

    /**
     * Step the quarry working position by one block
     *
     * @return True if we can keep moving
     */
    private boolean stepPos() {
        dy--;
        if (dy <= 1) {
            // stack is done, move back up
            dy = this.yCoord + 5;

            boolean resetX = false;
            if (dx + 1 >> 4 == chunkX && dx + 1 <= workArea.high.x) {
                dx++;
            } else {
                if (dz + 1 >> 4 == chunkZ && dz + 1 <= workArea.high.y) {
                    dz++;
                    resetX = true;
                } else {
                    // next pos up z is a new chunk and maybe oob
                    if (dz + 1 <= workArea.high.y) {
                        // just the next chunk
                        dz++;
                        unloadChunkShifted(chunkX, chunkZ);
                        chunkZ++;
                        loadChunkShifted(chunkX, chunkZ);
                        resetX = true;
                    } else {
                        // the next z slice
                        if (dx + 1 <= workArea.high.x) {
                            dx++;
                            unloadChunkShifted(chunkX, chunkZ);
                            chunkX++;
                            dz = workArea.low.y;
                            chunkZ = workArea.low.y >> 4;
                            loadChunkShifted(chunkX, chunkZ);
                        } else {
                            unloadChunkShifted(chunkX, chunkZ);
                            // Finished with area
                            return false;
                        }
                    }
                }
            }

            if (resetX) {
                // the distance we have to move left now, lower if we would cross the high x bound
                int toMove = dx + 1 > workArea.high.x ? workArea.chunkOffX : 15;
                if (dx - toMove >= workArea.low.x) {
                    dx -= toMove;
                } else {
                    dx = workArea.low.x;
                }
            }
        }
        return true;
    }

    /**
     * Tries to store the resulting resources of the block at the current position,
     * and store the items / fluid to the internal storage.
     *
     * @return A boolean tuple of: &lt;Could we work & harvest the block, Did we skip this block&gt;
     */
    private boolean[] tryHarvestCurrentBlock() {
        Block block = worldObj.getBlock(dx, dy, dz);
        if (block.getMaterial() == Material.air || worldObj.isAirBlock(dx, dy, dz)
            || block.getBlockHardness(worldObj, dx, dy, dz) < 0
            || block.getBlockHardness(worldObj, dx, dy, dz) > 100) {
            return new boolean[] { tryConsumeEnergy(0.0f, false), true };
        } ;

        float hardness = block.getBlockHardness(worldObj, dx, dy, dz);
        if (tryConsumeEnergy(hardness, true)) {
            return new boolean[] { (harvestAndStoreBlock(block) && tryConsumeEnergy(hardness, false)), false };
        }
        return new boolean[] { false, false };
    }

    private boolean tryConsumeEnergy(float hardness, boolean simulate) {
        float costMultiplier = 1.0f;
        int cost = (int) ((hardness == 0 ? 100 : EnderQuarryConfig.enderQuarryBaseRFCost) * costMultiplier);
        if (energyStorage.extractEnergy(cost, true) >= cost) {
            if (!simulate) {
                energyStorage.extractEnergy(cost, false);
            }
            return true;
        }
        state = QuarryWorkState.STOPPED_WAITING_FOR_ENERGY;
        return false;
    }

    private boolean harvestAndStoreBlock(Block block) {
        try {
            // TODO: Use "shouldPumpFluids" or similar from upgrades
            if (true) {
                FluidStack fluid = null;
                if (block == Blocks.water) {
                    fluid = new FluidStack(FluidRegistry.WATER, 1000);
                } else if (block == Blocks.lava) {
                    fluid = new FluidStack(FluidRegistry.LAVA, 1000);
                } else if (block instanceof IFluidBlock fluidBlock) {
                    fluid = fluidBlock.drain(worldObj, dx, dy, dz, false);
                }
                if (fluid != null) {
                    return tryStoreFluid(fluid);
                }
            }

            // TODO: Use fake player? Make sure to use fortune upgrade
            // We can accept that the maximum stored amount is sometimes overrun by fortune
            ArrayList<ItemStack> drops = block.getDrops(worldObj, dx, dy, dz, worldObj.getBlockMetadata(dx, dy, dz), 0);
            if (!drops.isEmpty()) {
                return tryStoreItems(drops);
            }

            // Block just has no drops
            return true;
        } catch (Exception ignored) {
            UtilitiesInExcess.LOG
                .error("Failed while trying to harvest block {} at {} {} {}.", block.toString(), dx, dy, dz);
            return false;
        }
    }

    /**
     * Tries to store the provided fluid in the internal tanks
     *
     * @return If we could store all the provided fluid
     */
    private boolean tryStoreFluid(FluidStack fluid) {
        int toStore = fluid.amount;
        for (FluidTank tank : fluidStorage) {
            if (toStore > 0) {
                toStore -= Math.min(tank.fill(fluid, false), toStore);
            }
        }
        if (toStore == 0) {
            toStore = fluid.amount;
            for (FluidTank tank : fluidStorage) {
                if (toStore > 0) {
                    toStore -= tank.fill(fluid, true);
                } else {
                    break;
                }
            }
            if (toStore == 0) {
                return true;
            }
        }
        state = QuarryWorkState.STOPPED_WAITING_FOR_FLUID_SPACE;
        return false;
    }

    /**
     * Tries to store the provided list of items in the internal item storage
     *
     * @return If we could store all the provided items
     */
    private boolean tryStoreItems(ArrayList<ItemStack> items) {
        int toStore = items.stream()
            .mapToInt((item) -> item != null ? item.stackSize : 0)
            .sum();
        if (storedItems + toStore <= ITEM_BUFFER_CAPACITY) {
            for (ItemStack item : items) {
                storeItemToStorage(item);
            }
            return true;
        }
        state = QuarryWorkState.STOPPED_WAITING_FOR_ITEM_SPACE;
        return false;
    }

    private void storeItemToStorage(ItemStack item) {
        int stackSize = item.stackSize;
        // Use an arbitrary size for all items of this type (type, meta, nbt),
        // since we store the amount as the value
        item.stackSize = 0;
        storedItems += stackSize;
        itemStorage.mergeInt(item, stackSize, Integer::sum);
    }

    private boolean fluidStorageIsEmpty() {
        return fluidStorage.stream()
            .mapToInt(FluidTank::getFluidAmount)
            .sum() == 0;
    }

    /**
     * Tries to eject internally stored items & fluids to adjacent containers
     *
     * @return False if the adjacent TEs changed without us noticing, and we need to rescan
     */
    private boolean ejectStoredToAdjacent() {
        if (storedItems > 0) {
            for (IInventory inventory : sidedItemAcceptors.values()) {
                if (inventory != null) {
                    for (int i = 0; i < inventory.getSizeInventory(); i++) {
                        ItemStack slotItem = inventory.getStackInSlot(i);
                        if (slotItem != null) {
                            // There is already an item in this slot, see if we can merge with it
                            int canBeAddedToStack = Math
                                .min(slotItem.getMaxStackSize(), inventory.getInventoryStackLimit())
                                - slotItem.stackSize;
                            if (canBeAddedToStack > 0) {
                                ItemStack key = slotItem.copy();
                                key.stackSize = 0;

                                // Do we also have this item type in our storage?
                                int storedAmount = itemStorage.getOrDefault(key, -1);
                                if (storedAmount > 0) {
                                    int toBeAdded = Math.min(storedAmount, canBeAddedToStack);

                                    ItemStack modifiedStack = key.copy();
                                    modifiedStack.stackSize = slotItem.stackSize + toBeAdded;
                                    if (inventory.isItemValidForSlot(i, modifiedStack)) {
                                        inventory.setInventorySlotContents(i, modifiedStack);

                                        itemStorage.put(key, storedAmount - toBeAdded);
                                        storedItems -= toBeAdded;
                                    }
                                }
                            }
                        } else {
                            // Empty slot, don't mind us
                            Object2IntMap.Entry<ItemStack> entry = peekItem();
                            if (entry != null) {
                                int canBeAddedToSlot = Math
                                    .min(entry.getIntValue(), inventory.getInventoryStackLimit());
                                if (canBeAddedToSlot > 0) {
                                    ItemStack item = entry.getKey()
                                        .copy();
                                    item.stackSize = canBeAddedToSlot;
                                    if (inventory.isItemValidForSlot(i, item)) {
                                        inventory.setInventorySlotContents(i, item);

                                        itemStorage.put(entry.getKey(), entry.getIntValue() - canBeAddedToSlot);
                                        storedItems -= canBeAddedToSlot;
                                    }
                                }
                            } else {
                                // Not sure why, but it seems our inventory is empty even though we thought it wasn't
                                // Lets re-count
                                storedItems = itemStorage.values()
                                    .intStream()
                                    .sum();
                            }
                        }
                    }

                    if (storedItems == 0) break;
                } else {
                    return false;
                }
            }
        }

        if (fluidStorageIsEmpty()) return true;

        for (Map.Entry<ForgeDirection, IFluidHandler> entry : sidedFluidAcceptors.entrySet()) {
            if (entry.getValue() != null) {
                for (FluidTank tank : fluidStorage) {
                    if (tank.getFluidAmount() > 0) {
                        int toStore = entry.getValue()
                            .fill(
                                entry.getKey()
                                    .getOpposite(),
                                tank.drain(tank.getCapacity(), false),
                                false);
                        if (toStore > 0) {
                            entry.getValue()
                                .fill(
                                    entry.getKey()
                                        .getOpposite(),
                                    tank.drain(toStore, true),
                                    true);
                        }
                    }
                }

                if (fluidStorageIsEmpty()) break;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the first item & int entry in the storage that we actually have something of
     *
     * @return An item if we have any, null otherwise
     */
    public @Nullable Object2IntMap.Entry<ItemStack> peekItem() {
        for (Object2IntMap.Entry<ItemStack> entry : itemStorage.object2IntEntrySet()) {
            if (entry.getIntValue() > 0) {
                return entry;
            }
        }
        return null;
    }

    public void scanSidesForTEs() {
        sidedFluidAcceptors.clear();
        sidedItemAcceptors.clear();
        ArrayList<ChunkCoordIntPair> loadedAdjacentChunks = new ArrayList<>();

        // Make sure the directly adjacent blocks in different chunks are loaded whilst checking
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            int directionChunkX = (direction.offsetX + this.xCoord) >> 4;
            int directionChunkZ = (direction.offsetZ + this.zCoord) >> 4;
            if ((directionChunkX != this.selfChunkX || directionChunkZ != this.selfChunkZ)
                && !areWeLoadingThisChunk(directionChunkX, directionChunkZ)) {
                loadChunkShifted(directionChunkX, directionChunkZ);
                loadedAdjacentChunks.add(new ChunkCoordIntPair(directionChunkX, directionChunkZ));
            }
        }

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity te = this.worldObj.getTileEntity(
                direction.offsetX + this.xCoord,
                direction.offsetY + this.yCoord,
                direction.offsetZ + this.zCoord);
            ChunkCoordIntPair chunk = new ChunkCoordIntPair(
                (direction.offsetX + this.xCoord) >> 4,
                (direction.offsetZ + this.zCoord) >> 4);

            // TODO: Upgrades people
            // if (te instanceof IQuarryUpgrade quarryUpgrade) {
            //
            // }

            if (te instanceof IFluidHandler fluidHandler) {
                sidedFluidAcceptors.put(direction, fluidHandler);
                // If there is actually a TE present that we want, keep this chunk loaded (if it is a different one)
                loadedAdjacentChunks.remove(chunk);
            }

            if (direction == ForgeDirection.UP) {
                if (te instanceof IInventory inventory) {
                    sidedItemAcceptors.put(direction, inventory);
                }
            }
        }

        // Unload all the temporarily loaded chunks
        for (ChunkCoordIntPair loadedChunk : loadedAdjacentChunks) {
            unloadChunk(loadedChunk);
        }
    }

    /**
     * Remove / replace the current block.
     * Does not check for anything, should be called after tryHarvestCurrentBlock() returns true.
     */
    private void removeCurrentBlock() {
        worldObj.setBlock(dx, dy, dz, Blocks.air);
    }

    // TileEntity & LoadableTE
    @Override
    public void validate() {
        super.validate();
        if (state != QuarryWorkState.FINISHED) {
            if (workArea != null) {
                loadChunkShifted(chunkX, chunkZ);
            }
            /*
             * preferably we would call scanSidesForTEs() here, however
             * the contained getTileEntity() seems to always re-load the chunk at this stage
             * which then re-validates back to this function, eventually leading to a stackoverflow
             * Instead we add a null inventory, so that it gets rechecked later
             **/
            sidedItemAcceptors.put(ForgeDirection.UP, null);
        }
    }

    // TileEntity
    @Override
    public void updateEntity() {
        if (worldObj.isRemote) return;
        if (facing == ForgeDirection.UNKNOWN) return;
        if (state == QuarryWorkState.FINISHED && storedItems == 0 && fluidStorageIsEmpty()) return;

        int brokenBlocksTick = 0;
        if (state != QuarryWorkState.RUNNING && state != QuarryWorkState.FINISHED && state != QuarryWorkState.STOPPED) {
            boolean[] harvestResult = tryHarvestCurrentBlock();
            boolean wasAbleToHarvest = harvestResult[0];
            boolean blockWasSkipped = harvestResult[1];
            if (wasAbleToHarvest) {
                state = QuarryWorkState.RUNNING;
                if (!blockWasSkipped) {
                    removeCurrentBlock();
                    brokenBlocksTick++;
                }
            }
        }

        if (state == QuarryWorkState.RUNNING) {
            while (brokenBlocksTick < STEPS_PER_TICK && stepPos()) {
                // TODO: Remove after this has been tested by others
                if (!isInBounds() || this.chunkX > 1000 || this.chunkZ > 1000) {
                    throw new RuntimeException(
                        String.format("Tried to quarry outside of work area at %d %d %d", dx, dy, dz));
                }

                boolean[] harvestResult = tryHarvestCurrentBlock();
                boolean wasAbleToHarvest = harvestResult[0];
                boolean blockWasSkipped = harvestResult[1];
                if (wasAbleToHarvest) {
                    if (!blockWasSkipped) {
                        removeCurrentBlock();
                        brokenBlocksTick++;
                    }
                } else {
                    // Check if something has stopped us (out of space / energy)
                    if (state != QuarryWorkState.RUNNING) break;
                }
            }
            if (brokenBlocksTick < STEPS_PER_TICK && state == QuarryWorkState.RUNNING) {
                state = QuarryWorkState.FINISHED;
                unloadSelf();
            }
            if (brokenBlocksTick > 0) {
                markDirty();
                if (state == QuarryWorkState.STOPPED_WAITING_FOR_ENERGY) {
                    // We were still able to mine some blocks this tick, so we don't consider this fully stopped
                    // If we fail to harvest again at the start of the next tick, it will be set to STOPPED_... either
                    // way
                    state = QuarryWorkState.THROTTLED_BY_ENERGY;
                }
            }

            this.brokenBlocksTotal += brokenBlocksTick;
        }

        if (worldObj.getTotalWorldTime() % 4 == 0) {
            // Move internally stored stuff to adjacent blocks
            if (!ejectStoredToAdjacent()) scanSidesForTEs();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        facing = ForgeDirection.getOrientation(nbt.getInteger("facing"));
        state = QuarryWorkState.values()[nbt.getInteger("state")];
        if (state != QuarryWorkState.FINISHED) {
            workArea = Area2d.fromNBTTag(nbt);
            dx = nbt.getInteger("dx");
            dy = nbt.getInteger("dy");
            dz = nbt.getInteger("dz");
            chunkX = dx >> 4;
            chunkZ = dz >> 4;
        }
        brokenBlocksTotal = nbt.getInteger("blocks");
        energyStorage.readFromNBT(nbt);

        NBTTagList tanksNBT = nbt.getTagList("tanks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tanksNBT.tagCount(); i++) {
            NBTTagCompound tag = tanksNBT.getCompoundTagAt(i);
            int slot = tag.getByte("Slot") & 255;
            if (slot < fluidStorage.size()) {
                fluidStorage.get(slot)
                    .readFromNBT(tag);
            }
        }

        NBTTagList itemsNBT = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        storedItems = 0;
        for (int i = 0; i < itemsNBT.tagCount(); i++) {
            NBTTagCompound tag = itemsNBT.getCompoundTagAt(i);
            ItemStack item = ItemStack.loadItemStackFromNBT(tag);
            if (item != null) {
                storeItemToStorage(item);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("facing", facing.ordinal());
        nbt.setInteger("state", state.ordinal());
        if (state != QuarryWorkState.FINISHED && workArea != null) {
            workArea.writeNBTTag(nbt);
            nbt.setInteger("dx", dx);
            nbt.setInteger("dy", dy);
            nbt.setInteger("dz", dz);
        }
        nbt.setInteger("blocks", brokenBlocksTotal);
        energyStorage.writeToNBT(nbt);

        NBTTagList tanksNBT = new NBTTagList();
        for (int i = 0; i < fluidStorage.size(); i++) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("Slot", (byte) i);
            fluidStorage.get(i)
                .writeToNBT(tag);
            tanksNBT.appendTag(tag);
        }
        nbt.setTag("tanks", tanksNBT);

        NBTTagList itemsNBT = new NBTTagList();
        for (Object2IntMap.Entry<ItemStack> entry : itemStorage.object2IntEntrySet()) {
            if (entry.getIntValue() > 0 && entry.getKey() != null) {
                NBTTagCompound tag = new NBTTagCompound();
                ItemStack item = entry.getKey()
                    .copy();
                item.stackSize = entry.getIntValue();
                item.writeToNBT(tag);
                itemsNBT.appendTag(tag);
            }
        }
        nbt.setTag("Items", itemsNBT);
    }

    // LoadableTE
    @Override
    public boolean keepsItselfLoaded() {
        return state != QuarryWorkState.FINISHED;
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
        for (FluidTank tank : fluidStorage) {
            if (resource != null && resource.isFluidEqual(tank.getFluid())) {
                return tank.drain(resource.amount, doDrain);
            }
        }
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        for (FluidTank tank : fluidStorage) {
            if (tank.getFluidAmount() > 0) {
                return tank.drain(maxDrain, doDrain);
            }
        }
        return null;
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
        return fluidStorage.stream()
            .map(FluidTank::getInfo)
            .toArray(FluidTankInfo[]::new);
    }

    public enum QuarryWorkState {
        STOPPED,
        STOPPED_WAITING_FOR_FLUID_SPACE,
        STOPPED_WAITING_FOR_ITEM_SPACE,
        STOPPED_WAITING_FOR_ENERGY,
        THROTTLED_BY_ENERGY,
        FINISHED,
        RUNNING
    }

    public static class Area2d {

        // The corner that has the lower x&y
        public final Vector2i low;
        // The corner that has the higher x&y
        public final Vector2i high;
        // The width of the entire working area
        public final int width;
        // The height of the entire working area
        public final int height;
        // The distance to the closest lower x chunk border from the high x bound
        public final int chunkOffX;

        public Area2d(Vector2i first, Vector2i second) {
            int lowX = Math.min(first.x, second.x);
            int lowZ = Math.min(first.y, second.y);
            int highX = Math.max(first.x, second.x);
            int highZ = Math.max(first.y, second.y);
            this.low = new Vector2i(lowX, lowZ);
            this.high = new Vector2i(highX, highZ);
            this.width = highX - lowX;
            this.height = highZ - lowZ;
            this.chunkOffX = highX - (highX & -16);
        }

        public Area2d(int x1, int z1, int x2, int z2) {
            this(new Vector2i(x1, z1), new Vector2i(x2, z2));
        }

        public boolean isInBounds(int x, int z) {
            return x >= this.low.x && x <= this.high.x && z >= this.low.y && z <= this.high.y;
        }

        public void writeNBTTag(NBTTagCompound nbt) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("lowX", low.x);
            tag.setInteger("lowZ", low.y);
            tag.setInteger("highX", high.x);
            tag.setInteger("highY", high.y);

            nbt.setTag("area", tag);
        }

        public static Area2d fromNBTTag(NBTTagCompound nbt) {
            NBTTagCompound tag = nbt.getCompoundTag("area");
            int lowX = tag.getInteger("lowX");
            int lowZ = tag.getInteger("lowZ");
            int highX = tag.getInteger("highX");
            int highZ = tag.getInteger("highY");
            return new Area2d(lowX, lowZ, highX, highZ);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Area2d other)) return false;
            return low.equals(other.low) && high.equals(other.high);
        }
    }

    protected static class ItemStackHashStrategy implements Hash.Strategy<ItemStack> {

        public static final ItemStackHashStrategy instance = new ItemStackHashStrategy();

        @Override
        public int hashCode(ItemStack itemStack) {
            if (itemStack == null || itemStack.getItem() == null) return 0;
            return Objects.hash(itemStack.getItem(), itemStack.getItemDamage(), itemStack.getTagCompound());
        }

        @Override
        public boolean equals(ItemStack a, ItemStack b) {
            if (a == null && b == null) return true;
            if (a == null || b == null) return false;
            return a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage()
                && Objects.equals(a.getTagCompound(), b.getTagCompound());
        }
    }
}
