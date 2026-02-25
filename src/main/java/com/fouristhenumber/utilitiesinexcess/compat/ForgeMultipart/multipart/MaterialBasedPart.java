package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.MicroMaterialRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;

public abstract class MaterialBasedPart extends UEMultipart
{
    protected int material;

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
    public ItemStack pickItem(MovingObjectPosition hit)
    {
        return UEMultipartItem.createStack(material, Content.partMap.get(this.getType()));
    }
}
