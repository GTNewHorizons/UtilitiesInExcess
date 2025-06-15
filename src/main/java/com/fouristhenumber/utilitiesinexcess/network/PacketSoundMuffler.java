package com.fouristhenumber.utilitiesinexcess.network;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySoundMuffler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class PacketSoundMuffler implements IMessage, IMessageHandler<PacketSoundMuffler, IMessage> {
    int dim;
    int x;
    int y;
    int z;
    boolean activate;

    public PacketSoundMuffler() {}

    public PacketSoundMuffler(TileEntitySoundMuffler muffler, boolean activate) {
        this.dim = muffler.getWorldObj().provider.dimensionId;
        this.x = muffler.xCoord;
        this.y = muffler.yCoord;
        this.z = muffler.zCoord;
        this.activate = activate;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dim);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(activate);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dim = buf.readInt();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        activate = buf.readBoolean();
    }

    @Override
    public IMessage onMessage(PacketSoundMuffler message, MessageContext ctx) {
        if (ctx.side == Side.SERVER) return null;
        if (message.activate) {
            UtilitiesInExcess.proxy.soundEventHandler.putMuffler(message.dim, message.x, message.y, message.z);
        }
        else {
            UtilitiesInExcess.proxy.soundEventHandler.removeMuffler(message.dim, message.x, message.y, message.z);
        }
        return null;
    }
}
