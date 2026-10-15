package com.fouristhenumber.utilitiesinexcess.network.client;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static com.fouristhenumber.utilitiesinexcess.transfer.upgrade.TransferUpgrade.isWirelessUpgrade;

public class PacketSetFrequency implements IMessage
{
    String frequency;

    public PacketSetFrequency(){}

    public PacketSetFrequency(String frequency)
    {
        this.frequency = frequency;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        frequency = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, frequency);
    }

    public static class Handler implements IMessageHandler<PacketSetFrequency, IMessage>
    {
        @Override
        public IMessage onMessage(PacketSetFrequency message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            ItemStack held = player.getHeldItem();

            if (held != null && isWirelessUpgrade(held))
            {
                NBTTagCompound tag = held.getTagCompound();

                if (tag == null)
                {
                    tag = new NBTTagCompound();
                    held.setTagCompound(tag);
                }

                tag.setString("Frequency", message.frequency);
                player.inventory.markDirty();
            }

            return null;
        }
    }
}
