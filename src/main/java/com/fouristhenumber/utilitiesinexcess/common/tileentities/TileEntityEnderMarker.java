package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.utils.IFacingTE;
import com.fouristhenumber.utilitiesinexcess.utils.DirectionUtil;
import com.fouristhenumber.utilitiesinexcess.utils.Tuple;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class TileEntityEnderMarker extends TileEntity implements IFacingTE {

    public static final ForgeDirection[] HORIZONTAL_DIRECTIONS = new ForgeDirection[] { ForgeDirection.NORTH,
        ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST };
    public static final ConcurrentHashMap<Integer, ConcurrentHashMap<BlockPos, TileEntityEnderMarker>> registeredMarkers = new ConcurrentHashMap<>();

    public final HashMap<ForgeDirection, Tuple<@Nullable TileEntityEnderMarker, BlockPos>> alignedMarkers = new HashMap<>();
    private ForgeDirection facing = ForgeDirection.UNKNOWN;
    private int activeDirections = 0;
    public MarkerOperationMode operationMode = MarkerOperationMode.DEFAULT;
    private boolean hasChangedMode = false;
    private int cuboidSize = 16;

    @Override
    public void validate() {
        super.validate();
        if (worldObj.isRemote) return;
        // Always update the marker for the current position, for world load and stale cases
        getRegistryForDimension().put(new BlockPos(xCoord, yCoord, zCoord), this);
    }

    // Helper to get the marker register for the current dimension
    private ConcurrentHashMap<BlockPos, TileEntityEnderMarker> getRegistryForDimension() {
        int dim = worldObj.provider.dimensionId;
        return registeredMarkers.computeIfAbsent(dim, k -> new ConcurrentHashMap<>());
    }

    public @Nullable List<Vector2i> checkForBoundary(ForgeDirection starterFacing) {
        return switch (operationMode) {
            case DEFAULT -> boundaryFromThree(starterFacing);
            case SINGLE -> boundaryForSizedCuboid(starterFacing);
            case ARBITRARY_LOOP -> boundaryForArbitraryLoop();
        };
    }

    private @Nullable List<Vector2i> boundaryFromThree(ForgeDirection starterFacing) {
        Tuple<TileEntityEnderMarker, BlockPos> secondCorner = alignedMarkers.getOrDefault(starterFacing, null);
        if (secondCorner != null && secondCorner.getKey() != null) {
            Tuple<TileEntityEnderMarker, BlockPos> thirdCorner = Optional
                .ofNullable(
                    secondCorner.getKey().alignedMarkers.getOrDefault(DirectionUtil.turnRight90(starterFacing), null))
                .orElse(
                    secondCorner.getKey().alignedMarkers.getOrDefault(DirectionUtil.turnLeft90(starterFacing), null));
            if (thirdCorner != null) {
                return Stream.of(new Vector2i(this.xCoord, this.zCoord), new Vector2i(thirdCorner.getValue().x, thirdCorner.getValue().z)).collect(Collectors.toList());
            }
        }
        return null;
    }

    private List<Vector2i> boundaryForSizedCuboid(ForgeDirection facing) {
        BlockPos otherCorner = DirectionUtil.offsetByForward(this.xCoord, this.yCoord, this.zCoord, facing, cuboidSize, 0);
        otherCorner = DirectionUtil.offsetByRight(otherCorner, facing, cuboidSize, 0);
        return Stream.of(new Vector2i(this.xCoord, this.zCoord), new Vector2i(otherCorner.x, otherCorner.z)).collect(Collectors.toList());
    }

    public List<Vector2i> boundaryForArbitraryLoop() {
        ArrayList<StackEntry> stack = new ArrayList<>();
        stack.add(new StackEntry(new LinkedHashMap<>(), this));
        Set<TileEntityEnderMarker> markerChain = null;

        searchStack: do {
            StackEntry entry = stack.remove(stack.size() - 1);

            if (entry.current.alignedMarkers.isEmpty()
                || entry.current.alignedMarkers.size() == 1
                && entry.current.alignedMarkers.get(entry.lastVisited.getValue()) != null
                && entry.current.alignedMarkers.get(entry.lastVisited.getValue()).getKey() == entry.lastVisited.getKey()) {
                entry.current.checkForAlignedMarkers();
                worldObj.setBlock(entry.current.xCoord, entry.current.yCoord + 3, entry.current.zCoord, Blocks.brick_block);
            }

            for (Map.Entry<ForgeDirection, Tuple<@Nullable TileEntityEnderMarker, BlockPos>> otherMarker : entry.current.alignedMarkers.entrySet()) {
                if (otherMarker.getValue().getKey() != null) {
                    // Has not already been visited
                    if (!entry.visitedMarkers.containsKey(otherMarker.getValue().getKey())) {
                        @SuppressWarnings("unchecked") // Same type
                        LinkedHashMap<TileEntityEnderMarker, ForgeDirection> visitedMarkers = (LinkedHashMap<TileEntityEnderMarker, ForgeDirection>) entry.visitedMarkers.clone();
                        StackEntry stackEntry = new StackEntry(visitedMarkers, otherMarker.getValue().getKey(), entry.current, otherMarker.getKey().getOpposite());
                        stack.add(stackEntry);

                        worldObj.setBlock(entry.current.xCoord, entry.current.yCoord + 2, entry.current.zCoord, Blocks.tnt);
                        continue;
                    }

                    // Check if we have completed a loop by arriving at the starter from a different direction
                    if (otherMarker.getValue().getKey() == this && entry.visitedMarkers.get(otherMarker.getValue().getKey()) != otherMarker.getKey() && !entry.visitedMarkers.isEmpty()) {
                        // Append the last marker
                        entry.visitedMarkers.put(entry.current, otherMarker.getKey());
                        markerChain = entry.visitedMarkers.keySet();
                        break searchStack;
                    }
                }
            }
        } while (!stack.isEmpty());

        if (markerChain != null) {
            UtilitiesInExcess.chat("Completed marker chain with " + markerChain.size() + " entries.");
            ArrayList<Vector2i> pointChain = new ArrayList<>(markerChain.size());
            markerChain.forEach((e) -> pointChain.add(new Vector2i(e.xCoord, e.zCoord)));
            return pointChain;
        }
        UtilitiesInExcess.chat("Failed to complete marker chain.");

        return null;
    }

    private static class StackEntry {
        // A map of previously visited markers further down the stack & the direction they were visited from
        LinkedHashMap<TileEntityEnderMarker, ForgeDirection> visitedMarkers;
        TileEntityEnderMarker current;
        Tuple<TileEntityEnderMarker, ForgeDirection> lastVisited;

        StackEntry(LinkedHashMap<TileEntityEnderMarker, ForgeDirection> visitedMarkers, TileEntityEnderMarker current) {
            this.visitedMarkers = visitedMarkers;
            this.current = current;
            this.lastVisited = null;
        }

        StackEntry(LinkedHashMap<TileEntityEnderMarker, ForgeDirection> visitedMarkers, TileEntityEnderMarker current, TileEntityEnderMarker previous, ForgeDirection previousDir) {
            this(visitedMarkers, current);
            this.visitedMarkers.put(previous, previousDir);
            lastVisited = new Tuple<>(previous, previousDir);
        }
    }

    public void checkForAlignedMarkers() {
        this.checkForAlignedMarkers(HORIZONTAL_DIRECTIONS, false);
    }

    public void checkForAlignedMarkers(@NotNull ForgeDirection[] dirs, boolean onlyValidate) {
        ConcurrentHashMap<BlockPos, TileEntityEnderMarker> dimRegistry = getRegistryForDimension();
        for (Map.Entry<ForgeDirection, Tuple<@Nullable TileEntityEnderMarker, BlockPos>> entry : new ArrayList<>(alignedMarkers.entrySet())) {
            if (entry.getValue()
                .getKey() == null) {
                BlockPos alignedMarkerPos = entry.getValue()
                    .getValue();
                worldObj.getChunkProvider()
                    .loadChunk(alignedMarkerPos.x >> 4, alignedMarkerPos.z >> 4);
                TileEntity te = worldObj.getTileEntity(alignedMarkerPos.x, alignedMarkerPos.y, alignedMarkerPos.z);
                if (te instanceof TileEntityEnderMarker marker) {
                    setAlignedMarker(entry.getKey(), marker);
                    marker.setAlignedMarker(
                        entry.getKey()
                            .getOpposite(),
                        this);
                } else {
                    removeAlignedMarker(entry.getKey());
                }
            }
        }

        for (ForgeDirection dir : dirs) {
            if (!alignedMarkers.containsKey(dir)) {
                for (Map.Entry<BlockPos, @Nullable TileEntityEnderMarker> entry : dimRegistry.entrySet()) {
                    if (entry.getValue() != null && entry.getValue() != this
                        && !entry.getValue().alignedMarkers.containsKey(dir.getOpposite())
                        && entry.getKey().y == this.yCoord
                        && (entry.getKey().x == this.xCoord || entry.getKey().z == this.zCoord)) {
                        int dx = entry.getKey().x - this.xCoord;
                        int dz = entry.getKey().z - this.zCoord;

                        // Don't want markers right next to us
                        if (Math.max(Math.abs(dx), Math.abs(dz)) < 2) continue;

                        ForgeDirection markerDirection;
                        if (dx == 0) {
                            markerDirection = dz > 0 ? ForgeDirection.SOUTH : ForgeDirection.NORTH;
                        } else {
                            markerDirection = dx > 0 ? ForgeDirection.EAST : ForgeDirection.WEST;
                        }
                        if (markerDirection == dir) {
                            if (!onlyValidate) {
                                setAlignedMarker(dir, entry.getValue());
                                entry.getValue()
                                    .setAlignedMarker(dir.getOpposite(), this);
                            } else if (!entry.getValue().alignedMarkers.containsKey(dir.getOpposite())
                                && (worldObj.getBlockMetadata(entry.getKey().x, entry.getKey().y, entry.getKey().z)
                                    & (1 << (dir.getOpposite()
                                        .ordinal() - 2)))
                                    != 0) {
                                        // Does the active store for this marker not contain us & but the metadata does?
                                        // Suggests we reloaded with previous connections,
                                        // and since we would usually link from "this" to "entry" unlink (just from the
                                        // meta) instead
                                        entry.getValue()
                                            .removeAlignedMarker(dir.getOpposite());
                                        entry.getValue()
                                            .checkForAlignedMarkers();
                                    }
                        }
                    }
                }
            }
        }
    }

    public void setAlignedMarker(ForgeDirection dir, TileEntityEnderMarker marker) {
        alignedMarkers.put(dir, new Tuple<>(marker, new BlockPos(marker.xCoord, marker.yCoord, marker.zCoord)));
        int prevActiveDirs = this.activeDirections;
        this.activeDirections |= 1 << (dir.ordinal() - 2);
        if (prevActiveDirs != activeDirections) {
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.activeDirections, 2);
        }
    }

    public void removeAlignedMarker(ForgeDirection dir) {
        alignedMarkers.remove(dir);
        int prevActiveDirs = this.activeDirections;
        this.activeDirections &= ~(1 << (dir.ordinal() - 2));
        if (prevActiveDirs != activeDirections) {
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.activeDirections, 2);
        }
    }

    public ForgeDirection[] getActiveDirs() {
        List<ForgeDirection> activeDirList = new ArrayList<>();
        for (ForgeDirection dir : HORIZONTAL_DIRECTIONS) {
            // Is the bit for this direction set?
            if ((activeDirections & (1 << (dir.ordinal() - 2))) != 0) {
                activeDirList.add(dir);
            }
        }
        return activeDirList.toArray(new ForgeDirection[0]);
    }

    public void teardownConnections() {
        checkForAlignedMarkers(getActiveDirs(), true);
        getRegistryForDimension().remove(new BlockPos(this.xCoord, this.yCoord, this.zCoord));
        for (Map.Entry<ForgeDirection, Tuple<@Nullable TileEntityEnderMarker, BlockPos>> alignedMarker : alignedMarkers
            .entrySet()) {
            TileEntityEnderMarker markerTE = alignedMarker.getValue()
                .getKey();
            if (markerTE != null) {
                markerTE.removeAlignedMarker(
                    alignedMarker.getKey()
                        .getOpposite());
                markerTE.checkForAlignedMarkers();
            } else {
                BlockPos alignedMarkerPos = alignedMarker.getValue()
                    .getValue();
                TileEntity te = worldObj.getTileEntity(alignedMarkerPos.x, alignedMarkerPos.y, alignedMarkerPos.z);
                if (te instanceof TileEntityEnderMarker marker) {
                    marker.removeAlignedMarker(
                        alignedMarker.getKey()
                            .getOpposite());
                    marker.checkForAlignedMarkers();
                }
            }
        }
        alignedMarkers.clear();
    }

    public String getMode() {
        return switch(operationMode) {
            case DEFAULT -> "Marker is set to establish a rectangular fence from the first 3 in chain.";
            case SINGLE -> "Marker is set to establish a cuboid of length " + this.cuboidSize + ". Sneak + R-Click to adjust size.";
            case ARBITRARY_LOOP -> "Marker is set to establish a full loop of markers that make up a rectilinear polygon back this marker of arbitrary size and shape.";
        };
    }

    public void rotateMode() {
        // Don't change the mode the first time, want them to be able to read the default
        if (!hasChangedMode) {
            hasChangedMode = true;
            return;
        }
        int ord = operationMode.ordinal() + 1;
        if (ord >= MarkerOperationMode.values().length) {
            ord = 0;
        }
        operationMode = MarkerOperationMode.values()[ord];
    }

    public int increaseCuboidSize() {
        int nextSize = cuboidSize * 2;
        cuboidSize = nextSize > 512 ? 2 : cuboidSize * 2;
        return cuboidSize;
    }

    // IFacingTE
    @Override
    public ForgeDirection getFacing() {
        return this.facing;
    }

    @Override
    public void setFacing(ForgeDirection newFacing) {
        this.facing = newFacing;
    }

    // TileEntity
    @Override
    public void invalidate() {
        super.invalidate();
        if (!worldObj.isRemote) {
            teardownConnections();
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (!worldObj.isRemote) {
            teardownConnections();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readFacingFromNBT(compound);
        this.activeDirections = compound.getInteger("meta");
        int dim = compound.getInteger("dim");
        // Can't use getRegistryForDimension yet since worldobj is null
        registeredMarkers.computeIfAbsent(dim, k -> new ConcurrentHashMap<>())
            .put(new BlockPos(xCoord, yCoord, zCoord), this);
        this.operationMode = MarkerOperationMode.values()[compound.getInteger("mode")];
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeFacingToNBT(compound);
        compound.setInteger("meta", this.activeDirections);
        compound.setInteger("dim", this.worldObj.provider.dimensionId);
        compound.setInteger("mode", this.operationMode.ordinal());
    }

    public enum MarkerOperationMode {
        DEFAULT,
        SINGLE,
        ARBITRARY_LOOP
    }
}
