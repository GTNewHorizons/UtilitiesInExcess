package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.MicroMaterialRegistry;
import codechicken.multipart.TMultiPart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collections;

public abstract class UEMultiPart extends TMultiPart
{
    protected int material;

    public abstract void render(Vector3 position, int pass);

    @Override
    public Iterable<ItemStack> getDrops()
    {
        return Collections.singletonList(MicroMaterialRegistry.getMaterial(material).getItem());
    }

    @Override
    public void save(NBTTagCompound tag)
    {
        tag.setString("mat", MicroMaterialRegistry.materialName(material));
    }

    @Override
    public void load(NBTTagCompound tag)
    {
       material = MicroMaterialRegistry.materialID(tag.getString("mat"));
    }

    @Override
    public void writeDesc(MCDataOutput packet)
    {
        MicroMaterialRegistry.writeMaterialID(packet, material);
    }

    MicroMaterialRegistry.IMicroMaterial getIMaterial()
    {
        return MicroMaterialRegistry.getMaterial(material);
    }
}
