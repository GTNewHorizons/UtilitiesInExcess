package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.MicroblockRender;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartFenceRenderingHelper.PRECOMPUTED_BOUNDS;
import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartFenceRenderingHelper.PRECOMPUTED_COLLISION;
import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartFenceRenderingHelper.PRECOMPUTED_MODEL;
import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartFenceRenderingHelper.itemConnectorMiddle;
import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartFenceRenderingHelper.itemConnectorNotch;
import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartFenceRenderingHelper.postBounds;

public class FencePart extends ConnectablePart
{
    public static final String name = "ue_fence";

    public FencePart(int material, int side)
    {
        super(side);
        this.material = material;
    }


    @Override
    public Cuboid6 getConnectionInDirection(ForgeDirection side)
    {
        return PRECOMPUTED_BOUNDS.get(downDirection)[15][indexInFrame(side) + 1].second();
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
            Pair<Integer, Cuboid6>[] models = PRECOMPUTED_MODEL.get(this.downDirection)[getConnectionMask()];
            for (int i = 0; i < models.length; i++)
            {
                if (i == 0)
                {
                    MicroblockRender.renderCuboid(position, getIMaterial(), pass, models[i].second(), 0);
                }
                MicroblockRender.renderCuboid(position, getIMaterial(), pass, models[i].second(), getCullMask(models[i].first()));
            }
        }
    }

    @Override
    public Iterable<IndexedCuboid6> getSubParts()
    {
        return Arrays.stream(PRECOMPUTED_BOUNDS.get(downDirection)[getConnectionMask()])
            .map(t -> new IndexedCuboid6(0, t.second()))
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
        return Collections.singleton(PRECOMPUTED_BOUNDS.get(downDirection)[0][0].second());
    }

    @Override
    public boolean drawHighlight(MovingObjectPosition hit, EntityPlayer player, float frame)
    {
        return drawConnecableHighLight(hit, player, frame, Arrays.asList(PRECOMPUTED_BOUNDS.get(downDirection)[getConnectionMask()]));
    }
}
