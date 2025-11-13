package com.fouristhenumber.utilitiesinexcess.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class EntitySpecialProperty implements IExtendedEntityProperties {
    public static final String PROP_NAME = "SpecialEntityData";

    public int entranceX, entranceY, entranceZ, entranceWorld;

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        compound.setTag(PROP_NAME, tag);

        tag.setInteger("entranceX", entranceX);
        tag.setInteger("entranceY", entranceY);
        tag.setInteger("entranceZ", entranceZ);
        tag.setInteger("entranceWorld", entranceWorld);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = compound.getCompoundTag(PROP_NAME);

        if (tag != null) {
            entranceX = tag.getInteger("entranceX");
            entranceY = tag.getInteger("entranceY");
            entranceZ = tag.getInteger("entranceZ");
            entranceWorld = tag.getInteger("entranceWorld");
        }
    }

    @Override
    public void init(Entity entity, World world) {

    }
}
