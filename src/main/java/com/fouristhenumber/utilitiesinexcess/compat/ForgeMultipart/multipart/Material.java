package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import codechicken.lib.data.MCDataOutput;
import codechicken.microblock.MicroMaterialRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

public class Material
{
    protected int id;

    public Material(int material)
    {
        this.id = material;
    }

    public void save(NBTTagCompound tag) {
        tag.setString("mat", MicroMaterialRegistry.materialName(id));
    }

    public void load(NBTTagCompound tag) {
        id = MicroMaterialRegistry.materialID(tag.getString("mat"));
    }

    public void writeDesc(MCDataOutput packet) {
        MicroMaterialRegistry.writeMaterialID(packet, id);
    }

    public MicroMaterialRegistry.IMicroMaterial getIMaterial() {
        return MicroMaterialRegistry.getMaterial(id);
    }

    public IIcon getBreakingIcon(Object subPart, int side) {
        return MicroMaterialRegistry.getMaterial(id)
            .getBreakingIcon(side);
    }

    public IIcon getBrokenIcon(int side) {
        MicroMaterialRegistry.IMicroMaterial material = getIMaterial();
        if (material == null) {
            return Blocks.stone.getIcon(0, 0);
        }
        return material.getBreakingIcon(side);
    }

    public boolean canMaterialRenderInPass(int pass)
    {
        return getIMaterial().canRenderInPass(pass);
    }
}
