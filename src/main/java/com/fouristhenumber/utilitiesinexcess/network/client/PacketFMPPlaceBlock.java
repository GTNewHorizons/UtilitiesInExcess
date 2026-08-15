package com.fouristhenumber.utilitiesinexcess.network.client;

import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.UiEPartFactory;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketFMPPlaceBlock implements IMessage
{
    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<PacketFMPPlaceBlock, IMessage> {
        @Override
        public IMessage onMessage(PacketFMPPlaceBlock message, MessageContext ctx)
        {
            UiEPartFactory.EventHandler.place(ctx.getServerHandler().playerEntity, ctx.getServerHandler().playerEntity.getEntityWorld());
            return null;
        }
    }
}
