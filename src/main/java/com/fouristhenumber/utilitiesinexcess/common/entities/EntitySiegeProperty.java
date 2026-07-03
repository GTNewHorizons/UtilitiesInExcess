package com.fouristhenumber.utilitiesinexcess.common.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.EntityEvent;

import com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EntitySiegeProperty implements IExtendedEntityProperties {

    public static final String PROP_KEY = "entity-siege";

    public boolean siege;
    public int siegeMobsKilled, beaconSpawnX, beaconSpawnY, beaconSpawnZ, siegeTimer;

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        compound.setTag(PROP_KEY, tag);

        tag.setBoolean("siege", siege);
        tag.setInteger("siegeMobsKilled", siegeMobsKilled);
        tag.setInteger("beaconSpawnX", beaconSpawnX);
        tag.setInteger("beaconSpawnY", beaconSpawnY);
        tag.setInteger("beaconSpawnZ", beaconSpawnZ);
        tag.setInteger("siegeTimer", siegeTimer);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = compound.getCompoundTag(PROP_KEY);

        if (tag != null) {
            siege = tag.getBoolean("siege");
            siegeMobsKilled = tag.getInteger("siegeMobsKilled");
            beaconSpawnX = tag.getInteger("beaconSpawnX");
            beaconSpawnY = tag.getInteger("beaconSpawnY");
            beaconSpawnZ = tag.getInteger("beaconSpawnZ");
            siegeTimer = tag.getInteger("siegeTimer");
        }
    }

    @Override
    public void init(Entity entity, World world) {

    }

    @SuppressWarnings("unused")
    @EventBusSubscriber
    public static class Events {

        @EventBusSubscriber.Condition
        public static boolean shouldSubscribe() {
            return InversionConfig.INSTANCE.enableInversionSigil;
        }

        @SubscribeEvent
        public static void onEntityConstructing(EntityEvent.EntityConstructing event) {
            if (event.entity instanceof EntityPlayer player) {
                player.registerExtendedProperties(PROP_KEY, new EntitySiegeProperty());
            }
        }
    }
}
