package com.fouristhenumber.utilitiesinexcess.network.client;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.world.World;

import org.joml.Vector3d;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/// Spawns some particles and plays a sound when the player is attacked by the invisible monster
public class PacketUnderworldAttack implements IMessage {

    public PacketUnderworldAttack() {}

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<PacketUnderworldAttack, IMessage> {

        @Override
        public IMessage onMessage(PacketUnderworldAttack message, MessageContext ctx) {
            World world = Minecraft.getMinecraft().theWorld;

            Random rng = ThreadLocalRandom.current();

            double theta1 = rng.nextDouble() * Math.PI * 2;
            double theta2 = theta1 + Math.PI + (rng.nextDouble() * 0.25 - 0.125);

            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            Vector3d a = new Vector3d(
                player.posX + Math.cos(theta1) * 2,
                player.posY,
                player.posZ + Math.sin(theta1) * 2);
            Vector3d b = new Vector3d(
                player.posX + Math.cos(theta2) * 2,
                player.posY,
                player.posZ + Math.sin(theta2) * 2);

            for (float k = 0; k <= 1f; k += 0.05f) {
                Vector3d v = new Vector3d(b).sub(a)
                    .mul(k)
                    .add(a);

                world.spawnParticle(
                    "crit",
                    v.x,
                    v.y,
                    v.z,
                    rng.nextDouble() * 0.02 - 0.01,
                    rng.nextDouble() * 0.02 - 0.01,
                    rng.nextDouble() * 0.02 - 0.01);
            }

            Vector3d v = new Vector3d(b).sub(a)
                .mul(rng.nextDouble())
                .add(a);

            world.playSound(v.x, v.y, v.z, "mob.bat.takeoff", 1, 1, false);

            return null;
        }
    }
}
