package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartFenceRenderingHelper.PRECOMPUTED_BOUNDS;
import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartFenceRenderingHelper.PRECOMPUTED_COLLISION;
import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartFenceRenderingHelper.PRECOMPUTED_MODEL;
import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartFenceRenderingHelper.itemConnectorMiddle;
import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartFenceRenderingHelper.itemConnectorNotch;
import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.MultipartFenceRenderingHelper.postBounds;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.microblock.MicroMaterialRegistry;
import codechicken.multipart.JNormalOcclusion;
import codechicken.multipart.NormalOcclusionTest;
import codechicken.multipart.TMultiPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.MicroblockRender;
import it.unimi.dsi.fastutil.Pair;

public class FencePart extends ConnectablePart implements IMaterialPart, JNormalOcclusion
{
    public static final String name = "ue_fence";
    public Material material;

    public FencePart(int materialId, int side)
    {
        super(side);
        this.material = new Material(materialId);
    }

    public FencePart(MCDataInput packet)
    {
        super(packet.readInt());
        this.material = new Material(MicroMaterialRegistry.readMaterialID(packet));
    }

    @Override
    public Cuboid6 getConnectionInDirection(ForgeDirection side) {
        return PRECOMPUTED_BOUNDS.get(downDirection)[15][indexInFrame(side) + 1].second();
    }

    @Override
    public String getType() {
        return name;
    }

    @Override
    public void render(Vector3 position, int pass) {
        // Render post
        if (pass == -1) {
            // Posts
            MicroblockRender.renderCuboid(position.add(0, 0, -.375), this.material.getIMaterial(), pass, postBounds, 0);
            MicroblockRender.renderCuboid(position.add(0, 0, .750), this.material.getIMaterial(), pass, postBounds, 0);

            // Connectors & Notches TODO notches would be nice to remove if I could figure out how to wrap the texture
            // with MicroblockRender
            // Bottom Connector
            MicroblockRender.renderCuboid(position.add(0, .3125, -.375), this.material.getIMaterial(), pass, itemConnectorMiddle, 0);
            MicroblockRender.renderCuboid(position.add(0, 0, -.125), this.material.getIMaterial(), pass, itemConnectorNotch, 0);
            MicroblockRender.renderCuboid(position.add(0, 0, 1.125), this.material.getIMaterial(), pass, itemConnectorNotch, 0);

            // Top Connector
            MicroblockRender.renderCuboid(position.add(0, .5, -1), this.material.getIMaterial(), pass, itemConnectorMiddle, 0);
            MicroblockRender.renderCuboid(position.add(0, 0, -.125), this.material.getIMaterial(), pass, itemConnectorNotch, 0);
            MicroblockRender.renderCuboid(position.add(0, 0, 1.125), this.material.getIMaterial(), pass, itemConnectorNotch, 0);
        } else {
            Pair<Integer, Cuboid6>[] models = PRECOMPUTED_MODEL.get(this.downDirection)[getConnectionMask()];
            for (int i = 0; i < models.length; i++) {
                if (i == 0) {
                    MicroblockRender.renderCuboid(position, this.material.getIMaterial(), pass, models[i].second(), 0);
                }
                MicroblockRender
                    .renderCuboid(position, this.material.getIMaterial(), pass, models[i].second(), getCullMask(models[i].first()));
            }
        }
    }

    @Override
    public Iterable<IndexedCuboid6> getSubParts() {
        return Arrays.stream(PRECOMPUTED_BOUNDS.get(downDirection)[getConnectionMask()])
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
        return postBounds;
    }

    @Override
    public IIcon getBreakingIcon(Object subPart, int side) {
        return this.material.getBreakingIcon(subPart, side);
    }

    @Override
    public IIcon getBrokenIcon(int side) {
        return this.material.getBrokenIcon(side);
    }

    @Override
    public boolean renderStatic(Vector3 position, int pass) {
        if (this.material.canMaterialRenderInPass(pass)) {
            render(position, pass);
            return true;
        }
        return false;
    }

    @Override
    public void save(NBTTagCompound tag) {
        super.save(tag);
        material.save(tag);
    }

    @Override
    public void load(NBTTagCompound tag) {
        super.load(tag);
        material.load(tag);
    }

    @Override
    public void writeDesc(MCDataOutput packet) {
        super.writeDesc(packet);
        material.writeDesc(packet);
    }

    @Override
    public ItemStack pickItem(MovingObjectPosition hit) {
        return UiEMultipartMaterialItem.createStack(this);
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return Collections.singleton(PRECOMPUTED_BOUNDS.get(downDirection)[0][0].second());
    }

    @Override
    public boolean drawHighlight(MovingObjectPosition hit, EntityPlayer player, float frame) {
        return drawConnectableHighLight(
            hit,
            player,
            frame,
            Arrays.asList(PRECOMPUTED_BOUNDS.get(downDirection)[getConnectionMask()]));
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public boolean occlusionTest(TMultiPart part) {
        return NormalOcclusionTest.apply(this, part) && super.occlusionTest(part);
    }
}
