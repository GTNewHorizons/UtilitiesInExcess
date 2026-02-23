package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.BlockMultipart;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumMap;
import java.util.Map;

public abstract class ConnectablePart extends MaterialBasedPart
{

    public static final Map<ForgeDirection, ForgeDirection[]> iteratorKey;
    static {
        Map<ForgeDirection, ForgeDirection[]> map = new EnumMap<>(ForgeDirection.class);

        map.put(ForgeDirection.DOWN,  new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.WEST, ForgeDirection.SOUTH, ForgeDirection.EAST});
        map.put(ForgeDirection.UP,    new ForgeDirection[]{ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.EAST});
        map.put(ForgeDirection.EAST,  new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.DOWN, ForgeDirection.SOUTH, ForgeDirection.UP});
        map.put(ForgeDirection.WEST,  new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.UP, ForgeDirection.SOUTH, ForgeDirection.DOWN});
        map.put(ForgeDirection.NORTH, new ForgeDirection[]{ForgeDirection.UP, ForgeDirection.WEST, ForgeDirection.DOWN, ForgeDirection.EAST});
        map.put(ForgeDirection.SOUTH, new ForgeDirection[]{ForgeDirection.DOWN, ForgeDirection.WEST, ForgeDirection.UP, ForgeDirection.EAST});

        iteratorKey = map;
    }


    // What direction is "down" for the connectable part
    public ForgeDirection downDirection;


    ConnectablePart(int side)
    {
        this.downDirection = ForgeDirection.getOrientation(side);
    }

    // Is there a better way to implement this?
    protected boolean doPartsOccludeDirection(ForgeDirection side)
    {
        for(TMultiPart part: tile().jPartList())
        {
            if (part != this)
            {
                for (Cuboid6 cube : part.getSubParts())
                {
                    if (cube.intersects(getConnectionInDirection(side)))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public abstract Cuboid6 getConnectionInDirection(ForgeDirection side);

    // TODO CACHE THESE RESULTS
    public boolean canConnectOnSide(ForgeDirection side) {
        if (tile() == null)
        {
            return false;
        }
        if (doPartsOccludeDirection(side))
        {
            return false;
        }
        int offsetX = x() + side.offsetX;
        int offsetY = y() + side.offsetY;
        int offsetZ = z() + side.offsetZ;
        Block block = world().getBlock(offsetX, offsetY, offsetZ);
        if (block.isSideSolid(world(), offsetX, offsetY, offsetZ, side.getOpposite()) &&
            block.renderAsNormalBlock() &&
            block.getMaterial().isOpaque())
        {
            return true;
        }
        else if (block instanceof BlockMultipart)
        {
            if (world().getTileEntity(offsetX, offsetY, offsetZ) instanceof TileMultipart mpTile)
            {
                for (TMultiPart part : mpTile.jPartList())
                {
                    if (part instanceof ConnectablePart cPart &&
                        part.getClass() == this.getClass() &&
                        cPart.downDirection == this.downDirection &&
                        !cPart.doPartsOccludeDirection(side.getOpposite()))
                    {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public int getConnectionMask()
    {
        int mask = 0b0000;
        ForgeDirection[] iteratorList = iteratorKey.get(downDirection);
        for (int i = 0; i < iteratorList.length; i++)
        {
            if (canConnectOnSide(iteratorList[i]))
            {
                mask = mask | 1 << i;
            }
        }
        return mask;
    }

    public int getCullMask(int relativeDirection)
    {
        return switch (relativeDirection) {
            case (1), (3) -> transformCullMask(0b110000);
            case (0), (2) -> transformCullMask(0b001100);
            default -> 0;
        };
    }

    private int transformCullMask(int mask)
    {
        switch(downDirection)
        {
            case DOWN, UP -> {
                return mask;
            }
            case NORTH, SOUTH ->
            {
                if (mask == 0b001100)
                {
                    return mask >> 2;
                }
                return mask;
            }
            case WEST, EAST ->
            {
                if (mask == 0b110000)
                {
                    return mask >> 4;
                }
                return mask;
            }
            case UNKNOWN ->
            {
                throw new IllegalArgumentException("Switch falloff in transforming the cullmask in a connectable part.");
            }
        }
        return mask;
    }

    @Override
    public void save(NBTTagCompound tag)
    {
        super.save(tag);
        tag.setInteger("side", this.downDirection.ordinal());
    }

    @Override
    public void load(NBTTagCompound tag)
    {
        super.load(tag);
        downDirection = ForgeDirection.getOrientation(tag.getInteger("side"));
    }

    @Override
    public void writeDesc(MCDataOutput packet)
    {
        super.writeDesc(packet);
        packet.writeInt(this.downDirection.ordinal());
    }

    protected int indexInFrame(ForgeDirection side) {
        ForgeDirection[] frame = iteratorKey.get(downDirection);
        for (int i = 0; i < frame.length; i++) {
            if (frame[i] == side) {
                return i;
            }
        }
        throw new IllegalArgumentException("Somehow multipart connectable is trying to access a direction outside of it's frame!");
    }
}
