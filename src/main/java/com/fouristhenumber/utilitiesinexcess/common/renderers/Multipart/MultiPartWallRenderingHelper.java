package com.fouristhenumber.utilitiesinexcess.common.renderers.Multipart;

import codechicken.lib.vec.Cuboid6;
import it.unimi.dsi.fastutil.Pair;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;
import java.util.Map;

import static com.fouristhenumber.utilitiesinexcess.utils.CuboidUtils.Rotate90AboutYBlockCenterPos;
import static com.fouristhenumber.utilitiesinexcess.utils.CuboidUtils.RotateCube;
import static com.fouristhenumber.utilitiesinexcess.utils.CuboidUtils.RotateModel;

public class MultiPartWallRenderingHelper
{
    // Selection bounds defaulted to North side (-Z)
    public static final Cuboid6 postBounds = new Cuboid6(.25, 0, .25, .75, 1, .75);
    public static final Cuboid6 connectorBounds = new Cuboid6(0.3125, 0, 0, 0.6875,0.8125, .25);
    public static final Cuboid6 connectorNS = new Cuboid6(0.3125, 0, 0, 0.6875, 0.8125, 1.0);

    // Collision bounds
    public static final Cuboid6 postCollisionBounds = new Cuboid6(.25, 0, .25, .75, 1.5, .75);
    public static final Cuboid6 connectorCollisionBounds = new Cuboid6(0.3125, 0, 0, 0.6875,1.5, .25);

    // Because of some vanilla weirdness we need to make two copies one for bounds that are vertical (as seen above).
    // and one for bounds that are non-vertical. Rubberbanding happens if you don't do this.
    public static final Cuboid6 getPostCollisionBoundsHorizontal = new Cuboid6(.25, 0, .25, .75, 1.0, .75);
    public static final Cuboid6 connectorCollisionBoundsHorizontal = new Cuboid6(0.3125, 0, 0, 0.6875,1.0, .25);

    // Compute model/bounds/collision all at class initialization
    public static final Map<ForgeDirection, Pair<Integer, Cuboid6>[][]> PRECOMPUTED_MODEL;
    public static final Map<ForgeDirection, Cuboid6[][]> PRECOMPUTED_BOUNDS;
    public static final Map<ForgeDirection, Cuboid6[][]> PRECOMPUTED_COLLISION;
    public static final Map<ForgeDirection, Cuboid6[]> PRECOMPUTED_SIMPLE_MODEL;

    static
    {
        // BASIC MODEL
        PRECOMPUTED_MODEL = new HashMap<>();

        // Make our base model in the down direction
        Pair<Integer, Cuboid6>[][] baseModels = new Pair [16][];
        for (int i = 0; i < 16; i++)
        {
            Pair<Integer, Cuboid6>[] values = new Pair [1 + Integer.bitCount(i)];
            values[0] = Pair.of(-1, postBounds);
            if (i > 0)
            {
                int count = 0;
                for (int j = 0; j < 4; j++)
                {
                    if ((i & (1 << j)) != 0)
                    {
                        values[1 + count] = Pair.of(j, Rotate90AboutYBlockCenterPos(connectorBounds, j));
                        count++;
                    }
                }
            }
            baseModels[i] = values;
        }

        PRECOMPUTED_MODEL.put(ForgeDirection.DOWN, baseModels);

        // SIMPLE MODEL
        PRECOMPUTED_SIMPLE_MODEL = new HashMap<>();

        Cuboid6[] baseSimpleModel = new Cuboid6[2];
        baseSimpleModel[0] = connectorNS;
        baseSimpleModel[1] = Rotate90AboutYBlockCenterPos(connectorNS, 1);

        PRECOMPUTED_SIMPLE_MODEL.put(ForgeDirection.DOWN, baseSimpleModel);

        // BOUNDS
        PRECOMPUTED_BOUNDS = new HashMap<>();

        // Make our base bounds in the down direction
        Cuboid6[][] baseBounds = new Cuboid6[16][];
        for (int i = 0; i < 16; i++)
        {
            Cuboid6[] values = new Cuboid6[1 + Integer.bitCount(i)];
            values[0] = postBounds;
            if (i > 0)
            {
                int count = 0;
                for (int j = 0; j < 4; j++)
                {
                    if ((i & (1 << j)) != 0)
                    {
                        values[1 + count] = Rotate90AboutYBlockCenterPos(connectorBounds, j);
                        count++;
                    }

                }
            }
            baseBounds[i] = values;
        }

        PRECOMPUTED_BOUNDS.put(ForgeDirection.DOWN, baseBounds);

        // COLLISION
        PRECOMPUTED_COLLISION = new HashMap<>();

        // Make our base bounds in the down direction
        Cuboid6[][] baseCollision = new Cuboid6[16][];
        for (int i = 0; i < 16; i++)
        {
            Cuboid6[] values = new Cuboid6[1 + Integer.bitCount(i)];
            values[0] = postCollisionBounds;
            if (i > 0)
            {
                int count = 0;
                for (int j = 0; j < 4; j++)
                {
                    if ((i & (1 << j)) != 0)
                    {
                        values[1 + count] = Rotate90AboutYBlockCenterPos(connectorCollisionBounds, j);
                        count++;
                    }

                }
            }
            baseCollision[i] = values;
        }

        // See comment about why we are doing this twice.
        Cuboid6[][] baseCollisionNonVert = new Cuboid6[16][];
        for (int i = 0; i < 16; i++)
        {
            Cuboid6[] values = new Cuboid6[1 + Integer.bitCount(i)];
            values[0] = getPostCollisionBoundsHorizontal;
            if (i > 0)
            {
                int count = 0;
                for (int j = 0; j < 4; j++)
                {
                    if ((i & (1 << j)) != 0)
                    {
                        values[1 + count] = Rotate90AboutYBlockCenterPos(connectorCollisionBoundsHorizontal, j);
                        count++;
                    }

                }
            }
            baseCollisionNonVert[i] = values;
        }

        PRECOMPUTED_COLLISION.put(ForgeDirection.DOWN, baseCollision);

        // The direction maps to the direction that it's place into. So if the post is hooked into the
        // ground the forge direction is actually down.
        for (int i = 0; i < ForgeDirection.values().length - 1; i++)
        {
            Pair<Integer, Cuboid6>[][] modelList = new Pair [16][];
            Cuboid6[][] boundsList = new Cuboid6[16][];
            Cuboid6[][] collisionList = new Cuboid6[16][];
            Cuboid6[] simpleModelList = new Cuboid6[2];
            ForgeDirection direction = ForgeDirection.getOrientation(i);
            if (direction == ForgeDirection.DOWN)
            {
                continue;
            }
            for (int j = 0; j < baseModels.length; j++)
            {
                modelList[j] = RotateModel(baseModels[j], direction);
                boundsList[j] = RotateModel(baseBounds[j], direction);
                collisionList[j] = RotateModel(baseCollisionNonVert[j], direction);
            }
            simpleModelList[0] = RotateCube(baseSimpleModel[0], direction);
            simpleModelList[1] = RotateCube(baseSimpleModel[1], direction);

            PRECOMPUTED_MODEL.put(direction, modelList);
            PRECOMPUTED_BOUNDS.put(direction, boundsList);
            PRECOMPUTED_COLLISION.put(direction, collisionList);
            PRECOMPUTED_SIMPLE_MODEL.put(direction, simpleModelList);
        }

    }
}
