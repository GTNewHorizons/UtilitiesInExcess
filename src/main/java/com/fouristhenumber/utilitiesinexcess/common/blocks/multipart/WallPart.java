package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.MicroblockRender;
import com.fouristhenumber.utilitiesinexcess.common.renderers.Multipart.MultiPartFenceRenderingHelper;
import com.fouristhenumber.utilitiesinexcess.common.renderers.Multipart.MultiPartWallRenderingHelper;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.fouristhenumber.utilitiesinexcess.common.renderers.Multipart.MultiPartWallRenderingHelper.PRECOMPUTED_BOUNDS;
import static com.fouristhenumber.utilitiesinexcess.common.renderers.Multipart.MultiPartWallRenderingHelper.PRECOMPUTED_COLLISION;
import static com.fouristhenumber.utilitiesinexcess.common.renderers.Multipart.MultiPartWallRenderingHelper.PRECOMPUTED_SIMPLE_MODEL;
import static com.fouristhenumber.utilitiesinexcess.common.renderers.Multipart.MultiPartWallRenderingHelper.connectorNS;
import static com.fouristhenumber.utilitiesinexcess.common.renderers.Multipart.MultiPartWallRenderingHelper.postBounds;
import static com.fouristhenumber.utilitiesinexcess.common.renderers.Multipart.MultiPartWallRenderingHelper.PRECOMPUTED_MODEL;
import static com.fouristhenumber.utilitiesinexcess.utils.CuboidUtils.Rotate90AboutYBlockCenterPos;

public class WallPart extends ConnectablePart
{

    public static final String name = "ue_wall";

    public WallPart(int material, int side) {
        super(side);
        this.material = material;
    }

    @Override
    public String getType() {
        return name;
    }

    @Override
    public void render(Vector3 position, int pass)
    {
        // Render post
        if (pass == -1)
        {
            // Post
            MicroblockRender.renderCuboid(position, getIMaterial(), pass, postBounds, 0);
            // Connector
            MicroblockRender.renderCuboid(position, getIMaterial(), pass, Rotate90AboutYBlockCenterPos(connectorNS, 1), 0);
        }
        else
        {
            int mask = getConnectionMask();
            if (mask == 0b1010)
            {
                Cuboid6 model = PRECOMPUTED_SIMPLE_MODEL.get(this.downDirection)[1];
                MicroblockRender.renderCuboid(position, getIMaterial(), pass, model, getCullMask(1));
            }
            else if (mask == 0b0101)
            {
                Cuboid6 model = PRECOMPUTED_SIMPLE_MODEL.get(this.downDirection)[0];
                MicroblockRender.renderCuboid(position, getIMaterial(), pass, model, getCullMask(0));
            }
            else
            {
                Pair<Integer, Cuboid6>[] models = PRECOMPUTED_MODEL.get(this.downDirection)[mask];
                for (int i = 0; i < models.length; i++)
                {
                    if (i == 0)
                    {
                        MicroblockRender.renderCuboid(position, getIMaterial(), pass, models[i].second(), 0);
                    }
                    else
                    {
                        MicroblockRender.renderCuboid(position, getIMaterial(), pass, models[i].second(), getCullMask(models[i].first()));
                    }
                }
            }
        }
    }

    @Override
    public Cuboid6 getConnectionInDirection(ForgeDirection side)
    {
        return MultiPartFenceRenderingHelper.PRECOMPUTED_BOUNDS.get(downDirection)[15][indexInFrame(side) + 1];
    }

    @Override
    public Iterable<IndexedCuboid6> getSubParts()
    {
        int mask = getConnectionMask();
        if (mask == 0b1010)
        {
            return Collections.singleton(new IndexedCuboid6(0, PRECOMPUTED_SIMPLE_MODEL.get(this.downDirection)[1]));

        }
        else if (mask == 0b0101)
        {
            return Collections.singleton(new IndexedCuboid6(0, PRECOMPUTED_SIMPLE_MODEL.get(this.downDirection)[0]));
        }
        return Arrays.stream(PRECOMPUTED_BOUNDS.get(downDirection)[mask])
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
        return MultiPartWallRenderingHelper.postBounds;
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return Collections.singleton(MultiPartFenceRenderingHelper.PRECOMPUTED_BOUNDS.get(downDirection)[0][0]);
    }

    @Override
    public boolean drawHighlight(MovingObjectPosition hit, EntityPlayer player, float frame)
    {
        return false;
    }
}
