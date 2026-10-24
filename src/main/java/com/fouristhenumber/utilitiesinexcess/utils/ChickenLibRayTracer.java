package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Gracefully ~~stolen~~ borrowed from CodeChickenLib: codechicken.lib.raytracer.RayTracer, only the part needed to
 * trace one block again. Copied rather than called, so nothing here depends on CodeChickenLib being loaded.
 */
public final class ChickenLibRayTracer {

    private ChickenLibRayTracer() {}

    public static MovingObjectPosition retraceBlock(World world, EntityPlayer player, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);

        Vec3 headVec = getCorrectedHeadVec(player);
        Vec3 lookVec = player.getLook(1.0F);
        double reach = getBlockReachDistance(player);
        Vec3 endVec = headVec.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
        return block.collisionRayTrace(world, x, y, z, headVec, endVec);
    }

    public static Vec3 getCorrectedHeadVec(EntityPlayer player) {
        Vec3 v = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
        if (player.worldObj.isRemote) {
            v.yCoord += player.getEyeHeight() - player.getDefaultEyeHeight(); // compatibility with eye height changing
                                                                              // mods
        } else {
            v.yCoord += player.getEyeHeight();
            if (player instanceof EntityPlayerMP && player.isSneaking()) v.yCoord -= 0.08;
        }
        return v;
    }

    public static double getBlockReachDistance(EntityPlayer player) {
        return player.worldObj.isRemote ? getBlockReachDistance_client()
            : player instanceof EntityPlayerMP ? getBlockReachDistance_server((EntityPlayerMP) player) : 5D;
    }

    private static double getBlockReachDistance_server(EntityPlayerMP player) {
        return player.theItemInWorldManager.getBlockReachDistance();
    }

    @SideOnly(Side.CLIENT)
    private static double getBlockReachDistance_client() {
        return Minecraft.getMinecraft().playerController.getBlockReachDistance();
    }
}
