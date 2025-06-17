package com.fouristhenumber.utilitiesinexcess.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import static com.fouristhenumber.utilitiesinexcess.common.blocks.BlockRainMuffler.NBT_RAIN_MUFFLED;

public class PacketRainMuffledSync implements IMessage, IMessageHandler<PacketRainMuffledSync, IMessage> {
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

    @Override
    public IMessage onMessage(PacketRainMuffledSync message, MessageContext ctx) {
        if (ctx.side.isClient()) {
            NBTTagCompound playerNBT = Minecraft.getMinecraft().thePlayer.getEntityData();
            NBTTagCompound persistentNBT = playerNBT.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
            persistentNBT.setBoolean(NBT_RAIN_MUFFLED, message.muffled);
            playerNBT.setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentNBT);
        }
        return null;
    }
}
