package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fouristhenumber.utilitiesinexcess.utils.UIEUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayerFactory;
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
import org.joml.Vector4i;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.blocks.ender_quarry.BlockEnderQuarryUpgrade;
import com.fouristhenumber.utilitiesinexcess.common.blocks.ender_quarry.EnderQuarryUpgradeManager;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.utils.LoadableTE;
import com.fouristhenumber.utilitiesinexcess.config.blocks.EnderQuarryConfig;
import com.fouristhenumber.utilitiesinexcess.utils.DirectionUtil;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;

public class TileEntityEnderQuarry extends LoadableTE implements IEnergyReceiver, IFluidHandler {

    public static final int BASE_STEPS_PER_TICK = 400;
    public static final int ITEM_BUFFER_CAPACITY = 256;
    public static final Block REPLACE_BLOCK = EnderQuarryConfig.enderQuarryReplaceBlock.block;
    public boolean isCreativeBoosted = false;
    private int storedItems;
    public ForgeDirection facing;
    private Area2d workArea;
    private List<Area2d> nextWorkAreas = new LinkedList<>();
    public QuarryWorkState state;
    private int dx;
    private int dy;
    private int dz;
    private int chunkX;
    private int chunkZ;
    private int brokenBlocksTotal;
    private final HashMap<ForgeDirection, @Nullable IInventory> sidedItemAcceptors = new HashMap<>();
    private final HashMap<ForgeDirection, IFluidHandler> sidedFluidAcceptors = new HashMap<>();
    private final EnderQuarryUpgradeManager upgradeManager = new EnderQuarryUpgradeManager();

    protected final EnergyStorage energyStorage = new EnergyStorage(EnderQuarryConfig.enderQuarryEnergyStorage);
    protected final List<FluidTank> fluidStorage = Stream
        .generate(() -> new FluidTank(EnderQuarryConfig.enderQuarryFluidTankStorage))
        .limit(EnderQuarryConfig.enderQuarryFluidTankAmount)
        .collect(Collectors.toList());
    protected final Object2IntMap<@NotNull ItemStack> itemStorage = new Object2IntOpenCustomHashMap<>(
        ITEM_BUFFER_CAPACITY,
        UIEUtils.ItemStackHashStrategy.instance);

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
            case RUNNING -> String.format(
                "Quarry is currently mining at %d %d %d, has already mined %d blocks",
                dx,
                dy,
                dz,
                brokenBlocksTotal);
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

