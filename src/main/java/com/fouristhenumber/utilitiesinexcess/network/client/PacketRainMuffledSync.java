package com.fouristhenumber.utilitiesinexcess.network.client;

import static com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRainMuffler.NBT_RAIN_MUFFLED;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class PacketRainMuffledSync implements IMessage {

    boolean muffled;

    public PacketRainMuffledSync() {}

    public PacketRainMuffledSync(boolean muffled) {
        this.muffled = muffled;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        muffled = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(muffled);
    }

    public static class Handler implements IMessageHandler<PacketRainMuffledSync, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketRainMuffledSync message, MessageContext ctx) {

            NBTTagCompound playerNBT = Minecraft.getMinecraft().thePlayer.getEntityData();
            NBTTagCompound persistentNBT = playerNBT.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
            persistentNBT.setBoolean(NBT_RAIN_MUFFLED, message.muffled);
            playerNBT.setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentNBT);

            return null;
        }
    }
}
