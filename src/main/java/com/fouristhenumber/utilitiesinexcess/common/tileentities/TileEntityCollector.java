package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.List;

public class TileEntityCollector extends TileEntity {

    public boolean showBorder = true;
    public int borderTimer = 0;
    int tickCount = 0;
    IInventory inventory;
    public List<Vec3> itemPositions = new ArrayList<>();
    private static final int RADIUS = 4;

    @Override
    public void validate() {
        super.validate();
    }

    public void showBorderFor(int ticks) {
        this.showBorder = true;
        this.borderTimer = ticks;
    }

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) {
            // client: just visual
            itemPositions.clear();
            List<EntityItem> nearbyItems = worldObj.getEntitiesWithinAABB(EntityItem.class, getRadiusAABB());
            for (EntityItem item : nearbyItems) {
                if (!item.isDead)
                    itemPositions.add(Vec3.createVectorHelper(item.posX, item.posY + 0.25, item.posZ));
            }
            return;
        }

        tickCount++;

        // Refresh inventory every second
        if (tickCount % 20 == 0) {
            TileEntity below = worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
            if (below instanceof IInventory inv) inventory = inv;
        }

        if (inventory == null) return;

        // Collect nearby items
        List<EntityItem> items = worldObj.getEntitiesWithinAABB(EntityItem.class, getRadiusAABB());
        for (EntityItem item : items) {
            if (item.isDead) continue;
            // make it move
            double dx = xCoord + 0.5 - item.posX;
            double dy = yCoord + 0.5 - item.posY;
            double dz = zCoord + 0.5 - item.posZ;
            double speed = 0.05;
            item.motionX += dx * speed;
            item.motionY += dy * speed;
            item.motionZ += dz * speed;
            // optionally absorb when close
        }
    }

    private AxisAlignedBB getRadiusAABB() {
        return AxisAlignedBB.getBoundingBox(
            xCoord - RADIUS, yCoord - RADIUS, zCoord - RADIUS,
            xCoord + RADIUS + 1, yCoord + RADIUS + 1, zCoord + RADIUS + 1
        );
    }


}
