package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.MicroblockRender;
import codechicken.multipart.MultiPartRegistry;
import net.minecraft.nbt.NBTTagCompound;

public class FencePart extends ConnectablePart
{
    public static final String name = "ue_fence";

    Cuboid6 postBounds = new Cuboid6(.375f, 0, .375f, .625f, 1, .625f);

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
        MicroblockRender.renderCuboid(position, getIMaterial(), pass, postBounds, 0);
        // Render connections?
    }

    @Override
    public void writeDesc(MCDataOutput packet)
    {
        super.writeDesc(packet);
        MultiPartRegistry.writePartID(packet, this);
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
}
