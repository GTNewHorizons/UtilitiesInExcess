package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;
import com.fouristhenumber.utilitiesinexcess.network.PacketHandler;
import com.fouristhenumber.utilitiesinexcess.network.client.PacketRainMuffledSync;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;

@EventBusSubscriber(side = Side.CLIENT)
public class TileEntityRainMuffler extends TileEntitySoundMuffler {

    public static final String NBT_RAIN_MUFFLED = "RainMuffledUIX";

    @Override
    public void enableMuffler() {
        UtilitiesInExcess.proxy.soundVolumeChecks.putRainMuffler(worldObj.provider.dimensionId, xCoord, yCoord, zCoord);
    }

    @Override
    public void disableMuffler() {
        UtilitiesInExcess.proxy.soundVolumeChecks
            .removeRainMuffler(worldObj.provider.dimensionId, xCoord, yCoord, zCoord);
    }

    @EventBusSubscriber.Condition
    public static boolean shouldEventBusSubscribe() {
        return BlockConfig.rainMuffler.enableRainMuffler;
    }

    @SubscribeEvent
    public static void muffleRain(PlaySoundEvent17 event) {

        ISound sound = event.sound;
        if (!sound.getPositionedSoundLocation()
            .getResourcePath()
            .equals("ambient.weather.rain")) return;

        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        double x = Math.floor(sound.getXPosF());
        double y = Math.floor(sound.getYPosF());
        double z = Math.floor(sound.getZPosF());

        if (player.getEntityData()
            .getCompoundTag(EntityClientPlayerMP.PERSISTED_NBT_TAG)
            .getBoolean(NBT_RAIN_MUFFLED)
            || UtilitiesInExcess.proxy.soundVolumeChecks.isInRainMufflerRange(player.dimension, x, y, z)) {
            event.result = null;
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP playerMP) {
            boolean rainMuffled = event.player.getEntityData()
                .getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG)
                .getBoolean(NBT_RAIN_MUFFLED);
            PacketHandler.INSTANCE.sendTo(new PacketRainMuffledSync(rainMuffled), playerMP);
        }
    }
}
