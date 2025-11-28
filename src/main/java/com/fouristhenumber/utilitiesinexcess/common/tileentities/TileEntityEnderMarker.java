package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector4i;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.utils.IFacingTE;
import com.fouristhenumber.utilitiesinexcess.utils.Tuple;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class TileEntityEnderMarker extends TileEntity implements IFacingTE {

    public static final ForgeDirection[] HORIZONTAL_DIRECTIONS = new ForgeDirection[] { ForgeDirection.NORTH,
        ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST };
    public static final ConcurrentHashMap<Integer, ConcurrentHashMap<BlockPos, TileEntityEnderMarker>> registeredMarkers =
        new ConcurrentHashMap<>();

    public final HashMap<ForgeDirection, Tuple<@Nullable TileEntityEnderMarker, BlockPos>> alignedMarkers = new HashMap<>();
    private ForgeDirection facing = ForgeDirection.UNKNOWN;
    private int activeDirections = 0;

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

    public static ForgeDirection turnRight90(ForgeDirection dir) {
        return switch (dir) {
            case EAST -> ForgeDirection.SOUTH;
            case NORTH -> ForgeDirection.EAST;
            case SOUTH -> ForgeDirection.WEST;
            case WEST -> ForgeDirection.NORTH;
            default -> dir;
        };
    }

    public static ForgeDirection turnLeft90(ForgeDirection dir) {
        return switch (dir) {
            case EAST -> ForgeDirection.NORTH;
            case NORTH -> ForgeDirection.WEST;
            case SOUTH -> ForgeDirection.EAST;
            case WEST -> ForgeDirection.SOUTH;
            default -> dir;
        };
    }

    public @Nullable Vector4i checkForBoundary(ForgeDirection starterFacing) {
        Tuple<TileEntityEnderMarker, BlockPos> secondCorner = alignedMarkers.getOrDefault(starterFacing, null);
        if (secondCorner != null && secondCorner.getKey() != null) {
            Tuple<TileEntityEnderMarker, BlockPos> thirdCorner = Optional
                .ofNullable(secondCorner.getKey().alignedMarkers.getOrDefault(turnRight90(starterFacing), null))
                .orElse(secondCorner.getKey().alignedMarkers.getOrDefault(turnLeft90(starterFacing), null));
            if (thirdCorner != null) {
                return new Vector4i(this.xCoord, this.zCoord, thirdCorner.getValue().x, thirdCorner.getValue().z);
            }
        }
        return null;
    }

    public void checkForAlignedMarkers() {
        ConcurrentHashMap<BlockPos, TileEntityEnderMarker> dimRegistry = getRegistryForDimension();
        // ArrayList<ForgeDirection> staleMarkers = new ArrayList<>();
//        for (Map.Entry<ForgeDirection, Tuple<@Nullable TileEntityEnderMarker, BlockPos>> entry : alignedMarkers
//            .entrySet()) {
//            if (entry.getValue()
//                .getKey() == null) {
//                BlockPos alignedMarkerPos = entry.getValue()
//                    .getValue();
//                worldObj.getChunkProvider()
//                    .loadChunk(alignedMarkerPos.x >> 4, alignedMarkerPos.z >> 4);
//                TileEntity te = worldObj.getTileEntity(alignedMarkerPos.x, alignedMarkerPos.y, alignedMarkerPos.z);
//                if (te instanceof TileEntityEnderMarker marker) {
//                    setAlignedMarker(entry.getKey(), marker);
//                    marker.setAlignedMarker(
//                        entry.getKey()
//                            .getOpposite(),
//                        this);
//                } else {
//                    removeAlignedMarker(entry.getKey());
//                }
//                // staleMarkers.add(entry.getKey());
//            }
//        }
        // staleMarkers.forEach(alignedMarkers::remove);
        //alignedMarkers.clear();
        for (ForgeDirection dir : HORIZONTAL_DIRECTIONS) {
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
                            setAlignedMarker(dir, entry.getValue());
                            entry.getValue()
                                .setAlignedMarker(dir.getOpposite(), this);
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

    public void teardownConnections() {
        getRegistryForDimension().remove(new BlockPos(this.xCoord, this.yCoord, this.zCoord));
        for (Map.Entry<ForgeDirection, Tuple<@Nullable TileEntityEnderMarker, BlockPos>> alignedMarker : alignedMarkers
            .entrySet()) {
            TileEntityEnderMarker markerTE = alignedMarker.getValue().getKey();
            if (markerTE != null) {
                    markerTE.removeAlignedMarker(alignedMarker.getKey().getOpposite());
                    markerTE.checkForAlignedMarkers();
            } else {
                BlockPos alignedMarkerPos = alignedMarker.getValue().getValue();
                TileEntity te = worldObj.getTileEntity(alignedMarkerPos.x, alignedMarkerPos.y, alignedMarkerPos.z);
                if (te instanceof TileEntityEnderMarker marker) {
                    marker.removeAlignedMarker(alignedMarker.getKey().getOpposite());
                    marker.checkForAlignedMarkers();
                }
            }
        }
        alignedMarkers.clear();
    }

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
    public ForgeDirection getFacing() {
        return this.facing;
    }

    @Override
    public void setFacing(ForgeDirection newFacing) {
        this.facing = newFacing;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readFacingFromNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeFacingToNBT(compound);
    }
}
