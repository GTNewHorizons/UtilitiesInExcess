package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
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
import com.fouristhenumber.utilitiesinexcess.utils.DirectionUtil;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityEnderMarker.FacingVector2i;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import org.joml.Vector4i;

public class TileEntityEnderQuarry extends LoadableTE implements IEnergyReceiver, IFluidHandler {

    public static final int BASE_STEPS_PER_TICK = 400;
    public static final int ITEM_BUFFER_CAPACITY = 256;
    public static final Block REPLACE_BLOCK = Blocks.dirt;
    public boolean isCreativeBoosted = false;
    private int storedItems;
    public ForgeDirection facing;
    private Area2d workArea;
    private Queue<Area2d> nextWorkAreas = new LinkedList<>();
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

    public void scanForWorkAreaFromMarkers(EntityPlayer player) {
        boolean foundMarkers = false;
        for (ForgeDirection dir : DirectionUtil.HORIZONTAL_DIRECTIONS) {
            TileEntity te = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord, zCoord + dir.offsetZ);
            if (te instanceof TileEntityEnderMarker marker) {
                @Nullable
                List<TileEntityEnderMarker.FacingVector2i> scanReturn = marker.checkForBoundary(dir);
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
                        return;
                        // Or do we have a more complex rectilinear polygon as defined by many points
                    } else {
                        // DEBUG: Clear work area of debug blocks
                        Vector2i low = new Vector2i(Integer.MAX_VALUE);
                        Vector2i high = new Vector2i(Integer.MIN_VALUE);
                        for (Vector2i point : scanReturn) {
                            if (point.x < low.x) {
                                low.x = point.x;
                            }
                            if (point.y < low.y) {
                                low.y = point.y;
                            }
                            if (point.x > high.x) {
                                high.x = point.x;
                            }
                            if (point.y > high.y) {
                                high.y = point.y;
                            }
                        }
                        LinkedList<Area2d> subRectangles;

                        for (int x = low.x; x <= high.x; x++) {
                            for (int z = low.y; z <= high.y; z++) {
                                worldObj.setBlock(x, this.yCoord - 1, z, Blocks.grass);
                            }
                        }

                        for (int dy = 1; dy < 51; dy++) {
                            for (int x = low.x; x <= high.x; x++) {
                                for (int z = low.y; z <= high.y; z++) {
                                    worldObj.setBlock(x, this.yCoord + dy, z, Blocks.air);
                                }
                            }
                        }

                        for (Vector2i point : scanReturn) {
                            worldObj
                                .setBlock(point.x, this.yCoord + 2, point.y, Blocks.brick_block);
                        }

                        try {
                             subRectangles = computeRectanglesFromRectilinearPointPolygon(scanReturn);
                        } catch (RuntimeException e) {
                            StackTraceElement[] stackTrace = e.getStackTrace();
                            StackTraceElement lastElement = stackTrace[0];
                            String lastFileAndLine = lastElement.getFileName() + ":" + lastElement.getLineNumber();
                            player.addChatComponentMessage(
                                new ChatComponentText(
                                    String.format(
                                        "Ender marker at (%d %d %d) failed to set up a fence boundary: %s - [%s:%s]",
                                        marker.xCoord,
                                        marker.yCoord,
                                        marker.zCoord,
                                        e.getMessage(),
                                        lastElement.getFileName(),
                                        lastElement.getLineNumber())));
                            return;
                        }
                        return;
                        //nextWorkAreas = subRectangles;
                    }
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

    private LinkedList<Area2d> computeRectanglesFromRectilinearPointPolygon(List<TileEntityEnderMarker.FacingVector2i> points) {
        RectilinearPointPoly poly = new RectilinearPointPoly(points);
        LinkedList<Area2d> subAreas = new LinkedList<>();
        UtilitiesInExcess.LOG.info("Starting subarea compute from {} markers", points.size());
        int color = 0;

        while (poly.canFormRectangle()) {
            FacingVector2i rectMinPoint = poly.shiftPoint();

            if (worldObj.getBlock(rectMinPoint.x, this.yCoord - 2, rectMinPoint.y) == Blocks.diamond_block) {
                UtilitiesInExcess.chat("Point-Info: " + rectMinPoint);
            }

            // Is this a bottom right corner?
            if (rectMinPoint.hasConnectionTowards(ForgeDirection.EAST) && rectMinPoint.hasConnectionTowards(ForgeDirection.NORTH)) {
                UtilitiesInExcess.chat("Skipping bottom right corner at " + rectMinPoint);
                continue;
            }

            // Is this a remainder on the lowest level from a new rect?
            if (rectMinPoint.y == poly.minY && poly.getXsAtY(rectMinPoint.y) == 0) {
                UtilitiesInExcess.chat("Skipping lowest level remainder at " + rectMinPoint);
                continue;
            }

            @Nullable
            Integer highXBound = null;
            @Nullable
            Integer highYBound = null;
            for (FacingVector2i higherPoint : poly.remainingPoints) {
                // Don't include if y is below our minimum y or on the same as what we already see as the lower y bound
                if (higherPoint.y < rectMinPoint.y || (highYBound != null && higherPoint.y > highYBound)) continue;
                // Do we have an early simple turn left?
                if (higherPoint.x == rectMinPoint.x) {
                    if (highYBound == null && (higherPoint.hasConnectionTowards(ForgeDirection.EAST) || higherPoint.hasConnectionTowards(ForgeDirection.WEST)))
                        highYBound = higherPoint.y;
                    // Don't include points for x bound on the same x (left line)
                    continue;
                }
                if (higherPoint.y == rectMinPoint.y && higherPoint.hasConnectionTowards(ForgeDirection.WEST) && higherPoint.hasConnectionTowards(ForgeDirection.NORTH)) {
                    // Don't take a left turn as the end of this rectangle
                    continue;
                }

                highXBound = higherPoint.x;
                if (worldObj.getBlock(higherPoint.x, this.yCoord - 2, higherPoint.y) == Blocks.diamond_block) {
                    UtilitiesInExcess.chat("Point-Info for color " + color + ": " + higherPoint);
                }

                if (higherPoint.hasConnectionTowards(ForgeDirection.SOUTH)) {
                    if (higherPoint.hasConnectionTowards(ForgeDirection.WEST)) {
                        if (rectMinPoint.y == higherPoint.y) {
                            break;
                        } else if (highYBound == null || highYBound > higherPoint.y) {
                            highYBound = higherPoint.y;
                        }
                        //break;
                    } else {
                        break;
                    }
                }
                if (higherPoint.hasConnectionTowards(ForgeDirection.NORTH)) {
                    break;
                }
            }

            if (highXBound == null) {
                throw new RuntimeException(
                    "Failed to find right side boundary of rectangle starting at x " + rectMinPoint.x);
            }

            if (highYBound == null) {


            var iter = poly.remainingPoints.iterator();
            @Nullable Integer unsafeHighYBound = null;
            while (iter.hasNext()) {
                FacingVector2i higherPoint = iter.next();
                // Don't include points below our minimum y,
                // Don't include points which have an x outside the x bounds
                // (in theory higherPoint.x >= rectMinPoint.x is redundant since rectMinPoint.x should be the lowest
                // available x left, but is there for visibility),
                // Don't include points with a y value that is higher than an already present high y bound
                if (higherPoint.y <= rectMinPoint.y || higherPoint.x < rectMinPoint.x)
                    continue;
                // Stop if we are outside x bounds
                if (higherPoint.x > highXBound) break;

                if (worldObj.getBlock(higherPoint.x, this.yCoord - 2, higherPoint.y) == Blocks.diamond_block) {
                    UtilitiesInExcess.chat("Point-Info for color " + color + ": " + higherPoint);
                }

                if (higherPoint.hasConnectionTowards(ForgeDirection.WEST) && higherPoint.x > rectMinPoint.x) {
                    if (highYBound != null) {
                        if (highYBound > higherPoint.y) {
                            // Found a boundary that would close the rectangle on the right side
                            highYBound = higherPoint.y;
                        }
                    } else {
                        highYBound = higherPoint.y;
                    }
                    continue;
                }
                if (higherPoint.hasConnectionTowards(ForgeDirection.EAST) && higherPoint.x <= highXBound) {
                    if (highYBound != null) {
                        if (highYBound > higherPoint.y) {
                            // Found a boundary that would close the rectangle on the right side
                            highYBound = higherPoint.y;
                        }
                    } else {
                        highYBound = higherPoint.y;
                    }
                    continue;
                }

                if (highYBound == null) {
                    unsafeHighYBound = higherPoint.y;
                }

                if (!iter.hasNext() && higherPoint.hasConnectionTowards(ForgeDirection.NORTH)) {
                    unsafeHighYBound = higherPoint.y;
                }
            }
            if (unsafeHighYBound != null && highYBound == null) {
                highYBound = unsafeHighYBound;
            }
            }

            if (highYBound == null) {
                throw new RuntimeException(
                    "Failed to find top side boundary of rectangle starting at " + rectMinPoint);
            }

            FacingVector2i rectMaxPoint = new FacingVector2i(highXBound, highYBound, ForgeDirection.UNKNOWN);
            FacingVector2i topLeftCorner = new FacingVector2i(highXBound, rectMinPoint.y, ForgeDirection.UNKNOWN);
            FacingVector2i bottomRightCorner = new FacingVector2i(rectMinPoint.x, highYBound, Stream.of(ForgeDirection.EAST, ForgeDirection.SOUTH).collect(Collectors.toList()));


            // Finished sub-rectangle, add it to the list and remove all points on the edges of it
            Area2d subArea = new Area2d(rectMinPoint, rectMaxPoint, new Vector4i(0, 0, 0, 0));

            boolean isLineArea = subArea.width <= 1 || subArea.height <= 1;
            subAreas.add(subArea);

            HashSet<FacingVector2i> containedPoints = poly.getRemainingPointsInRect(rectMinPoint, rectMaxPoint);
            for (FacingVector2i containedPoint : containedPoints) {
                // Keep corners on the top side (if not present, will be added later below)
                if (containedPoint.x == rectMaxPoint.x && containedPoint.y == rectMaxPoint.y) {
                    if (containedPoint.hasConnectionTowards(ForgeDirection.NORTH)) {
                        // We were the connection north, that has now moved up with this rectangle
                        containedPoint.facings.remove(ForgeDirection.NORTH);
                    } else if (containedPoint.hasConnectionTowards(ForgeDirection.SOUTH) && containedPoint.hasConnectionTowards(ForgeDirection.EAST)) {
                        // needed?
                        containedPoint.facings.remove(ForgeDirection.SOUTH);
                    }
                    continue;
                }
                if (containedPoint.x == topLeftCorner.x && containedPoint.y == topLeftCorner.y) continue;

                // Is point on the top edge and has a connection to something further out that isn't covered by this rectangle?
                if (containedPoint.y != rectMaxPoint.y && containedPoint.hasConnectionTowards(ForgeDirection.EAST)) {
                    // Was point connected to the left boundary that we moved right with this rectangle?
                    if (containedPoint.hasConnectionTowards(ForgeDirection.NORTH)) {
                        containedPoint.facings.remove(ForgeDirection.NORTH);
                        containedPoint.facings.add(ForgeDirection.SOUTH);
                    }
                    continue;
                }
                if (containedPoint.y == rectMaxPoint.y && containedPoint.hasConnectionTowards(ForgeDirection.SOUTH) && !containedPoint.hasConnectionTowards(ForgeDirection.EAST)) {
                    // Point is on the top edge and has a connection to the right, leave it for later rectangles
                    continue;
                }
                if (containedPoint.y == rectMinPoint.y && containedPoint.x > rectMinPoint.x && containedPoint.hasConnectionTowards(ForgeDirection.NORTH)) {
                    // Point is on the left edge and has a connection to a lower rectangle, leave it for later rectangles
                    continue;
                }
                if (worldObj.getBlock(containedPoint.x, this.yCoord - 2, containedPoint.y) == Blocks.diamond_block) {
                    UtilitiesInExcess.chat("Removing point at " + containedPoint + " for subarea " + subArea);
                }
                poly.removeFromRemainingPoints(containedPoint);
            }

            // DEBUG: Draw new sub area on floor
            for (int x = subArea.low.x; x <= subArea.high.x; x++) {
                for (int z = subArea.low.y; z <= subArea.high.y; z++) {
                    if (worldObj.getBlock(x, this.yCoord - 1, z) == Blocks.wool || worldObj.getBlock(x, this.yCoord - 1, z) == Blocks.gold_block) {
                        //worldObj.setBlock(x, this.yCoord - 1, z, Blocks.gold_block);
                        //throw new RuntimeException("Overlapping sub-areas detected at " + x + ", " + z);
                    } else {
                        worldObj.setBlock(x, this.yCoord - 1, z, Blocks.wool, color, 2);
                    }
                }
            }

            // Add the new points on the high x-axis that might be relevant to future areas
            if (poly.hasRemaining()) {
                List<FacingVector2i> newPoints = new ArrayList<>();
                // Determine facing of top right corner
                if (containedPoints.contains(topLeftCorner)) {
                    rectMaxPoint.facings.add(ForgeDirection.NORTH);
                }
                if (poly.maxY == rectMaxPoint.y) {
                    rectMaxPoint.facings.add(ForgeDirection.EAST);
                }

                newPoints.add(topLeftCorner);
                newPoints.add(rectMaxPoint);
                poly.unshiftPointsToRemainingPointsIfMissing(newPoints);

            }

            // DEBUG: Show points used for area & remaining points one above that
            for (Vector2i remainingPoint : poly.remainingPoints) {
                worldObj.setBlock(remainingPoint.x, this.yCoord + 2 + subAreas.size() * 2, remainingPoint.y, Blocks.stained_glass, color, 2);
            }
            worldObj.setBlock(rectMinPoint.x, this.yCoord + 1 + subAreas.size() * 2, rectMinPoint.y, Blocks.wool, color, 2);
            worldObj.setBlock(rectMaxPoint.x, this.yCoord + 1 + subAreas.size() * 2, rectMaxPoint.y, Blocks.wool, color, 2);

            color++;
            if (color == 16) {
                color = 0;
            }

            UtilitiesInExcess.LOG.info("Added subarea at {}", subArea.toString());
        }

        return subAreas;
    }

    private static class RectilinearPointPoly {

        LinkedHashSet<FacingVector2i> remainingPoints = new LinkedHashSet<>();
        ArrayDeque<FacingVector2i> stackPoints = new ArrayDeque<>();
        HashMap<Integer, HashSet<Integer>> xMarkersByY = new HashMap<>();
        private int maxY = Integer.MIN_VALUE;
        private int minY = Integer.MAX_VALUE;


        RectilinearPointPoly(List<FacingVector2i> points) {
            points.sort(
                Comparator.comparingInt(FacingVector2i::x)
                    .thenComparingInt(FacingVector2i::y));
            for (FacingVector2i sortedPoint : points) {
                this.stackPoints.add(sortedPoint);
                this.remainingPoints.add(sortedPoint);
                this.xMarkersByY.computeIfAbsent(sortedPoint.y, k -> new HashSet<>()).add(sortedPoint.x);
                if (sortedPoint.y > maxY) maxY = sortedPoint.y;
                if (sortedPoint.y < minY) minY = sortedPoint.y;
            }
        }

        /**
         * Add new points to the remaining points if they are not already present
         */
        void unshiftPointsToRemainingPointsIfMissing(List<FacingVector2i> newPoints) {
            List<FacingVector2i> missingPoints = newPoints.stream()
                .filter((point) -> !remainingPoints.contains(point))
                .collect(Collectors.toList());

            if (!missingPoints.isEmpty()) {
                List<FacingVector2i> points = new ArrayList<>(stackPoints);
                points.addAll(missingPoints);

                stackPoints.clear();
                remainingPoints.clear();
                xMarkersByY.clear();
                maxY = Integer.MIN_VALUE;
                minY = Integer.MAX_VALUE;

                // Same logic as in constructor - sort random remaining pool of points by x & y, then add in low to high
                // order
                points.sort(
                    Comparator.comparingInt(FacingVector2i::x)
                        .thenComparingInt(FacingVector2i::y));
                for (FacingVector2i sortedPoint : points) {
                    this.stackPoints.add(sortedPoint);
                    this.remainingPoints.add(sortedPoint);
                    this.xMarkersByY.computeIfAbsent(sortedPoint.y, k -> new HashSet<>()).add(sortedPoint.x);
                    if (sortedPoint.y > maxY) maxY = sortedPoint.y;
                    if (sortedPoint.y < minY) minY = sortedPoint.y;
                }
            }
        }

        void removeFromRemainingPoints(FacingVector2i point) {
            this.remainingPoints.remove(point);
            this.stackPoints.remove(point);
            this.xMarkersByY.get(point.y).remove(point.x);
            // If we removed the max Y point, recalculate
            if (point.y == maxY) {
                maxY = remainingPoints.stream()
                    .mapToInt(p -> p.y)
                    .max()
                    .orElse(Integer.MIN_VALUE);
                minY = remainingPoints.stream()
                    .mapToInt(p -> p.y)
                    .min()
                    .orElse(Integer.MAX_VALUE);
            }
        }

        HashSet<FacingVector2i> getRemainingPointsInRect(FacingVector2i low, FacingVector2i high) {
            HashSet<FacingVector2i> points = new HashSet<>();
            for (FacingVector2i remainingPoint : this.remainingPoints) {
                if (remainingPoint.x < low.x || remainingPoint.y < low.y || remainingPoint.y > high.y)
                    continue;
                if (remainingPoint.x > high.x) break;
                points.add(remainingPoint);
            }
            return points;
        }

        FacingVector2i shiftPoint() {
            FacingVector2i point = stackPoints.removeFirst();
            this.removeFromRemainingPoints(point);
            return point;
        }

        /**
         * Check if we have remaining points to process
         * This does not check if there are any remaining points but if there are enough points left to form a rectangle
         */
        boolean hasRemaining() {
            return !stackPoints.isEmpty();
        }

        boolean canFormRectangle() {
            return stackPoints.size() >= 4;
        }

        boolean hasPointAt(Vector2i point) {
            return remainingPoints.stream().anyMatch(p -> p.x == point.x && p.y == point.y);
        }

        int getXsAtY(int y) {
            return xMarkersByY.getOrDefault(y, new HashSet<>()).size();
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
            while (brokenBlocksTick < (BASE_STEPS_PER_TICK * (isCreativeBoosted ? 8 : 1)) && stepPos()) {
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
            if (brokenBlocksTick < (BASE_STEPS_PER_TICK * (isCreativeBoosted ? 8 : 1))
                && state == QuarryWorkState.RUNNING) {
                if (nextWorkAreas.isEmpty()) {
                    state = QuarryWorkState.FINISHED;
                    unloadSelf();
                } else {
                    workArea = nextWorkAreas.remove();
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
        public final int width;
        // The height of the entire working area
        public final int height;
        // The distance to the closest lower x chunk border from the high x bound
        public final int chunkOffX;

        public Area2d(Vector2i first, Vector2i second, Vector4i shrinkMatrix) {
            int lowX = Math.min(first.x, second.x) + (shrinkMatrix.x); // Side: left
            int lowZ = Math.min(first.y, second.y) + (shrinkMatrix.y); // Side: bottom
            int highX = Math.max(first.x, second.x) - (shrinkMatrix.z); // Side: right
            int highZ = Math.max(first.y, second.y) - (shrinkMatrix.w); // Side: top
            this.low = new Vector2i(lowX, lowZ);
            this.high = new Vector2i(highX, highZ);
            this.width = highX - lowX;
            this.height = highZ - lowZ;
            this.chunkOffX = highX - (highX & -16);
        }

        public Area2d(int x1, int z1, int x2, int z2) {
            this(new Vector2i(x1, z1), new Vector2i(x2, z2));
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
