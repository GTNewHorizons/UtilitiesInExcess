package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.BlockMultipart;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public abstract class ConnectablePart extends UEMultiPart
{

    // Connection mask for neighbors NSEW order sames as forge direction enum order
    int neighbors = 0;

    @Override
    public Iterable<IndexedCuboid6> getSubParts()
    {
        List<IndexedCuboid6> collisionList = new ArrayList<>();
        for (int side = 2; ForgeDirection.values().length > side; side++)
        {
            if (canConnectOnSide(ForgeDirection.getOrientation(side)))
            {
                Cuboid6 connectionBound = getConnectionBounds(ForgeDirection.getOrientation(side));
                if (connectionBound != null)
                {
                    collisionList.add(new IndexedCuboid6(0, connectionBound));
                }
            }
        }
        collisionList.add(new IndexedCuboid6(0, getBaseBounds()));
        return collisionList;
    }

    public abstract Cuboid6 getBaseBounds();

    public abstract Cuboid6 getConnectionBounds(ForgeDirection side);

    @Override
    public Iterable<Cuboid6> getCollisionBoxes()
    {
        List<Cuboid6> collisionList = new ArrayList<>();
        for (int side = 0; ForgeDirection.values().length > side; side++)
        {
            if (canConnectOnSide(ForgeDirection.getOrientation(side)))
            {
                Cuboid6 connectionBound = getCollisionConnectionBounds(ForgeDirection.getOrientation(side));
                if (connectionBound != null) {
                    collisionList.add(connectionBound);
                }
            }
        }
        collisionList.add(getBaseCollisionBounds());
        return collisionList;
    }

    public abstract Cuboid6 getBaseCollisionBounds();

    public abstract Cuboid6 getCollisionConnectionBounds(ForgeDirection side);

    protected boolean doPartsOccludeDirection(ForgeDirection side)
    {
        return false;
    }

    protected boolean canConnectOnSide(ForgeDirection side)
    {
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
                Class<? extends TMultiPart> myClass = this.getClass();
                for (TMultiPart part : mpTile.jPartList())
                {
                    if (part.getClass() == myClass && !doPartsOccludeDirection(side.getOpposite()))
                    {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }
}
