package com.fouristhenumber.utilitiesinexcess.common.renderers.Multipart;

import codechicken.lib.vec.Cuboid6;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fouristhenumber.utilitiesinexcess.utils.CuboidUtils.Rotate90AboutYBlockCenterPos;

public class MultiPartWallRenderingHelper
{
    // Selection bounds defaulted to North side (-Z)
    public static final Cuboid6 postBounds = new Cuboid6(.375, 0, .375, .625, 1, .625);
    public static final Cuboid6 connectorBounds = new Cuboid6(0.4375, .375, 0, 0.5625,0.9375, .375);

    // Defaulted on the North side (-Z)
    public static final Cuboid6 connector = new Cuboid6(0.4375, 0.75, 0, 0.5625, 0.9375, 0.375);
    public static final Cuboid6 connectorNS = new Cuboid6(0.4375, 0.75, 0, 0.5625, 0.9375, 1);

    // Collision bounds
    public static final Cuboid6 postCollisionBounds = new Cuboid6(.375, 0, .375, .625, 1.5, .625);
    public static final Cuboid6 connectorCollisionBounds = new Cuboid6(.375, 0, 0, .625,1.5, .375);

    // Because of some vanilla weirdness we need to make two copies one for bounds that are vertical (as seen above).
    // and one for bounds that are non-vertical. Rubberbanding happens if you don't do this.
    public static final Cuboid6 getPostCollisionBoundsHorizontal = new Cuboid6(.375, 0, .375, .625, 1.0, .625);
    public static final Cuboid6 connectorCollisionBoundsHorizontal = new Cuboid6(.375, 0, 0, .625,1.0, .375);

    // Item rendering helpers
    public static final Cuboid6 itemConnector = new Cuboid6(.4375, 0, .25, .5625, .125, .75);

    // Compute model/bounds/collision all at class initialization
    public static final Map<ForgeDirection, Cuboid6[][]> PRECOMPUTED_MODEL;
    public static final Map<ForgeDirection, Cuboid6[][]> PRECOMPUTED_BOUNDS;
    public static final Map<ForgeDirection, Cuboid6[][]> PRECOMPUTED_COLLISION;

    static
    {
        PRECOMPUTED_MODEL = new HashMap<>();

        // Make our base model in the down direction
        Cuboid6[][] baseModels = new Cuboid6[16][];
        for (int i = 0; i < 16; i++)
        {
            List<Cuboid6> values = new ArrayList<>();
            values.add(postBounds);

            boolean north = (i & 1) != 0;
            boolean east  = (i & 2) != 0;
            boolean south = (i & 4) != 0;
            boolean west  = (i & 8) != 0;

            // Render on axis so we can do less render calls
            if (north || south)
            {
                if (north && south)
                {
                    values.add(connectorNS);
                }
                else
                {
                    values.add(Rotate90AboutYBlockCenterPos(connector, north ? 0 : 2));
                }
            }

            if (east || west)
            {
                if (east && west)
                {
                    values.add(Rotate90AboutYBlockCenterPos(connectorNS, 1));
                }
                else
                {
                    values.add(Rotate90AboutYBlockCenterPos(connector, east ? 1 : 3));
                }
            }
            baseModels[i] = values.toArray(new Cuboid6[0]);
        }

        PRECOMPUTED_MODEL.put(ForgeDirection.DOWN, baseModels);

        PRECOMPUTED_BOUNDS = new HashMap<>();
        PRECOMPUTED_COLLISION = new HashMap<>();
    }
}
