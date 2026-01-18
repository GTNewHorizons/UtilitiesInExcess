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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
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

    public @Nullable List<Vector2i> checkForBoundary(ForgeDirection starterFacing, EntityPlayer player) {
        return switch (operationMode) {
            case DEFAULT -> boundaryFromThree(starterFacing);
            case SINGLE -> boundaryForSizedCuboid(starterFacing);
            case ARBITRARY_LOOP -> boundaryForArbitraryLoop(player);
        };
    }

    private @Nullable List<Vector2i> boundaryFromThree(ForgeDirection starterFacing) {
        for (ForgeDirection dir : HORIZONTAL_DIRECTIONS) {
            // Don't check the direction back towards the quarry
            if (dir == starterFacing.getOpposite()) continue;

            Tuple<TileEntityEnderMarker, BlockPos> secondCorner = alignedMarkers.getOrDefault(dir, null);
            if (secondCorner != null && secondCorner.getKey() != null) {
                Tuple<TileEntityEnderMarker, BlockPos> thirdCorner = Optional
                    .ofNullable(secondCorner.getKey().alignedMarkers.getOrDefault(DirectionUtil.turnRight90(dir), null))
                    .orElse(secondCorner.getKey().alignedMarkers.getOrDefault(DirectionUtil.turnLeft90(dir), null));
                if (thirdCorner != null) {
                    return Stream
                        .of(
                            new Vector2i(this.xCoord, this.zCoord),
                            new Vector2i(thirdCorner.getValue().x, thirdCorner.getValue().z))
                        .collect(Collectors.toList());
                }
            }
        }
        return null;
    }

    private List<Vector2i> boundaryForSizedCuboid(ForgeDirection facing) {
        BlockPos otherCorner = DirectionUtil
            .offsetByForward(this.xCoord, this.yCoord, this.zCoord, facing, cuboidSize, 0);
        otherCorner = DirectionUtil.offsetByRight(otherCorner, facing, cuboidSize, 0);
        return Stream.of(new Vector2i(this.xCoord, this.zCoord), new Vector2i(otherCorner.x, otherCorner.z))
            .collect(Collectors.toList());
    }

    public List<Vector2i> boundaryForArbitraryLoop(EntityPlayer player) {
        ArrayList<StackEntry> stack = new ArrayList<>();
        stack.add(new StackEntry(new LinkedHashMap<>(), this));
        StackEntry lastVisited;
        Set<TileEntityEnderMarker> markerChain = null;

        searchStack: do {
            StackEntry entry = stack.remove(stack.size() - 1);
            lastVisited = entry;

            if (entry.current.operationMode != MarkerOperationMode.ARBITRARY_LOOP) {
                // Set to arbitrary loop mode to avoid automatic linking to more than 2 directions
                entry.current.operationMode = MarkerOperationMode.ARBITRARY_LOOP;
            }

            // If we have no discovered aligned markers yet, or the only discovered marker
            // is the one we came from (a single back-connection), refresh discovery so we
            // can populate any missing/stale connections (e.g. after chunk reload).
            if (entry.current.alignedMarkers.isEmpty() || entry.current.alignedMarkers.size() == 1
                && (entry.lastVisited == null || (entry.current.alignedMarkers.get(entry.lastVisited.getValue()) != null
                    && entry.current.alignedMarkers.get(entry.lastVisited.getValue())
                        .getKey() == entry.lastVisited.getKey()))) {
                entry.current.checkForAlignedMarkers(entry.current.getActiveDirsFromMeta(), false, true);
            }

            int realizedDirections = 0;
            for (Map.Entry<ForgeDirection, Tuple<@Nullable TileEntityEnderMarker, BlockPos>> otherMarker : entry.current.alignedMarkers
                .entrySet()) {
                if (otherMarker.getValue()
                    .getKey() != null) {
                    // Has not already been visited & we have not already queued 2 new directions from this marker
                    // (avoid markers with >2 connections)
                    if (!entry.visitedMarkers.containsKey(
                        otherMarker.getValue()
                            .getKey())
                        && realizedDirections < 2) {
                        @SuppressWarnings("unchecked") // Same type
                        LinkedHashMap<TileEntityEnderMarker, ForgeDirection> visitedMarkers = (LinkedHashMap<TileEntityEnderMarker, ForgeDirection>) entry.visitedMarkers
                            .clone();
                        StackEntry stackEntry = new StackEntry(
                            visitedMarkers,
                            otherMarker.getValue()
                                .getKey(),
                            entry.current,
                            otherMarker.getKey()
                                .getOpposite());
                        stack.add(stackEntry);
                        realizedDirections++;
                        continue;
                    }

                    // Check if we have completed a loop by arriving at the starter from a different direction
                    if (otherMarker.getValue()
                        .getKey() == this
                        && entry.visitedMarkers.get(
                            otherMarker.getValue()
                                .getKey())
                            != otherMarker.getKey()
                        && !entry.visitedMarkers.isEmpty()) {
                        // Append the last marker
                        entry.visitedMarkers.put(entry.current, otherMarker.getKey());
                        markerChain = entry.visitedMarkers.keySet();
                        break searchStack;
                    }
                }
            }
        } while (!stack.isEmpty());

        if (markerChain != null) {
            ArrayList<Vector2i> pointChain = new ArrayList<>(markerChain.size());
            markerChain.forEach((e) -> pointChain.add(new Vector2i(e.xCoord, e.zCoord)));
            return pointChain;
        }

        player.addChatComponentMessage(
            new ChatComponentText(
                String.format(
                    StatCollector.translateToLocal("uie.quarry.scanmessage.5"),
                    lastVisited.current.xCoord,
                    lastVisited.current.yCoord,
                    lastVisited.current.zCoord)));

        // Spawn particles to show where the chain broke
        for (Object obj : worldObj.playerEntities) {
            EntityPlayerMP playerMP = (EntityPlayerMP) obj;
            double distance = player.getDistanceSq(xCoord, yCoord, zCoord);
            if (distance < 1024) { // 32 block range
                S2APacketParticles packet = new S2APacketParticles(
                    "flame",
                    (float) (lastVisited.current.xCoord + 0.5),
                    (float) (lastVisited.current.yCoord + 3.25),
                    (float) (lastVisited.current.zCoord + 0.5),
                    0.0F,
                    1F,
                    0.0F,
                    0.04F, // speed
                    400 // count
                );
                playerMP.playerNetServerHandler.sendPacket(packet);
            }
        }

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

        StackEntry(LinkedHashMap<TileEntityEnderMarker, ForgeDirection> visitedMarkers, TileEntityEnderMarker current,
            TileEntityEnderMarker previous, ForgeDirection previousDir) {
            this(visitedMarkers, current);
            this.visitedMarkers.put(previous, previousDir);
            lastVisited = new Tuple<>(previous, previousDir);
        }
    }

    public void checkForAlignedMarkers() {
        this.checkForAlignedMarkers(HORIZONTAL_DIRECTIONS, false, false);
    }

    /**
     * Checks for and establishes connections to aligned markers in the specified directions.
     * <p>
     * This method performs two main operations:
     * <p>
     * Attempts to resolve any null aligned markers by loading their chunks and retrieving
     * the actual {@link TileEntityEnderMarker} instances from saved positions.</li>
     *
     * Scans the dimension registry for new markers that can be connected in the given directions,
     * respecting ARBITRARY_LOOP operation mode constraints.</li>
     *
     * @param dirs                The {@link ForgeDirection}s to check for aligned markers
     * @param onlyValidate        If true, only validates existing connections without establishing new ones
     * @param forceMetaDirections If true, only accepts connections where the target marker's metadata
     *                            indicates a connection back to this marker - meant to be used to restore stale
     *                            connections
     *
     * @see #setAlignedMarker(ForgeDirection, TileEntityEnderMarker)
     * @see #removeAlignedMarker(ForgeDirection)
     */
    public void checkForAlignedMarkers(@NotNull ForgeDirection[] dirs, boolean onlyValidate,
        boolean forceMetaDirections) {
        ConcurrentHashMap<BlockPos, TileEntityEnderMarker> dimRegistry = getRegistryForDimension();

        // First try to resolve any null aligned markers from saved positions
        for (Map.Entry<ForgeDirection, Tuple<@Nullable TileEntityEnderMarker, BlockPos>> entry : new ArrayList<>(
            alignedMarkers.entrySet())) {
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

        // Now check for any aligned markers in the given directions
        for (ForgeDirection dir : dirs) {
            if (!alignedMarkers.containsKey(dir)) {
                for (Map.Entry<BlockPos, @Nullable TileEntityEnderMarker> entry : dimRegistry.entrySet()) {
                    if (operationMode == MarkerOperationMode.ARBITRARY_LOOP && alignedMarkers.size() >= 2) {
                        // In arbitrary loop mode, only allow 2 connections
                        break;
                    }
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
                            if (forceMetaDirections
                                && (worldObj.getBlockMetadata(entry.getKey().x, entry.getKey().y, entry.getKey().z)
                                    & (1 << (dir.getOpposite()
                                        .ordinal() - 2)))
                                    == 0) {
                                // The other marker's metadata does not indicate a (stale) connection to us, skip
                                continue;
                            }
                            // If this other marker is in loop mode only link if it has free connections (less than 2)
                            if (!onlyValidate && (entry.getValue().operationMode != MarkerOperationMode.ARBITRARY_LOOP
                                || entry.getValue().alignedMarkers.size() < 2)) {
                                setAlignedMarker(dir, entry.getValue());
                                entry.getValue()
                                    .setAlignedMarker(dir.getOpposite(), this);
                                // If we aren't in loop mode and just linked to a marker in loop mode, switch to loop
                                // mode (if we have less than 3 connections)
                                if (entry.getValue().operationMode == MarkerOperationMode.ARBITRARY_LOOP
                                    && this.operationMode != MarkerOperationMode.ARBITRARY_LOOP
                                    && this.alignedMarkers.size() < 3) {
                                    this.operationMode = MarkerOperationMode.ARBITRARY_LOOP;
                                }
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

        // Finally check if the block metadata matches the active directions
        if (worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) != this.activeDirections) {
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.activeDirections, 2);
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

    public ForgeDirection[] getActiveDirsFromMeta() {
        // Make sure the loaded meta matches the blocks meta
        if (worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord)
            .equals(ModBlocks.ENDER_MARKER.get())
            && worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) != this.activeDirections) {
            this.activeDirections = worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        }

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
        checkForAlignedMarkers(getActiveDirsFromMeta(), true, false);
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
        return switch (operationMode) {
            case DEFAULT -> "Marker is set to establish a rectangular fence from the first 3 in chain.";
            case SINGLE -> "Marker is set to establish a cuboid of length " + this.cuboidSize
                + ". Sneak + R-Click to adjust size.";
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
        this.cuboidSize = compound.getInteger("cuboidSize");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeFacingToNBT(compound);
        compound.setInteger("meta", this.activeDirections);
        compound.setInteger("dim", this.worldObj.provider.dimensionId);
        compound.setInteger("mode", this.operationMode.ordinal());
        compound.setInteger("cuboidSize", this.cuboidSize);
    }

    public enum MarkerOperationMode {
        DEFAULT,
        SINGLE,
        ARBITRARY_LOOP
    }
}
