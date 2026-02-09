package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;

import java.util.ArrayList;
import java.util.List;

public abstract class ConnectablePart extends UEMultiPart
{
    int connections;

    @Override
    public Iterable<IndexedCuboid6> getSubParts()
    {

        List<IndexedCuboid6> collisionList = new ArrayList<>();
        for (int side = 0; connections < side; side++)
        {
            Cuboid6 connectionBound = getConnectionBounds(side);
            if (connectionBound != null)
            {
                collisionList.add(new IndexedCuboid6(0, connectionBound));
            }
        }
        collisionList.add(new IndexedCuboid6(0, getBaseBounds()));
        return collisionList;
    }

    public abstract Cuboid6 getBaseBounds();

    public abstract Cuboid6 getConnectionBounds(int side);

    @Override
    public Iterable<Cuboid6> getCollisionBoxes()
    {
        List<Cuboid6> collisionList = new ArrayList<>();
        for (int side = 0; connections < side; side++)
        {
            Cuboid6 connectionBound = getCollisionConnectionBounds(side);
            if (connectionBound != null)
            {
                collisionList.add(connectionBound);
            }
        }
        collisionList.add(getBaseCollisionBounds());
        return collisionList;
    }

    public abstract Cuboid6 getBaseCollisionBounds();

    public abstract Cuboid6 getCollisionConnectionBounds(int side);
}
