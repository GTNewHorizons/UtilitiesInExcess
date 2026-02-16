package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.MicroblockRender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fouristhenumber.utilitiesinexcess.utils.CuboidUtils.Rotate90AboutYBlockCenterPos;
import static com.fouristhenumber.utilitiesinexcess.utils.CuboidUtils.RotateModel;

public class FencePart extends ConnectablePart
{
    public static final String name = "ue_fence";

    // Selection bounds defaulted to North side (-Z)
    public static final Cuboid6 postBounds = new Cuboid6(.375, 0, .375, .625, 1, .625);
    public static final Cuboid6 connectorBounds = new Cuboid6(0.4375, .375, 0, 0.5625,0.9375, .375);

    // Defaulted on the North side (-Z)
    public static final Cuboid6 connector = new Cuboid6(0.4375, 0.75, 0, 0.5625, 0.9375, 0.375);

    // Collision bounds
    public static final Cuboid6 postCollisionBounds = new Cuboid6(.375, 0, .375, .625, 1.5, .625);
    public static final Cuboid6 connectorCollisionBounds = new Cuboid6(.375, 0, 0, .625,1.5, .375);

    // Because of some vanilla weirdness we need to make two copies one for bounds that are vertical (as seen above).
    // and one for bounds that are non-vertical. Rubberbanding happens if you don't do this.
    public static final Cuboid6 getPostCollisionBoundsHorizontal = new Cuboid6(.375, 0, .375, .625, 1.0, .625);
    public static final Cuboid6 connectorCollisionBoundsHorizontal = new Cuboid6(.375, 0, 0, .625,1.0, .375);

    // Item rendering helpers
    public static final Cuboid6 itemConnectorMiddle = new Cuboid6(.4375, 0, .25, .5625, .125, .75);
    public static final Cuboid6 itemConnectorNotch = new Cuboid6(.4375, 0, 0, .5625, .125, .125);


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
            Cuboid6[] values = new Cuboid6[1 + 2 * Integer.bitCount(i)];
            values[0] = postBounds;
            if (i > 0)
            {
                int count = 0;
                for (int j = 0; j < 4; j++)
                {
                    if ((i & (1 << j)) != 0)
                    {
                        values[1 + 2 * count] = Rotate90AboutYBlockCenterPos(connector, j);
                        values[2 + 2 * count] = Rotate90AboutYBlockCenterPos(connector.copy().offset(new Cuboid6(0, -.375, 0, 0, -.375, 0)), j);
                        count++;
                    }

                }
            }
            baseModels[i] = values;
        }

        PRECOMPUTED_MODEL.put(ForgeDirection.DOWN, baseModels);

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
            Cuboid6[][] modelList = new Cuboid6[16][];
            Cuboid6[][] boundsList = new Cuboid6[16][];
            Cuboid6[][] collisionList = new Cuboid6[16][];
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
            PRECOMPUTED_MODEL.put(direction, modelList);
            PRECOMPUTED_BOUNDS.put(direction, boundsList);
            PRECOMPUTED_COLLISION.put(direction, collisionList);
        }
    }

    // Forge direction isn't that well suited to rotations so we need to remap south to west and west to south
    // I.E. swap bits 2 and 3
//    public static int MapForgeDirection(int dir)
//    {
//        int mask = ((dir >> 1) ^ (dir >> 2)) & 1; // Mask bits 2 and 3
//        return dir ^ ((mask << 1) | (mask << 2)); // Swap them with XOR
//    }

    public FencePart(int material, int side)
    {
        super(side);
        this.material = material;
    }


    @Override
    public Cuboid6 getConnectionInDirection(ForgeDirection side)
    {
        return PRECOMPUTED_BOUNDS.get(downDirection)[15][indexInFrame(side) + 1];
    }

    @Override
    public String getType() {
        return name;
    }

    @Override
    public boolean renderStatic(Vector3 position, int pass)
    {
        if (getIMaterial().canRenderInPass(pass))
        {
            render(position, pass);
            return true;
        }
        return false;
    }

    @Override
    public void render(Vector3 position, int pass)
    {
        // Render post
        if (pass == -1)
        {
            // Posts
            MicroblockRender.renderCuboid(position.add(0, 0, -.375), getIMaterial(), pass, postBounds, 0);
            MicroblockRender.renderCuboid(position.add(0, 0, .750), getIMaterial(), pass, postBounds, 0);

            // Connectors & Notches TODO notches would be nice to remove if I could figure out how to wrap the texture with MicroblockRender
            // Bottom Connector
            MicroblockRender.renderCuboid(position.add(0, .3125, -.375), getIMaterial(), pass, itemConnectorMiddle, 0);
            MicroblockRender.renderCuboid(position.add(0, 0, -.125), getIMaterial(), pass, itemConnectorNotch, 0);
            MicroblockRender.renderCuboid(position.add(0, 0, 1.125), getIMaterial(), pass, itemConnectorNotch, 0);

            // Top Connector
            MicroblockRender.renderCuboid(position.add(0, .5, -1), getIMaterial(), pass, itemConnectorMiddle, 0);
            MicroblockRender.renderCuboid(position.add(0, 0, -.125), getIMaterial(), pass, itemConnectorNotch, 0);
            MicroblockRender.renderCuboid(position.add(0, 0, 1.125), getIMaterial(), pass, itemConnectorNotch, 0);
        }
        else
        {
            Cuboid6[][] models = PRECOMPUTED_MODEL.get(this.downDirection);
            for (Cuboid6 cuboid : models[getConnectionMask()])
            {
                MicroblockRender.renderCuboid(position, getIMaterial(), pass, cuboid, 0); // TODO fix faces to not render useless faces
            }
        }
    }

    @Override
    public Iterable<IndexedCuboid6> getSubParts()
    {
        return Arrays.stream(PRECOMPUTED_BOUNDS.get(downDirection)[getConnectionMask()])
            .map(t -> new IndexedCuboid6(0, t))
            .collect(Collectors.toList());
    }

    @Override
    public Iterable<Cuboid6> getCollisionBoxes()
    {
        return Arrays.asList(PRECOMPUTED_COLLISION.get(downDirection)[getConnectionMask()]);
    }

    @Nonnull
    @Override
    public Cuboid6 getBounds() {
        return postBounds;
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return Collections.singleton(PRECOMPUTED_BOUNDS.get(downDirection)[0][0]);
    }

    @Override
    public boolean drawHighlight(MovingObjectPosition hit, EntityPlayer player, float frame)
    {
        return false;
    }
}
