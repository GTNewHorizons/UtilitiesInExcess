package com.fouristhenumber.utilitiesinexcess.utils;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.config.BlockConfig;
import com.gtnewhorizon.gtnhlib.datastructs.space.VolumeMembershipCheck;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;



public class SoundEventHandler {
    VolumeMembershipCheck volumeCheck = new VolumeMembershipCheck(VolumeMembershipCheck.VolumeShape.CUBE);

    public void putMuffler(int dim, int x, int y, int z) {
        volumeCheck.putVolume(dim, x, y, z, BlockConfig.soundMuffler.soundMufflerRange);
    }
    public void removeMuffler(int dim, int x, int y, int z) {
        volumeCheck.removeVolume(dim, x, y, z);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void muffleSound(PlaySoundEvent17 event) {

        ISound sound = event.sound;
        double x = Math.floor(sound.getXPosF());
        double y = Math.floor(sound.getYPosF());
        double z = Math.floor(sound.getZPosF());

        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        if (volumeCheck.isInVolume(player.dimension, x, y, z)) {
            UtilitiesInExcess.LOG.warn("Muffling Sound");
            float reduction = BlockConfig.soundMuffler.soundMufflerReduction / 100f;
            event.result = new MuffledSound(event.sound, reduction);
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
            return base.getXPosF();
        }

        @Override
        public AttenuationType getAttenuationType() {
            return base.getAttenuationType();
        }
    }
}
