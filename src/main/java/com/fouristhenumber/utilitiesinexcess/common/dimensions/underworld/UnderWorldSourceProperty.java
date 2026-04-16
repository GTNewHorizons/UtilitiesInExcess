package com.fouristhenumber.utilitiesinexcess.common.dimensions.underworld;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/// Stores the player's source portal location
@EventBusSubscriber
public class UnderWorldSourceProperty implements IExtendedEntityProperties {

    public static final String PROP_KEY = "underworld-source";

    public int entranceX, entranceY, entranceZ, entranceWorld;

    @SubscribeEvent
    public static void onEntityConstructing(EntityConstructing event) {
        if (event.entity instanceof EntityPlayer player) {
            player.registerExtendedProperties(PROP_KEY, new UnderWorldSourceProperty());
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        compound.setTag(PROP_KEY, tag);

        tag.setInteger("entranceX", entranceX);
        tag.setInteger("entranceY", entranceY);
        tag.setInteger("entranceZ", entranceZ);
        tag.setInteger("entranceWorld", entranceWorld);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = compound.getCompoundTag(PROP_KEY);

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
