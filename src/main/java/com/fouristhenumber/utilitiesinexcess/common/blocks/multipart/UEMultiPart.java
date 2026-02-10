package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.MicroMaterialRegistry;
import codechicken.multipart.IconHitEffects;
import codechicken.multipart.JIconHitEffects;
import codechicken.multipart.JNormalOcclusion;
import codechicken.multipart.NormalOcclusionTest;
import codechicken.multipart.TMultiPart;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;

import java.util.Collections;

public abstract class UEMultiPart extends TMultiPart implements JIconHitEffects, JNormalOcclusion
{
    protected int material;

    public abstract void render(Vector3 position, int pass);

    @Override
    public Iterable<ItemStack> getDrops()
    {
        return Collections.singletonList(UEMultiPartItem.createStack(this));
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

    @Override
    public void addDestroyEffects(EffectRenderer renderer)
    {
        IconHitEffects.addDestroyEffects(this, renderer);
    }

    @Override
    public void addHitEffects(MovingObjectPosition movingObjectPosition, EffectRenderer renderer)
    {
        IconHitEffects.addHitEffects(this, movingObjectPosition, renderer);
    }

    @Override
    public IIcon getBreakingIcon(Object subPart, int side) {
        return MicroMaterialRegistry.getMaterial(material).getBreakingIcon(side);
    }

    @Override
    public IIcon getBrokenIcon(int side) {
        MicroMaterialRegistry.IMicroMaterial material = getIMaterial();
        if (material == null)
        {
            return Blocks.stone.getIcon(0, 0);
        }
        return material.getBreakingIcon(side);
    }

    @Override
    public boolean occlusionTest(TMultiPart part)
    {
        return NormalOcclusionTest.apply(this, part) && super.occlusionTest(part);
    }
}
