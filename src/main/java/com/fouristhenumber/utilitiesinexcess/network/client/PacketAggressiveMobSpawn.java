package com.fouristhenumber.utilitiesinexcess.network.client;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

/// Spawns some smoke and plays a sound when a mob is aggressively spawn in a difficult region.
public class PacketAggressiveMobSpawn implements IMessage {

    private int x, y, z;

    public PacketAggressiveMobSpawn() {}

    public PacketAggressiveMobSpawn(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public static class Handler implements IMessageHandler<PacketAggressiveMobSpawn, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketAggressiveMobSpawn message, MessageContext ctx) {
            World world = Minecraft.getMinecraft().theWorld;

            Random rng = ThreadLocalRandom.current();

            int x = message.x;
            int y = message.y;
            int z = message.z;

            for (int i = 0; i < 20; i++) {
                world.spawnParticle(
                    "flame",
                    x + rng.nextDouble(),
                    y + rng.nextDouble(),
                    z + rng.nextDouble(),
                    rng.nextDouble() * 0.1 - 0.05,
                    rng.nextDouble() * 0.1 - 0.025,
                    rng.nextDouble() * 0.1 - 0.05);
            }

            for (int i = 0; i < 20; i++) {
                world.spawnParticle(
                    "smoke",
                    x + rng.nextDouble(),
                    y + rng.nextDouble(),
                    z + rng.nextDouble(),
                    rng.nextDouble() * 0.1 - 0.05,
                    rng.nextDouble() * 0.1 - 0.025,
                    rng.nextDouble() * 0.1 - 0.05);
            }

            world.playSound(x + 0.5, y + 0.5, z + 0.5, "mob.bat.takeoff", 1, 1, false);

            return null;
        }
    }
}
