package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartWallRenderingHelper.PRECOMPUTED_BOUNDS;
import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartWallRenderingHelper.PRECOMPUTED_COLLISION;
import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartWallRenderingHelper.PRECOMPUTED_MODEL;
import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartWallRenderingHelper.PRECOMPUTED_SIMPLE_MODEL;
import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartWallRenderingHelper.connectorNS;
import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartWallRenderingHelper.postBounds;
import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.util.CuboidUtils.Rotate90AboutYBlockCenterPos;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartWallRenderingHelper;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.MicroblockRender;
import it.unimi.dsi.fastutil.Pair;

public class WallPart extends ConnectablePart {

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
    public void render(Vector3 position, int pass) {
        // Render post
        if (pass == -1) {
            // Post
            MicroblockRender.renderCuboid(position, getIMaterial(), pass, postBounds, 0);
            // Connector
            MicroblockRender
                .renderCuboid(position, getIMaterial(), pass, Rotate90AboutYBlockCenterPos(connectorNS, 1), 0);
        } else {
            int mask = getConnectionMask();
            if (mask == 0b1010) {
                Cuboid6 model = PRECOMPUTED_SIMPLE_MODEL.get(this.downDirection)[1];
                MicroblockRender.renderCuboid(position, getIMaterial(), pass, model, getCullMask(1));
            } else if (mask == 0b0101) {
                Cuboid6 model = PRECOMPUTED_SIMPLE_MODEL.get(this.downDirection)[0];
                MicroblockRender.renderCuboid(position, getIMaterial(), pass, model, getCullMask(0));
            } else {
                Pair<Integer, Cuboid6>[] models = PRECOMPUTED_MODEL.get(this.downDirection)[mask];
                for (int i = 0; i < models.length; i++) {
                    if (i == 0) {
                        MicroblockRender.renderCuboid(position, getIMaterial(), pass, models[i].second(), 0);
                    } else {
                        MicroblockRender.renderCuboid(
                            position,
                            getIMaterial(),
                            pass,
                            models[i].second(),
                            getCullMask(models[i].first()));
                    }
                }
            }
        }
    }

    @Override
    public Cuboid6 getConnectionInDirection(ForgeDirection side) {
        return PRECOMPUTED_BOUNDS.get(downDirection)[15][indexInFrame(side) + 1].second();
    }

    @Override
    public Iterable<IndexedCuboid6> getSubParts() {
        int mask = getConnectionMask();
        if (mask == 0b1010) {
            return Collections.singleton(new IndexedCuboid6(0, PRECOMPUTED_SIMPLE_MODEL.get(this.downDirection)[1]));

        } else if (mask == 0b0101) {
            return Collections.singleton(new IndexedCuboid6(0, PRECOMPUTED_SIMPLE_MODEL.get(this.downDirection)[0]));
        }
        return Arrays.stream(PRECOMPUTED_BOUNDS.get(downDirection)[mask])
            .map(t -> new IndexedCuboid6(0, t.second()))
            .collect(Collectors.toList());
    }

    @Override
    public Iterable<Cuboid6> getCollisionBoxes() {
        return Arrays.asList(PRECOMPUTED_COLLISION.get(downDirection)[getConnectionMask()]);
    }

    @Nonnull
    @Override
    public Cuboid6 getBounds() {
        return MultipartWallRenderingHelper.postBounds;
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return Collections.singleton(MultipartWallRenderingHelper.PRECOMPUTED_BOUNDS.get(downDirection)[0][0].second());
    }

    @Override
    public boolean drawHighlight(MovingObjectPosition hit, EntityPlayer player, float frame) {
        int mask = getConnectionMask();
        Iterable<Pair<Integer, Cuboid6>> highlightCuboidList;
        if (mask == 0b1010) {
            highlightCuboidList = Collections.singleton(Pair.of(-1, PRECOMPUTED_SIMPLE_MODEL.get(downDirection)[1]));
        } else if (mask == 0b0101) {
            highlightCuboidList = Collections.singleton(Pair.of(-1, PRECOMPUTED_SIMPLE_MODEL.get(downDirection)[0]));
        } else {
            highlightCuboidList = Arrays.asList(PRECOMPUTED_BOUNDS.get(downDirection)[mask]);
        }
        return drawConnecableHighLight(hit, player, frame, highlightCuboidList);
    }

}
