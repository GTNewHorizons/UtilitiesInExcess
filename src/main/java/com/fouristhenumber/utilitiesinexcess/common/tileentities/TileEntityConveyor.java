package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockConveyor;

public class TileEntityConveyor extends TileEntity {

    /// The speed of the conveyor, measured in blocks per tick
    private static final float SPEED = 1f / 20f;

    @Override
    public void updateEntity() {
        super.updateEntity();

        ForgeDirection facing = BlockConveyor.getFacing(getBlockMetadata());

        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1);
        aabb.offset(xCoord, yCoord + 1, zCoord);

        List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, aabb);

        for (Entity entity : entities) {
            boolean valid = entity instanceof EntityLivingBase || entity instanceof EntityItem;

            if (!valid) continue;

            if (!entity.onGround) continue;
            if (entity.isSneaking()) continue;

            // Center the entity on the conveyor
            if (facing.offsetX != 0) {
                AxisAlignedBB entityAABB = entity.boundingBox;

                double centerZ = (entityAABB.minZ + entityAABB.maxZ) / 2;

                double targetZ = zCoord + 0.5;

                double deltaZ = targetZ - centerZ;

                entity.moveEntity(0, 0, Math.min(Math.abs(deltaZ), SPEED) * Math.signum(deltaZ));
            } else {
                AxisAlignedBB entityAABB = entity.boundingBox;

                double centerX = (entityAABB.minX + entityAABB.maxX) / 2;

                double targetX = xCoord + 0.5;

                double deltaX = targetX - centerX;

                entity.moveEntity(Math.min(Math.abs(deltaX), SPEED) * Math.signum(deltaX), 0, 0);
            }

            entity.moveEntity(facing.offsetX * SPEED, 0, facing.offsetZ * SPEED);
        }
    }
}