    public void scanForWorkAreaFromMarkers(EntityPlayer player) {
        boolean foundMarkers = false;
        for (ForgeDirection dir : DirectionUtil.HORIZONTAL_DIRECTIONS) {
            TileEntity te = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord, zCoord + dir.offsetZ);
            if (te instanceof TileEntityEnderMarker marker) {
                @Nullable
                List<Vector2i> scanReturn = marker.checkForBoundary(dir, player);
                if (scanReturn != null) {
                    nextWorkAreas.clear();
                    // Do we have a simple rectangle as defined by two points
                    if (scanReturn.size() == 2) {
                        Vector2i firstCorner = scanReturn.get(0);
                        Vector2i secondCorner = scanReturn.get(1);

                        // Pad work area by one (inwards), so that we don't mine the markers
                        Vector2i low = new Vector2i(
                            Math.min(firstCorner.x, secondCorner.x) + 1,
                            Math.min(firstCorner.y, secondCorner.y) + 1);
                        Vector2i high = new Vector2i(
                            Math.max(firstCorner.x, secondCorner.x) - 1,
                            Math.max(firstCorner.y, secondCorner.y) - 1);
                        setWorkArea(new Area2d(low, high));
                        state = QuarryWorkState.RUNNING;

                        int estBlocks = (worldObj.getHeightValue(dx, dy) + 5) * workArea.height * workArea.width;
                        player.addChatComponentMessage(
                            new ChatComponentText(
                                String.format(
                                    "Found ender marker fence boundary, setting up work area from (%d %d) to (%d %d). Should roughly contain %d blocks.",
                                    workArea.low.x,
                                    workArea.low.y,
                                    workArea.high.x,
                                    workArea.high.y,
                                    estBlocks)));
                        // Or do we have a more complex rectilinear polygon as defined by many points
                    } else {

                        // DEBUG: Clear work area of debug blocks
                        /*
                         * Vector2i low = new Vector2i(Integer.MAX_VALUE);
                         * Vector2i high = new Vector2i(Integer.MIN_VALUE);
                         * for (Vector2i point : scanReturn) {
                         * if (point.x < low.x) {
                         * low.x = point.x;
                         * }
                         * if (point.y < low.y) {
                         * low.y = point.y;
                         * }
                         * if (point.x > high.x) {
                         * high.x = point.x;
                         * }
                         * if (point.y > high.y) {
                         * high.y = point.y;
                         * }
                         * }
                         * for (int x = low.x; x <= high.x; x++) {
                         * for (int z = low.y; z <= high.y; z++) {
                         * worldObj.setBlock(x, this.yCoord - 1, z, Blocks.grass);
                         * }
                         * }
                         */

                        nextWorkAreas = computeRectanglesFromRectilinearPointPolygon(scanReturn);
                        setWorkArea(nextWorkAreas.remove(nextWorkAreas.size() - 1));
                        state = QuarryWorkState.RUNNING;
                    }
                    return;
                } else {
                    player.addChatComponentMessage(
                        new ChatComponentText(
                            String.format(
                                "Ender marker at (%d %d %d) failed to set up a fence boundary.",
                                marker.xCoord,
                                marker.yCoord,
                                marker.zCoord)));
                    foundMarkers = true;
                }
            }
        }
        if (!foundMarkers)
            player.addChatComponentMessage(new ChatComponentText("Found no ender markers around quarry."));
    }

    private List<Area2d> computeRectanglesFromRectilinearPointPolygon(List<Vector2i> points) {
        RectilinearEdgePoly poly = new RectilinearEdgePoly(points);
        List<Area2d> subAreas = new ArrayList<>();
        // DEBUG: int color = 0;

        List<RectilinearEdgePoly.Span> activeSpans = new ArrayList<>();

        // Sweep through y coordinates from bottom to top, skip last since we handle active spans after the loop
        for (int i = 0; i < poly.yCoords.size() - 1; i++) {
            int y = poly.yCoords.get(i);

            // Find spans at this Y level by intersecting vertical edges
            List<RectilinearEdgePoly.Span> currentSpans = poly.findSpansAtY(y);

            // Try to merge with active spans (greedy vertical extension)
            List<RectilinearEdgePoly.Span> newActiveSpans = new ArrayList<>();
            Set<Integer> mergedIndices = new HashSet<>();

            for (RectilinearEdgePoly.Span active : activeSpans) {
                boolean merged = false;
                for (int j = 0; j < currentSpans.size(); j++) {
                    if (!mergedIndices.contains(j) && active.matches(currentSpans.get(j))) {
                        // Extend the active span vertically
                        newActiveSpans.add(new RectilinearEdgePoly.Span(active.x1, active.x2, active.y));
                        mergedIndices.add(j);
                        merged = true;
                        break;
                    }
                }

                if (!merged) {
                    // Can't extend this span, output as rectangle
                    // Check if bottom and top align with actual boundary edges
                    int lowX = Math.min(active.x1, active.x2) + 1;
                    int highX = Math.max(active.x1, active.x2) - 1;
                    boolean intersectsWithBottomBoundary = poly.intersectsWithHorizontalBoundary(active.y, lowX, highX)
                        && (active.y != y);
                    boolean intersectsWithTopBoundary = poly.intersectsWithHorizontalBoundary(y, lowX, highX)
                        && (active.y != y);

                    Area2d subArea = new Area2d(
                        active.x1,
                        active.y,
                        active.x2,
                        y,
                        new Vector4i(1, intersectsWithBottomBoundary ? 1 : 0, 1, intersectsWithTopBoundary ? 1 : 0));

                    // The base area should have a width and height greater than zero, but that might change after
                    // applying shrinkage
                    if (subArea.height > 0 && subArea.width > 0) subAreas.add(subArea);
                }
            }

            // Add unmerged current spans as new active spans
            for (int j = 0; j < currentSpans.size(); j++) {
                if (!mergedIndices.contains(j)) {
                    newActiveSpans.add(new RectilinearEdgePoly.Span(currentSpans.get(j).x1, currentSpans.get(j).x2, y));
                }
            }

            activeSpans = newActiveSpans;
        }

        // Output remaining active spans that can't be extended anymore
        int lastY = poly.yCoords.get(poly.yCoords.size() - 1);
        for (RectilinearEdgePoly.Span span : activeSpans) {
            boolean intersectsWithBottomBoundary = poly.intersectsWithHorizontalBoundary(
                span.y,
                Math.min(span.x1, span.x2) + 1,
                Math.max(span.x1, span.x2) - 1) && (span.y != lastY);
            // Always shrink top side on last rectangle
            subAreas.add(
                new Area2d(
                    span.x1,
                    span.y,
                    span.x2,
                    lastY,
                    new Vector4i(1, intersectsWithBottomBoundary ? 1 : 0, 1, 1)));
        }

        // DEBUG: Draw sub areas on floor
        /*
         * for (Area2d subArea : subAreas) {
         * for (int x = subArea.low.x; x <= subArea.high.x; x++) {
         * for (int z = subArea.low.y; z <= subArea.high.y; z++) {
         * if (worldObj.getBlock(x, this.yCoord - 1, z) == Blocks.wool || worldObj.getBlock(x, this.yCoord - 1, z) ==
         * Blocks.gold_block) {
         * worldObj.setBlock(x, this.yCoord - 1, z, Blocks.gold_block);
         * //throw new RuntimeException("Overlapping sub-areas detected at " + x + ", " + z);
         * } else {
         * worldObj.setBlock(x, this.yCoord - 1, z, Blocks.wool, color, 2);
         * }
         * }
         * }
         * color++;
         * if (color == 16) {
         * color = 0;
         * }
         * }
         */

        return subAreas;
    }

    private static class RectilinearEdgePoly {

        List<Integer> yCoords;
        HashMap<Integer, List<Edge>> verticalEdgesByX;
        HashMap<Integer, List<Edge>> horizontalEdgesByY;

        RectilinearEdgePoly(List<Vector2i> points) {
            // Build edges from point list
            Iterator<Vector2i> iter = points.iterator();
            List<Edge> edges = new ArrayList<>();
            Vector2i current = null;
            Vector2i next = null;
            while (iter.hasNext()) {
                current = next != null ? next : iter.next();
                next = iter.hasNext() ? iter.next() : points.get(0);
                edges.add(new Edge(current, next));
            }
            // Connect back to start
            if (next != null) edges.add(new Edge(next, points.get(0)));

            // Build list of Y coordinates with points sorted by the lovely tree set
            Set<Integer> ySet = new TreeSet<>();
            // Build vertical edge map for x & y lookups
            verticalEdgesByX = new HashMap<>();
            horizontalEdgesByY = new HashMap<>();

            for (Edge edge : edges) {
                ySet.add(edge.p1.y);
                ySet.add(edge.p2.y);
                if (edge.isVertical()) {
                    verticalEdgesByX.computeIfAbsent(edge.getX(), k -> new ArrayList<>())
                        .add(edge);
                } else if (edge.isHorizontal()) {
                    horizontalEdgesByY.computeIfAbsent(edge.getY(), k -> new ArrayList<>())
                        .add(edge);
                }
            }
            yCoords = new ArrayList<>(ySet);
        }

        /**
         * Find all vertical edges that cross the horizontal line at y
         */
        List<Span> findSpansAtY(int y) {
            // Collect x-coordinates where edges cross
            Set<Integer> xIntersections = new TreeSet<>();

            for (Map.Entry<Integer, List<Edge>> entry : verticalEdgesByX.entrySet()) {
                for (Edge e : entry.getValue()) {
                    // Check if this vertical edge crosses y (exclusive on top to avoid double counting)
                    if (e.getMinY() <= y && e.getMaxY() > y) {
                        xIntersections.add(entry.getKey());
                    }
                }
            }

            // Pair up intersections to form spans (inside regions)
            // Assumes even number of intersections (entering/exiting polygon)
            List<Span> spans = new ArrayList<>();
            List<Integer> xCoords = new ArrayList<>(xIntersections);

            for (int i = 0; i < xIntersections.size(); i += 2) {
                if (i + 1 < xIntersections.size()) {
                    spans.add(new Span(xCoords.get(i), xCoords.get(i + 1), y));
                }
            }

            return spans;
        }

        boolean intersectsWithHorizontalBoundary(int y, int x1, int x2) {
            List<Edge> boundaries = horizontalEdgesByY.getOrDefault(y, Collections.emptyList());
            for (Edge edge : boundaries) {
                // Check if [x1, x2] overlaps with [boundary.x1, boundary.x2]
                if (x1 <= edge.getMaxX() && x2 >= edge.getMinX()) {
                    return true; // Our span is contained within a boundary edge
                }
            }
            return false;
        }

        static class Edge {

            Vector2i p1, p2;

            Edge(Vector2i p1, Vector2i p2) {
                this.p1 = p1;
                this.p2 = p2;
            }

            boolean isHorizontal() {
                return p1.y == p2.y;
            }

            boolean isVertical() {
                return p1.x == p2.x;
            }

            int getY() {
                return p1.y; // For horizontal edges
            }

            int getX() {
                return p1.x; // For vertical edges
            }

            int getMinX() {
                return Math.min(p1.x, p2.x);
            }

            int getMaxX() {
                return Math.max(p1.x, p2.x);
            }

            int getMinY() {
                return Math.min(p1.y, p2.y);
            }

            int getMaxY() {
                return Math.max(p1.y, p2.y);
            }

            @Override
            public String toString() {
                return p1 + " -> " + p2;
            }
        }

        static class Span {

            int x1, x2, y;

            Span(int x1, int x2, int y) {
                this.x1 = x1;
                this.x2 = x2;
                this.y = y;
            }

            boolean matches(Span other) {
                return this.x1 == other.x1 && this.x2 == other.x2;
            }
        }
    }

    /**
     * Are the current dx & dy & dz in work area bounds
     */
    private boolean isInBounds() {
        return dy > 0 && this.workArea.isInBounds(dx, dz);
    }

    /**
     * Step the quarry working position by one block
     *
     * @return True if we can keep moving
     */
    private boolean stepPos() {
        dy--;
        if (dy <= 0) {
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
        double costMultiplier = upgradeManager.getTotalCostMultiplier();
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
            if (upgradeManager.has(EnderQuarryUpgradeManager.EnderQuarryUpgrade.PUMP_FLUIDS)) {
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

            EntityPlayer fakePlayer = FakePlayerFactory.getMinecraft((WorldServer) worldObj);
            @Nullable List<ItemStack> drops = null;
            int meta = worldObj.getBlockMetadata(dx, dy, dz);
            // Try to silk touch if we have the upgrade
            if (upgradeManager.has(EnderQuarryUpgradeManager.EnderQuarryUpgrade.SILK_TOUCH)) {
                if (block.canSilkHarvest(worldObj, fakePlayer, dx, dy, dz, meta)) {
                    Item item = Item.getItemFromBlock(block);
                    if (item != null) {
                        // Set item damage from meta if the BlockItem has subtypes
                        drops = Collections.singletonList(new ItemStack(item, 1, item.getHasSubtypes() ? meta : 0));
                    }
                }
            }
            // If not silk or we failed to resolve to reasonable drops, get normal drops (with fortune if applicable)
            if (drops == null) {
                drops = block.getDrops(
                    worldObj,
                    dx,
                    dy,
                    dz,
                    meta,
                    (int) upgradeManager.getValue(EnderQuarryUpgradeManager.TieredEnderQuarryUpgrade.FORTUNE, 0));
            }
            // We can accept that the maximum stored amount is sometimes overrun by fortune
            if (!drops.isEmpty()) {
                return tryStoreItems(drops);
            }

            // Block probably just has no drops
            return true;
        } catch (Exception ignored) {
            UtilitiesInExcess.LOG
                .error("EQ Failed while trying to harvest block {} at {} {} {}.", block.toString(), dx, dy, dz);
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
    private boolean tryStoreItems(List<ItemStack> items) {
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
        upgradeManager.clear();
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
            Block block = this.worldObj.getBlock(
                direction.offsetX + this.xCoord,
                direction.offsetY + this.yCoord,
                direction.offsetZ + this.zCoord);

            ChunkCoordIntPair chunk = new ChunkCoordIntPair(
                (direction.offsetX + this.xCoord) >> 4,
                (direction.offsetZ + this.zCoord) >> 4);

            if (block instanceof BlockEnderQuarryUpgrade) {
                int meta = this.worldObj.getBlockMetadata(
                    direction.offsetX + this.xCoord,
                    direction.offsetY + this.yCoord,
                    direction.offsetZ + this.zCoord);
                upgradeManager.addUpgrade(EnderQuarryUpgradeManager.EnderQuarryUpgrade.VALUES[meta]);
            }

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
        worldObj.setBlock(dx, dy, dz, upgradeManager.has(EnderQuarryUpgradeManager.EnderQuarryUpgrade.WORLD_HOLE) ? Blocks.air : REPLACE_BLOCK);
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
            while (brokenBlocksTick < (BASE_STEPS_PER_TICK * (isCreativeBoosted ? 8 : 1)) && stepPos()) {
                // TODO: Remove after this has been tested by others
                if (!isInBounds() || this.chunkX > 1000 || this.chunkZ > 1000) {
                    UtilitiesInExcess.LOG.warn(
                        "Tried to quarry outside of work area at {} {} {} for work area {}",
                        dx,
                        dy,
                        dz,
                        this.workArea.toString());
                    worldObj.setBlock(dx, dy + 8, dz, Blocks.glass);
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
            if (brokenBlocksTick < (BASE_STEPS_PER_TICK * (isCreativeBoosted ? 8 : 1))
                && state == QuarryWorkState.RUNNING) {
                if (nextWorkAreas.isEmpty()) {
                    state = QuarryWorkState.FINISHED;
                    unloadSelf();
                } else {
                    setWorkArea(nextWorkAreas.remove(nextWorkAreas.size() - 1));
                }
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
            // Current work area
            workArea = Area2d.fromNBTTag(nbt);
            dx = nbt.getInteger("dx");
            dy = nbt.getInteger("dy");
            dz = nbt.getInteger("dz");
            chunkX = dx >> 4;
            chunkZ = dz >> 4;

            // Possible next work areas
            NBTTagList possibleNextAreas = nbt.getTagList("nextAreas", Constants.NBT.TAG_COMPOUND);
            if (possibleNextAreas.tagCount() > 0) {
                nextWorkAreas.clear();
                for (int i = 0; i < possibleNextAreas.tagCount(); i++) {
                    NBTTagCompound tag = possibleNextAreas.getCompoundTagAt(i);
                    nextWorkAreas.add(Area2d.fromNBTTag(tag));
                }
            }
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

            NBTTagList areasNBT = new NBTTagList();
            for (Area2d nextArea : nextWorkAreas) {
                NBTTagCompound tag = new NBTTagCompound();
                nextArea.writeNBTTag(tag);
                areasNBT.appendTag(tag);
            }
            nbt.setTag("nextAreas", areasNBT);
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
        public int width;
        // The height of the entire working area
        public int height;
        // The distance to the closest lower x chunk border from the high x bound
        public int chunkOffX;

        public Area2d(Vector2i first, Vector2i second, Vector4i shrinkMatrix) {
            int lowX = Math.min(first.x, second.x) + (shrinkMatrix.x); // Side: left
            int lowZ = Math.min(first.y, second.y) + (shrinkMatrix.y); // Side: bottom
            int highX = Math.max(first.x, second.x) - (shrinkMatrix.z); // Side: right
            int highZ = Math.max(first.y, second.y) - (shrinkMatrix.w); // Side: top
            // We explicitly do not rerun min & max here since the shrink matrix might have inverted the area,
            // But rather leave it to the caller to handle a 0 null width / height
            this.low = new Vector2i(lowX, lowZ);
            this.high = new Vector2i(highX, highZ);
            this.width = highX - lowX;
            this.height = highZ - lowZ;
            this.chunkOffX = highX - (highX & -16);
        }

        public Area2d(int x1, int z1, int x2, int z2) {
            this(new Vector2i(x1, z1), new Vector2i(x2, z2));
        }

        public Area2d(int x1, int z1, int x2, int z2, Vector4i shrinkMatrix) {
            this(new Vector2i(x1, z1), new Vector2i(x2, z2), shrinkMatrix);
        }

        public Area2d(Vector2i first, Vector2i second) {
            // Don't shrink anywhere
            this(first, second, new Vector4i(0, 0, 0, 0));
        }

        public Area2d(Vector2i first, Vector2i second, boolean shouldShrink) {
            // Shrink equally on all sides if requested
            this(first, second, shouldShrink ? new Vector4i(1, 1, 1, 1) : new Vector4i(0, 0, 0, 0));
        }

        public boolean isInBounds(int x, int z) {
            return x >= this.low.x && x <= this.high.x && z >= this.low.y && z <= this.high.y;
        }

        public void applyShrinkMatrix(Vector4i shrinkMatrix) {
            // Same here, we do not check a null / inverted area, but rather leave it up to the caller
            this.low.x += shrinkMatrix.x;
            this.low.y += shrinkMatrix.y;
            this.high.x -= shrinkMatrix.z;
            this.high.y -= shrinkMatrix.w;

            this.width = this.high.x - this.low.x;
            this.height = this.high.y - this.low.y;
            this.chunkOffX = this.high.x - (this.high.x & -16);
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

        @Override
        public String toString() {
            return String.format("[(%d, %d), (%d, %d)]", this.low.x, this.low.y, this.high.x, this.high.y);
        }
    }
}
