package com.fouristhenumber.utilitiesinexcess.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.client.particle.GammaRayEmitter;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

/**
 * Spawns a single gamma ray converging onto the given point (a block being mined by the quarry).
 */
public class GammaRayParticlePacket implements IMessage {

    private double x, y, z;

    public GammaRayParticlePacket() {} // Required

    public GammaRayParticlePacket(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    public static class Handler implements IMessageHandler<GammaRayParticlePacket, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(GammaRayParticlePacket message, MessageContext ctx) {
            World world = Minecraft.getMinecraft().theWorld;
            GammaRayEmitter.emitConverging(world, message.x, message.y, message.z, world.rand);
            return null;
        }
    }
}
