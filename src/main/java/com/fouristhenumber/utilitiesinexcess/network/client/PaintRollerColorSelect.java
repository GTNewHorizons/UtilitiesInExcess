package com.fouristhenumber.utilitiesinexcess.network.client;

import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.common.items.ItemPaintRoller;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class PaintRollerColorSelect implements IMessage {

    private int color;

    public PaintRollerColorSelect() {}

    public PaintRollerColorSelect(int color) {
        this.color = color;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        color = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(color);
    }

    public int getColor() {
        return color;
    }

    public static class Handler implements IMessageHandler<PaintRollerColorSelect, IMessage> {

        @Override
        public IMessage onMessage(PaintRollerColorSelect message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) return null;

            ItemStack stack = ctx.getServerHandler().playerEntity.getHeldItem();
            if (!(stack.getItem() instanceof ItemPaintRoller)) return null;

            ItemPaintRoller.setStackColor(stack, message.getColor());

            return null;
        }
    }
}
