package com.fouristhenumber.utilitiesinexcess.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class ParticlePacket implements IMessage {

    private String particleName;
    private double x, y, z;
    private double velocityX, velocityY, velocityZ;
    private int frequency;

    public ParticlePacket() {} // Required

    public ParticlePacket(String particleName, double x, double y, double z, int frequency) {
        this.particleName = particleName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.velocityX = 0;
        this.velocityY = 0;
        this.velocityZ = 0;
        this.frequency = frequency;
    }

    public ParticlePacket(String particleName, double x, double y, double z, int frequency, double velocityX,
        double velocityY, double velocityZ) {
        this.particleName = particleName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.frequency = frequency;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.particleName = ByteBufUtils.readUTF8String(buf);
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.velocityX = buf.readDouble();
        this.velocityY = buf.readDouble();
        this.velocityZ = buf.readDouble();
        this.frequency = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, particleName);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(velocityX);
        buf.writeDouble(velocityY);
        buf.writeDouble(velocityZ);
        buf.writeInt(frequency);
    }

    public static class Handler implements IMessageHandler<ParticlePacket, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(ParticlePacket message, MessageContext ctx) {
            World world = Minecraft.getMinecraft().theWorld;
            for (int i = 0; i < message.frequency; i++) { // Send the particle multiple times based on frequency
                world.spawnParticle(
                    message.particleName,
                    message.x,
                    message.y,
                    message.z,
                    message.velocityX,
                    message.velocityY,
                    message.velocityZ
                );
            }
            return null;
        }
    }
}
