package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

@EventBusSubscriber(side = Side.CLIENT)
public class TileEntitySoundMuffler extends TileEntity {

    boolean active;

    public void enableMuffler() {
        UtilitiesInExcess.proxy.soundVolumeChecks
            .putSoundMuffler(worldObj.provider.dimensionId, xCoord, yCoord, zCoord);
    }

    public void disableMuffler() {
        UtilitiesInExcess.proxy.soundVolumeChecks
            .removeSoundMuffler(worldObj.provider.dimensionId, xCoord, yCoord, zCoord);
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    public void onInputChanged() {
        if (worldObj.isRemote) return;

        boolean redstonePowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
        // Check our redstone state has changed
        if (redstonePowered == active) {
            active = !redstonePowered;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        active = nbt.getBoolean("active");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("active", active);
    }

    @Override
    public void invalidate() {
        if (worldObj.isRemote) {
            disableMuffler();
        }
        super.invalidate();
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
        if (worldObj.isRemote) {
            if (active) enableMuffler();
            else disableMuffler();
        }
    }

    @EventBusSubscriber.Condition
    public static boolean shouldEventBusSubscribe() {
        return BlockConfig.soundMuffler.enableSoundMuffler;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void muffleSound(PlaySoundEvent17 event) {

        ISound sound = event.sound;

        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        double x = Math.floor(sound.getXPosF());
        double y = Math.floor(sound.getYPosF());
        double z = Math.floor(sound.getZPosF());

        if (UtilitiesInExcess.proxy.soundVolumeChecks.isInSoundMufflerRange(player.dimension, x, y, z)) {
            float reduction = BlockConfig.soundMuffler.soundMufflerReduction / 100f;
            event.result = new MuffledSound(event.sound, reduction);
            player.worldObj.spawnParticle("smoke", sound.getXPosF(), sound.getYPosF(), sound.getZPosF(), 0, 0.03, 0);
        }
    }

    private static class MuffledSound implements ISound {

        ISound base;
        float reduction;

        MuffledSound(ISound base, float reduction) {
            this.base = base;
            this.reduction = reduction;
        }

        @Override
        public float getVolume() {
            return base.getVolume() * reduction;
        }

        @Override
        public ResourceLocation getPositionedSoundLocation() {
            return base.getPositionedSoundLocation();
        }

        @Override
        public boolean canRepeat() {
            return base.canRepeat();
        }

        @Override
        public int getRepeatDelay() {
            return base.getRepeatDelay();
        }

        @Override
        public float getPitch() {
            return base.getPitch();
        }

        @Override
        public float getXPosF() {
            return base.getXPosF();
        }

        @Override
        public float getYPosF() {
            return base.getYPosF();
        }

        @Override
        public float getZPosF() {
            return base.getZPosF();
        }

        @Override
        public AttenuationType getAttenuationType() {
            return base.getAttenuationType();
        }
    }
}
