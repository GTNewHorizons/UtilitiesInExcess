package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.renderers.XRayRenderer;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class ItemXRayGlasses extends ItemArmor {

    static final int reach = 5;

    public ItemXRayGlasses(ArmorMaterial armorMaterial, int renderIndex, int armorType) {
        super(armorMaterial, renderIndex, armorType);
        setUnlocalizedName("xray_glasses");
        setTextureName("utilitiesinexcess:xray_glasses");
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return "utilitiesinexcess:textures/items/xray_glasses_model.png";
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        if (!world.isRemote || player.ticksExisted % 3 != 0) return;

        XRayRenderer.clearCandidatePositions();

        double eyeX = player.posX;
        double eyeY = player.posY + player.getEyeHeight();
        double eyeZ = player.posZ;

        Vec3 look = player.getLookVec();

        Vec3 end = Vec3
            .createVectorHelper(eyeX + look.xCoord * reach, eyeY + look.yCoord * reach, eyeZ + look.zCoord * reach);

        MovingObjectPosition mop = world.rayTraceBlocks(Vec3.createVectorHelper(eyeX, eyeY, eyeZ), end);

        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int x = mop.blockX;
            int y = mop.blockY;
            int z = mop.blockZ;
            Block targetBlock = world.getBlock(x, y, z);
            int targetMeta = world.getBlockMetadata(x, y, z);

            XRayRenderer.addCandidatePosition(new BlockPos(x, y, z));

            Set<BlockPos> visited = new HashSet<>();
            Queue<BlockPos> toVisit = new LinkedList<>();

            BlockPos origin = new BlockPos(x, y, z);
            toVisit.add(origin);
            visited.add(origin);

            int maxDistance = 5;

            while (!toVisit.isEmpty()) {
                BlockPos current = toVisit.poll();

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            if (dx == 0 && dy == 0 && dz == 0) continue;

                            int nx = current.x + dx;
                            int ny = current.y + dy;
                            int nz = current.z + dz;
                            BlockPos neighbor = new BlockPos(nx, ny, nz);

                            if (distanceSq(origin, neighbor) > maxDistance * maxDistance || visited.contains(neighbor))
                                continue;

                            Block b = world.getBlock(nx, ny, nz);
                            int m = world.getBlockMetadata(nx, ny, nz);
                            if (b == targetBlock && m == targetMeta) {
                                visited.add(neighbor);
                                toVisit.add(neighbor);
                                XRayRenderer.addCandidatePosition(neighbor);
                            }
                        }
                    }
                }
            }
        }
    }

    private double distanceSq(BlockPos a, BlockPos b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        double dz = a.z - b.z;
        return dx * dx + dy * dy + dz * dz;
    }
}
