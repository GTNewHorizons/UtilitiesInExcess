package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
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
import com.fouristhenumber.utilitiesinexcess.utils.Tuple;
import com.fouristhenumber.utilitiesinexcess.utils.UIEUtils;
import com.gtnewhorizon.gtnhlib.capability.item.ItemSink;
import com.gtnewhorizon.gtnhlib.item.ImmutableItemStack;
import com.gtnewhorizon.gtnhlib.item.InsertionItemStack;
import com.gtnewhorizon.gtnhlib.item.InventoryIterator;
import com.gtnewhorizon.gtnhlib.util.ItemUtil;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;

public class TileEntityEnderQuarry extends LoadableTE implements IEnergyReceiver, IFluidHandler {

    public static final int BASE_STEPS_PER_TICK = EnderQuarryConfig.enderQuarryBaseSpeed;
    public static final int ITEM_BUFFER_CAPACITY = BASE_STEPS_PER_TICK * 5; // Emptied every 4 ticks + some margin for
                                                                            // more than one item per block
    public static Block REPLACE_BLOCK = Blocks.dirt;
    public boolean isCreativeBoosted = false;
    private int storedItems;
    public ForgeDirection facing;
    private Area2d workArea;
    private List<Area2d> nextWorkAreas = new LinkedList<>();
    public QuarryWorkState state;
    private int dx;
    private int dy;
    private int dz;
    private int lowerYBound;
    private int upperYBound;
    private int chunkX;
    private int chunkZ;
    private long brokenBlocksTotal;
    private long estimatedTotalBlocks;
    private int estimatedSecondsLeft = -1;
    private boolean sidesValidated = false;
    public @Nullable UUID ownerUUID = null;
    private final ArrayDeque<Integer> brokenBlocksSlidingWindow = new ArrayDeque<>();
    private final HashMap<ForgeDirection, @Nullable IInventory> sidedItemAcceptors = new LinkedHashMap<>();
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
        REPLACE_BLOCK = EnderQuarryReplaceBlock.valueOf(EnderQuarryConfig.enderQuarryReplaceBlock).block;
        resetQuarry();
        storedItems = 0;
    }

    public String getState() {
        return switch (state) {
            case RUNNING -> String.format(
                "Quarry is currently mining at %d %d %d, has already mined/scanned %d blocks.%s",
                dx,
                dy,
                dz,
                brokenBlocksTotal,
                estimatedSecondsLeft > 0 ? String.format(
                    " Estimated time remaining: %02d:%02d:%02d.",
                    estimatedSecondsLeft / 3600,
                    (estimatedSecondsLeft % 3600) / 60,
                    (estimatedSecondsLeft % 60)) : "");
            case STOPPED_WAITING_FOR_FLUID_SPACE -> "Quarry is full on fluids.";
            case STOPPED_WAITING_FOR_ITEM_SPACE -> "Quarry is full on items.";
            case STOPPED_WAITING_FOR_ENERGY -> "Quarry is missing energy.";
            case THROTTLED_BY_ENERGY -> "Quarry is running, but not at full speed because of missing energy.";
            case STOPPED -> "Quarry is stopped.";
            case FINISHED -> String.format(
                "Quarry has finished after mining %d blocks%s.",
                brokenBlocksTotal,
                String.format(storedItems > 0 ? ", still holding %d items" : "", storedItems));
        };
    }

    private void resetQuarry() {
        state = QuarryWorkState.STOPPED;
        brokenBlocksTotal = 0;
        workArea = null;
        nextWorkAreas.clear();
    }

    public void setWorkArea(Area2d area) {
        workArea = area;
        dx = area.low.x;
        dy = upperYBound;
        dz = area.low.y;
        chunkX = dx >> 4;
        chunkZ = dz >> 4;
    }

    /**
     * Starts the quarry if it is currently stopped and has a valid work area.
     * Attempts to harvest the first block, because updateEntity steps before harvesting.
     */
    public void startQuarry() {
        if (state == QuarryWorkState.STOPPED && workArea != null) {
            state = QuarryWorkState.RUNNING;
            Tuple<Boolean, Boolean> harvestResult = tryHarvestCurrentBlock();
            boolean wasAbleToHarvest = harvestResult.first;
            boolean blockWasSkipped = harvestResult.second;
            if (wasAbleToHarvest) {
                if (!blockWasSkipped) {
                    removeCurrentBlock();
                }
                brokenBlocksTotal++;
            }
        }
    }

    /**
     * Tries to find a lower Y bound than the default of bedrock level (1), by scanning downwards from the quarry,
     * looking for the first marker. (Returns block above the marker, since we don't want to mine the marker itself.)
     */
    public int findLowerYBound() {
        for (int i = this.yCoord - 2; i > 1; i--) {
            if (worldObj.getTileEntity(xCoord, i, zCoord) instanceof TileEntityEnderMarker) {
                return i + 1;
            }
        }
        return 1;
    }

    /**
     * Tries to find a higher Y bound than the default of 5 blocks above the quarry (also configurable), by scanning
     * upwards from the quarry,
     * looking for the first marker. (Returns block below the marker, since we don't want to mine the marker itself.)
     */
    public int findUpperYBound() {
        for (int i = this.yCoord + 2; i < 255; i++) {
            if (worldObj.getTileEntity(xCoord, i, zCoord) instanceof TileEntityEnderMarker) {
                return i - 1;
            }
        }
        return Math.min(this.yCoord + EnderQuarryConfig.enderQuarryDefaultTopPadding, 255);
    }

    public void scanForWorkAreaFromMarkers(EntityPlayer player) {
        boolean foundMarkers = false;
        for (ForgeDirection dir : DirectionUtil.HORIZONTAL_DIRECTIONS) {
            TileEntity te = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord, zCoord + dir.offsetZ);
            if (te instanceof TileEntityEnderMarker marker) {
                @Nullable
                List<Vector2i> scanReturn = marker.checkForBoundary(dir, player);
                if (scanReturn != null) {
                    lowerYBound = findLowerYBound();
                    upperYBound = findUpperYBound();
                    long estBlocks = 0;
                    nextWorkAreas.clear();

                    // Do we have a simple rectangle as defined by two points
                    if (scanReturn.size() == 2) {
                        setWorkArea(new Area2d(scanReturn.get(0), scanReturn.get(1), true));
                        startQuarry();
                        // Estimate total blocks to be scanned, based upon work area size and vertical bounds. (Add one
                        // to each dimension to make it inclusive)
                        estBlocks = (long) (upperYBound - lowerYBound + 1) * (workArea.height + 1)
                            * (workArea.width + 1);
                        player.addChatComponentMessage(
                            new ChatComponentText(
                                String.format(
                                    StatCollector.translateToLocal("uie.quarry.scanmessage.1"),
                                    workArea.low.x,
                                    workArea.low.y,
                                    workArea.high.x,
                                    workArea.high.y,
                                    estBlocks)));

                        // Or do we have a more complex rectilinear polygon as defined by many points
                    } else {
                        // DEBUG: Clear work area of debug blocks
                        // Vector2i low = new Vector2i(Integer.MAX_VALUE);
                        // Vector2i high = new Vector2i(Integer.MIN_VALUE);
                        // for (Vector2i point : scanReturn) {
                        // if (point.x < low.x) {
                        // low.x = point.x;
                        // }
                        // if (point.y < low.y) {
                        // low.y = point.y;
                        // }
                        // if (point.x > high.x) {
                        // high.x = point.x;
                        // }
                        // if (point.y > high.y) {
                        // high.y = point.y;
                        // }
                        // }
                        // for (int x = low.x; x <= high.x; x++) {
                        // for (int z = low.y; z <= high.y; z++) {
                        // worldObj.setBlock(x, this.yCoord - 1, z, Blocks.grass);
                        // }
                        // }

                        nextWorkAreas = computeRectanglesFromRectilinearPointPolygon(scanReturn);
                        for (Area2d nextWorkArea : nextWorkAreas) {
                            // Estimate blocks in this area to be scanned, based upon work area size and vertical
                            // bounds. (Add one to each dimension to make it inclusive)
                            estBlocks += (long) (upperYBound - lowerYBound + 1) * (nextWorkArea.height + 1)
                                * (nextWorkArea.width + 1);
                        }

                        player.addChatComponentMessage(
                            new ChatComponentText(
                                String.format(
                                    StatCollector.translateToLocal("uie.quarry.scanmessage.2"),
                                    scanReturn.size(),
                                    estBlocks)));

                        setWorkArea(nextWorkAreas.remove(nextWorkAreas.size() - 1));
                        startQuarry();
                    }

                    estimatedTotalBlocks = estBlocks;
                    return;
                } else {
                    player.addChatComponentMessage(
                        new ChatComponentText(
                            String.format(
                                StatCollector.translateToLocal("uie.quarry.scanmessage.3"),
                                marker.xCoord,
                                marker.yCoord,
                                marker.zCoord)));
                    foundMarkers = true;
                }
            }
        }
        if (!foundMarkers) player
            .addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("uie.quarry.scanmessage.4")));
    }

    private List<Area2d> computeRectanglesFromRectilinearPointPolygon(List<Vector2i> points) {
        RectilinearEdgePoly poly = new RectilinearEdgePoly(points);
        List<Area2d> subAreas = new ArrayList<>();

        List<RectilinearEdgePoly.Span> activeSpans = new ArrayList<>();
        HashMap<Integer, List<RectilinearEdgePoly.Span>> shrunkSpansByY = new HashMap<>();

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

                    // Check for overlaps with previously shrunk spans which would be excluded,
                    // And create smaller single-line areas for them
                    // Store bottom and top spans that were shrunk inwards due to boundary intersections
                    if (intersectsWithBottomBoundary) {
                        for (RectilinearEdgePoly.Span span : shrunkSpansByY
                            .getOrDefault(active.y, Collections.emptyList())) {
                            // Check if the two intersect horizontally
                            if (span.pointIsInSpan(lowX)) {
                                subAreas.add(new Area2d(lowX, active.y, span.getMaxX(), active.y));
                            } else if (span.pointIsInSpan(highX)) {
                                subAreas.add(new Area2d(span.getMinX(), active.y, highX, active.y));
                            }
                        }
                        shrunkSpansByY.computeIfAbsent(active.y, k -> new ArrayList<>())
                            .add(new RectilinearEdgePoly.Span(lowX, highX, active.y));
                    }
                    if (intersectsWithTopBoundary) {
                        for (RectilinearEdgePoly.Span span : shrunkSpansByY.getOrDefault(y, Collections.emptyList())) {
                            // Check if the two intersect horizontally
                            if (span.pointIsInSpan(lowX)) {
                                subAreas.add(new Area2d(lowX, y, span.getMaxX(), y));
                            } else if (span.pointIsInSpan(highX)) {
                                subAreas.add(new Area2d(span.getMinX(), y, highX, y));
                            }
                        }
                        shrunkSpansByY.computeIfAbsent(y, k -> new ArrayList<>())
                            .add(new RectilinearEdgePoly.Span(lowX, highX, y));
                    }

                    Area2d subArea = new Area2d(
                        active.x1,
                        active.y,
                        active.x2,
                        y,
                        new Vector4i(1, intersectsWithBottomBoundary ? 1 : 0, 1, intersectsWithTopBoundary ? 1 : 0));

                    // The base area should have a width and height on init greater than zero, but that might change
                    // after
                    // applying shrinkage.
                    // We keep any area with width >= 0 and height >= 0 to allow for weird cases with single line areas.
                    if (subArea.height >= 0 && subArea.width >= 0) subAreas.add(subArea);
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
            int lowX = Math.min(span.x1, span.x2) + 1;
            int highX = Math.max(span.x1, span.x2) - 1;
            boolean intersectsWithBottomBoundary = poly.intersectsWithHorizontalBoundary(span.y, lowX, highX)
                && (span.y != lastY);

            // Same overlap check as before, just for bottom side now
            if (intersectsWithBottomBoundary) {
                for (RectilinearEdgePoly.Span shrunkSpan : shrunkSpansByY
                    .getOrDefault(span.y, Collections.emptyList())) {
                    // Check if the two intersect horizontally
                    if (shrunkSpan.pointIsInSpan(lowX)) {
                        subAreas.add(new Area2d(lowX, span.y, shrunkSpan.getMaxX(), span.y));
                    } else if (shrunkSpan.pointIsInSpan(highX)) {
                        subAreas.add(new Area2d(shrunkSpan.getMinX(), span.y, highX, span.y));
                    }
                }
            }

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
        // int color = 0;
        // for (Area2d subArea : subAreas) {
        // for (int x = subArea.low.x; x <= subArea.high.x; x++) {
        // for (int z = subArea.low.y; z <= subArea.high.y; z++) {
        // if (worldObj.getBlock(x, this.yCoord - 1, z) == Blocks.wool || worldObj.getBlock(x, this.yCoord - 1, z) ==
        // Blocks.gold_block) {
        // worldObj.setBlock(x, this.yCoord - 1, z, Blocks.gold_block);
        // //worldObj.setBlock(x, this.yCoord + 3 + color, z, Blocks.stained_glass, color, 2);
        //
        // //throw new RuntimeException("Overlapping sub-areas detected at " + x + ", " + z);
        // } else {
        // worldObj.setBlock(x, this.yCoord - 1, z, Blocks.wool, color, 2);
        // }
        // }
        // }
        // color++;
        // if (color == 16) {
        // color = 0;
        // }
        // }

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

            int getMinX() {
                return Math.min(x1, x2);
            }

            int getMaxX() {
                return Math.max(x1, x2);
            }

            /**
             * Check if an x value is within the span, assuming y is the same
             */
            boolean pointIsInSpan(int x) {
                return x >= getMinX() && x <= getMaxX();
            }
        }
    }

    /**
     * Are the current dx & dy & dz in work area bounds
     */
    private boolean isInBounds() {
        return dy >= lowerYBound && dy <= upperYBound && this.workArea.isInBounds(dx, dz);
    }

    /**
     * Step the quarry working position by one block
     *
     * @return True if we can keep moving
     */
    private boolean stepPos() {
        dy--;
        if (dy < lowerYBound) {
            // stack is done, move back up
            dy = upperYBound;

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
    private Tuple<Boolean, Boolean> tryHarvestCurrentBlock() {
        Block block = worldObj.getBlock(dx, dy, dz);
        if (block.getMaterial() == Material.air || worldObj.isAirBlock(dx, dy, dz)
            || block == (upgradeManager.has(EnderQuarryUpgradeManager.EnderQuarryUpgrade.WORLD_HOLE) ? Blocks.air
                : REPLACE_BLOCK)
            || block.getBlockHardness(worldObj, dx, dy, dz) < 0
            || block.getBlockHardness(worldObj, dx, dy, dz) > 100) {
            return new Tuple<>(tryConsumeEnergy(0f, false), true);
        } ;

        float hardness = block.getBlockHardness(worldObj, dx, dy, dz);
        if (tryConsumeEnergy(hardness, true)) {
            return new Tuple<>((harvestAndStoreBlock(block) && tryConsumeEnergy(hardness, false)), false);
        }
        return new Tuple<>(false, false);
    }

    private boolean tryConsumeEnergy(float hardness, boolean simulate) {
        // Get base cost multiplier from upgrades, add a multiplier based on hardness (that roughly scales 0 - 10 to 1.0
        // - 1.66)
        double costMultiplier = upgradeManager.totalCostMultiplier + (1.0 + 0.8 * (hardness / (hardness + 2)));
        int cost = (int) (EnderQuarryConfig.enderQuarryBaseRFCost * costMultiplier);
        if (energyStorage.extractEnergy(cost, true) >= cost) {
            if (!simulate) {
                energyStorage.extractEnergy(cost, false);
            }
            return true;
        }
        state = QuarryWorkState.STOPPED_WAITING_FOR_ENERGY;
        return false;
    }

    /**
     * Harvests the block at the current position, and tries to store the resulting items / fluids to internal storage
     *
     * @return If we could store all resulting items / fluids
     */
    private boolean harvestAndStoreBlock(Block block) {
        try {
            int meta = worldObj.getBlockMetadata(dx, dy, dz);
            if (upgradeManager.has(EnderQuarryUpgradeManager.EnderQuarryUpgrade.PUMP_FLUIDS)) {
                FluidStack fluid = null;
                if ((block == Blocks.water || block == Blocks.flowing_water) && meta == 0) {
                    fluid = new FluidStack(FluidRegistry.WATER, 1000);
                } else if ((block == Blocks.lava || block == Blocks.flowing_water) && meta == 0) {
                    fluid = new FluidStack(FluidRegistry.LAVA, 1000);
                } else if (block instanceof IFluidBlock fluidBlock) {
                    fluid = fluidBlock.drain(worldObj, dx, dy, dz, false);
                }
                if (fluid != null) {
                    return tryStoreFluid(fluid);
                }
            }

            EntityPlayer fakePlayer = FakePlayerFactory.getMinecraft((WorldServer) worldObj);
            @Nullable
            List<ItemStack> drops = null;
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
        int toStore = 0;
        for (ItemStack item : items) {
            toStore += item != null ? item.stackSize : 0;
        }
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
        for (FluidTank tank : fluidStorage) {
            if (tank.getFluidAmount() > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tries to eject internally stored items & fluids to adjacent containers
     *
     * @return False if the adjacent TEs changed without us noticing, and we need to rescan
     */
    private boolean ejectStoredToAdjacent() {
        if (storedItems > 0) {
            for (Map.Entry<ForgeDirection, @Nullable IInventory> inventory : sidedItemAcceptors.entrySet()) {
                if (inventory.getValue() != null) {
                    ItemSink sink = ItemUtil.getItemSink(inventory.getValue(), inventory.getKey());
                    InventoryIterator iter = sink.sinkIterator();
                    if (iter == null) {
                        continue;
                    }

                    while (iter.hasNext() && storedItems > 0) {
                        ImmutableItemStack slotContent = iter.next();
                        if (slotContent != null) {
                            // There is already an item in this slot, see if we have more of the same type to add
                            ItemStack slotItem = slotContent.toStack();
                            ItemStack key = slotItem.copy();
                            key.stackSize = 0;

                            int storedAmount = itemStorage.getOrDefault(key, -1);
                            if (storedAmount > 0) {
                                // modifiedStack.stackSize = slotItem.stackSize + toBeAdded; Stack size is handled by
                                // InsertionItemStack
                                int addedAmount = storedAmount
                                    - iter.insert(new InsertionItemStack(key.copy(), storedAmount), false);
                                if (addedAmount > 0) {
                                    itemStorage.put(key, storedAmount - addedAmount);
                                    storedItems -= addedAmount;
                                }
                            }
                        } else {
                            // Empty slot, don't mind us
                            Object2IntMap.Entry<ItemStack> entry = peekItem();
                            if (entry != null) {
                                ItemStack item = entry.getKey()
                                    .copy();
                                int storedAmount = entry.getIntValue();
                                // item.stackSize = canBeAddedToSlot; Stack size is handled by InsertionItemStack
                                int addedAmount = storedAmount
                                    - iter.insert(new InsertionItemStack(item, storedAmount), false);
                                if (addedAmount > 0) {
                                    itemStorage.put(entry.getKey(), storedAmount - addedAmount);
                                    storedItems -= addedAmount;
                                }
                            } else {
                                // Not sure why, but it seems our inventory is empty even though we thought it wasn't
                                // Lets re-count instead of breaking things
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

    /**
     * Retrieve all stored items, i.e. for the quarry is broken, and we need to drop its contents
     *
     * @return A list of all stored items as ItemStacks with correct stack sizes
     */
    public List<ItemStack> retrieveAllItems() {
        List<ItemStack> items = new ArrayList<>();
        for (Object2IntMap.Entry<ItemStack> entry : itemStorage.object2IntEntrySet()) {
            if (entry.getIntValue() > 0) {
                ItemStack item = entry.getKey()
                    .copy();
                item.stackSize = entry.getIntValue();
                items.add(item);
            }
        }
        itemStorage.clear();
        storedItems = 0;
        return items;
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
        worldObj.setBlock(
            dx,
            dy,
            dz,
            upgradeManager.has(EnderQuarryUpgradeManager.EnderQuarryUpgrade.WORLD_HOLE) ? Blocks.air : REPLACE_BLOCK);
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
             * Instead we set the side check state to false, so that the next updateEntity() call does the scan
             */
            sidesValidated = false;
        }
    }

    /**
     * Update the time left data with a new data point of blocks broken this tick & recalculate the estimate every
     * second
     */
    private void updateTimeLeftEstimate(int newDataPoint) {
        brokenBlocksSlidingWindow.add(newDataPoint);
        if (brokenBlocksSlidingWindow.size() > 300) brokenBlocksSlidingWindow.removeFirst();

        if (worldObj.getTotalWorldTime() % 20 == 0 && estimatedTotalBlocks > brokenBlocksTotal) {
            double averageBlocksPerTick = getAverageBlocksPerTick();
            if (averageBlocksPerTick > 0) {
                long blocksLeft = estimatedTotalBlocks - brokenBlocksTotal;
                estimatedSecondsLeft = (int) (blocksLeft / averageBlocksPerTick / 20);
            } else {
                estimatedSecondsLeft = -1;
            }
        }
    }

    private double getAverageBlocksPerTick() {
        int queuePos = 1;
        int currentWeight = 1;
        int weightedSum = 0;
        int weightTotal = 0;
        for (int blocksBrokenInTick : brokenBlocksSlidingWindow) {
            weightedSum += blocksBrokenInTick * currentWeight;
            weightTotal += currentWeight;
            // Increase weight every 20 ticks, to reduce the impact of short-lived spikes that only happened once
            if (queuePos % 20 == 0) currentWeight++;
            queuePos++;
        }
        return (double) weightedSum / weightTotal;
    }

    // TileEntity
    @Override
    public void updateEntity() {
        if (worldObj.isRemote) return;
        if (facing == ForgeDirection.UNKNOWN) return;
        if (state == QuarryWorkState.FINISHED && storedItems == 0 && fluidStorageIsEmpty() && ownerUUID == null) {
            if (selfIsLoaded) unloadSelf();
            return;
        }
        // Try to notify owner if the quarry has finished
        if (state == QuarryWorkState.FINISHED && ownerUUID != null && worldObj.getTotalWorldTime() % 100 == 0) {
            // Check if owner is online
            MinecraftServer server = MinecraftServer.getServer();
            if (server != null) {
                EntityPlayerMP owner = server.getConfigurationManager().playerEntityList.stream()
                    .filter(
                        (EntityPlayerMP player) -> player.getUniqueID()
                            .equals(ownerUUID))
                    .findFirst()
                    .orElse(null);
                if (owner != null) {
                    owner.addChatMessage(
                        new ChatComponentText(
                            String.format(
                                "Your Ender Quarry at (%d %d %d) in DIM %d has finished.",
                                xCoord,
                                yCoord,
                                zCoord,
                                worldObj.provider.dimensionId)));
                    ownerUUID = null;
                }
            }
        }
        if (!sidesValidated) {
            scanSidesForTEs();
            sidesValidated = true;
        }

        // This may mean any mix of: Blocks broken & Blocks skipped
        int blocksVisitedThisTick = 0;
        // Check if we can harvest the current block if we are stopped by something, but have already started working
        if (state != QuarryWorkState.RUNNING && state != QuarryWorkState.FINISHED && state != QuarryWorkState.STOPPED) {
            Tuple<Boolean, Boolean> harvestResult = tryHarvestCurrentBlock();
            boolean wasAbleToHarvest = harvestResult.first;
            boolean blockWasSkipped = harvestResult.second;
            if (wasAbleToHarvest) {
                state = QuarryWorkState.RUNNING;
                if (!blockWasSkipped) {
                    removeCurrentBlock();
                }
                blocksVisitedThisTick++;
            }
        }

        if (state == QuarryWorkState.RUNNING) {
            int stepsPerTick = (int) (BASE_STEPS_PER_TICK * (isCreativeBoosted ? 8 : 1)
                * upgradeManager.getValue(EnderQuarryUpgradeManager.TieredEnderQuarryUpgrade.SPEED, 1.0));
            while (blocksVisitedThisTick < stepsPerTick && stepPos()) {
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

                Tuple<Boolean, Boolean> harvestResult = tryHarvestCurrentBlock();
                boolean wasAbleToHarvest = harvestResult.first;
                boolean blockWasSkipped = harvestResult.second;
                if (wasAbleToHarvest) {
                    if (!blockWasSkipped) {
                        removeCurrentBlock();
                    }
                    blocksVisitedThisTick++;
                } else {
                    // Check if something has stopped us (out of space / energy)
                    if (state != QuarryWorkState.RUNNING) break;
                }
            }
            if (blocksVisitedThisTick < stepsPerTick && state == QuarryWorkState.RUNNING) {
                if (nextWorkAreas.isEmpty()) {
                    state = QuarryWorkState.FINISHED;
                } else {
                    setWorkArea(nextWorkAreas.remove(nextWorkAreas.size() - 1));
                }
            }
            if (blocksVisitedThisTick > 0) {
                markDirty();
                if (state == QuarryWorkState.STOPPED_WAITING_FOR_ENERGY) {
                    // We were still able to mine some blocks this tick, so we don't consider this fully stopped
                    // If we fail to harvest again at the start of the next tick, it will be set to STOPPED_... either
                    // way
                    state = QuarryWorkState.THROTTLED_BY_ENERGY;
                }
            }

            updateTimeLeftEstimate(blocksVisitedThisTick);
            this.brokenBlocksTotal += blocksVisitedThisTick;
        }

        // Move internally stored stuff to adjacent blocks
        if (worldObj.getTotalWorldTime() % 4 == 0 && (storedItems > 0 || !fluidStorageIsEmpty())) {
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
            // Y bounds
            lowerYBound = nbt.getInteger("lowerYBound");
            upperYBound = nbt.getInteger("upperYBound");
            // Fix for cases where upperYBound was not present yet
            if (lowerYBound == upperYBound) upperYBound = findUpperYBound();

            // Possible next work areas for complex markers
            NBTTagList possibleNextAreas = nbt.getTagList("nextAreas", Constants.NBT.TAG_COMPOUND);
            if (possibleNextAreas.tagCount() > 0) {
                nextWorkAreas.clear();
                for (int i = 0; i < possibleNextAreas.tagCount(); i++) {
                    NBTTagCompound tag = possibleNextAreas.getCompoundTagAt(i);
                    nextWorkAreas.add(Area2d.fromNBTTag(tag));
                }
            }
        }
        brokenBlocksTotal = nbt.getLong("brokenBlocks");
        estimatedTotalBlocks = nbt.getLong("estBlocks");
        estimatedSecondsLeft = nbt.getInteger("estSecondsLeft");
        ownerUUID = nbt.getString("owner")
            .isEmpty() ? null : UUID.fromString(nbt.getString("owner"));

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
                item.stackSize = tag.getShort("Count");
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
            // Work area
            workArea.writeNBTTag(nbt);
            nbt.setInteger("dx", dx);
            nbt.setInteger("dy", dy);
            nbt.setInteger("dz", dz);
            // Y bounds
            nbt.setInteger("lowerYBound", lowerYBound);
            nbt.setInteger("upperYBound", upperYBound);
            // Possible next work areas for complex markers
            NBTTagList areasNBT = new NBTTagList();
            for (Area2d nextArea : nextWorkAreas) {
                NBTTagCompound tag = new NBTTagCompound();
                nextArea.writeNBTTag(tag);
                areasNBT.appendTag(tag);
            }
            nbt.setTag("nextAreas", areasNBT);
        }
        nbt.setLong("brokenBlocks", brokenBlocksTotal);
        nbt.setLong("estBlocks", estimatedTotalBlocks);
        nbt.setInteger("estSecondsLeft", estimatedSecondsLeft);
        nbt.setString("owner", ownerUUID != null ? ownerUUID.toString() : "");

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
                item.writeToNBT(tag);
                // Default itemstack uses byte for count, but we can have more than 255 of an item
                tag.setShort("Count", (short) entry.getIntValue());

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

        STOPPED("uie.quarry.state.1"),
        STOPPED_WAITING_FOR_FLUID_SPACE("uie.quarry.state.2"),
        STOPPED_WAITING_FOR_ITEM_SPACE("uie.quarry.state.3"),
        STOPPED_WAITING_FOR_ENERGY("uie.quarry.state.4"),
        THROTTLED_BY_ENERGY("uie.quarry.state.5"),
        FINISHED("uie.quarry.state.6"),
        RUNNING("uie.quarry.state.7");

        public static final QuarryWorkState[] VALUES = values();

        public final String localKey;

        QuarryWorkState(String localKey) {
            this.localKey = localKey;
        }
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

    private enum EnderQuarryReplaceBlock {

        COBBLE(Blocks.cobblestone),
        DIRT(Blocks.dirt),
        GLASS(Blocks.glass),
        SNOW(Blocks.snow),
        STONE(Blocks.stone);

        public final Block block;

        EnderQuarryReplaceBlock(Block block) {
            this.block = block;
        }
    }
}
