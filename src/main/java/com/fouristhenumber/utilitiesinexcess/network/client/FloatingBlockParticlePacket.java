package com.fouristhenumber.utilitiesinexcess.network.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

/**
 * Spawns block break particles that fall towards the sky.
 */
public class FloatingBlockParticlePacket implements IMessage {

    private int x, y, z, blockId, meta;

    public FloatingBlockParticlePacket() {} // Required

    public FloatingBlockParticlePacket(int x, int y, int z, int blockId, int meta) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockId = blockId;
        this.meta = meta;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.blockId = buf.readInt();
        this.meta = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(blockId);
        buf.writeInt(meta);
    }

    public static class Handler implements IMessageHandler<FloatingBlockParticlePacket, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(FloatingBlockParticlePacket message, MessageContext ctx) {
            World world = Minecraft.getMinecraft().theWorld;
            Block block = Block.getBlockById(message.blockId);
            block.blockParticleGravity = -1.0f;
            for (int i = 0; i < 4; ++i) {
                for (int j = 0; j < 4; ++j) {
                    for (int k = 0; k < 4; ++k) {
                        double delta_x = (double) message.x + ((double) i + 0.5D) / (double) 4;
                        double delta_y = (double) message.y + ((double) j + 0.5D) / (double) 4;
                        double delta_z = (double) message.z + ((double) k + 0.5D) / (double) 4;
                        Minecraft.getMinecraft().effectRenderer.addEffect(
                            (new EntityDiggingFX(
                                world,
                                delta_x,
                                delta_y,
                                delta_z,
                                delta_x - (double) message.x - 0.5D,
                                delta_y - (double) message.y - 0.5D,
                                delta_z - (double) message.z - 0.5D,
                                block,
                                message.meta)).applyColourMultiplier(message.x, message.y, message.z));
                    }
                }
            }

            return null;
        }
    }
}
