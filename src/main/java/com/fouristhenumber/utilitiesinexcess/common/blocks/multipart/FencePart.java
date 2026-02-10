package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.MicroblockRender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;

import javax.annotation.Nonnull;
import java.util.Collections;

public class FencePart extends ConnectablePart
{
    public static final String name = "ue_fence";

    // Selection bounds
    public static final Cuboid6 postBounds = new Cuboid6(.375, 0, .375, .625, 1, .625);
    public static final Cuboid6 connectorBoundsEW = new Cuboid6(0, 0, .375, .375,1, .625);
    public static final Cuboid6 connectorBoundsNS = new Cuboid6(.375, 0, 0, .625,1, .375);

    // Collision bounds
    public static final Cuboid6 postCollisionBounds = new Cuboid6(.375, 0, .375, .625, 1.5, .625);
    public static final Cuboid6 connectorCollisionBoundsEW = new Cuboid6(0, 0, .375, .375,1.5, .625);
    public static final Cuboid6 connectorCollisionBoundsNS = new Cuboid6(.375, 0, 0, .625,1.5, .375);

    // Item rendering helpers
    public static final Cuboid6 itemConnectorMiddle = new Cuboid6(.4375, 0, .25, .5625, .125, .75);
    public static final Cuboid6 itemConnectorNotch = new Cuboid6(.4375, 0, 0, .5625, .125, .125);

    public FencePart(int material)
    {
        this.material = material;
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

            // Connectors
            MicroblockRender.renderCuboid(position.add(0, .3125, -.375), getIMaterial(), pass, itemConnectorMiddle, 0);
            MicroblockRender.renderCuboid(position.add(0, 0, -.125), getIMaterial(), pass, itemConnectorNotch, 0);
            MicroblockRender.renderCuboid(position.add(0, 0, 1.125), getIMaterial(), pass, itemConnectorNotch, 0);

            // Notches
            MicroblockRender.renderCuboid(position.add(0, .5, -1), getIMaterial(), pass, itemConnectorMiddle, 0);
            MicroblockRender.renderCuboid(position.add(0, 0, -.125), getIMaterial(), pass, itemConnectorNotch, 0);
            MicroblockRender.renderCuboid(position.add(0, 0, 1.125), getIMaterial(), pass, itemConnectorNotch, 0);
        }
        else
        {
            MicroblockRender.renderCuboid(position, getIMaterial(), pass, postBounds, 0);
        }
    }


    @Override
    public void save(NBTTagCompound tag)
    {
        super.save(tag);
        tag.setString("t", getType());
    }

    @Override
    public void load(NBTTagCompound tag)
    {
        super.load(tag);
        tag.setString("t", getType());
    }

    @Override
    public Cuboid6 getBaseBounds() {
        return postBounds;
    }

    @Override
    public Cuboid6 getConnectionBounds(ForgeDirection side) {
        return switch (side) {
            case NORTH -> connectorBoundsNS;
            case SOUTH -> connectorBoundsNS.copy().offset(new Cuboid6(0, 0, 0.625, 0, 0, 0.625));
            case EAST -> connectorBoundsEW.copy().offset(new Cuboid6(0.625, 0, 0, 0.625, 0, 0));
            case WEST -> connectorBoundsEW;
            default -> null;
        };
    }

    @Override
    public Cuboid6 getBaseCollisionBounds()
    {
        return postCollisionBounds;
    }

    @Override
    public Cuboid6 getCollisionConnectionBounds(ForgeDirection side) {
        return switch (side) {
            case NORTH -> connectorCollisionBoundsNS;
            case SOUTH -> connectorCollisionBoundsNS.copy().offset(new Cuboid6(0, 0, 0.625, 0, 0, 0.625));
            case EAST -> connectorCollisionBoundsEW.copy().offset(new Cuboid6(0.625, 0, 0, 0.625, 0, 0));
            case WEST -> connectorCollisionBoundsEW;
            default -> null;
        };
    }

    @Nonnull
    @Override
    public Cuboid6 getBounds() {
        return postBounds;
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return Collections.singleton(postBounds);
    }

    @Override
    public boolean drawHighlight(MovingObjectPosition hit, EntityPlayer player, float frame)
    {
        return true;
    }
}
