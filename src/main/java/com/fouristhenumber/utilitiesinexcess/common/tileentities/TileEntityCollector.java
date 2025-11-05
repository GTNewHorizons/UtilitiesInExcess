package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class TileEntityCollector extends TileEntity {

    public boolean showBorder = true;
    public int borderTimer = 0;
    int tickCount = 0;
    IInventory inventory;
    public List<Vec3> itemPositions = new ArrayList<>();
    private float size = 6f;

    @Override
    public void validate() {
        super.validate();
    }

    public void showBorderFor(int ticks) {
        this.showBorder = true;
        this.borderTimer = ticks;
    }

    public void incrementSize() {
        size++;
        if (size > 9) size = 1;
    }

    @Override
    public void updateEntity() {
        List<EntityItem> items = worldObj.getEntitiesWithinAABB(EntityItem.class, getRadiusAABB());

        if (worldObj.isRemote) {
            // client: just visual
            itemPositions.clear();
            for (EntityItem item : items) {
                if (!item.isDead) itemPositions.add(Vec3.createVectorHelper(item.posX, item.posY + 0.25, item.posZ));
            }
        }

        tickCount++;
        if (!(tickCount % 20 == 0)) {
            return;
        }

        TileEntity below = worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
        if (!(below instanceof IInventory)) return;

        inventory = (IInventory) below;

        for (EntityItem item : items) {
            if (item.isDead) continue;
            if (!item.onGround) continue;
            // Only collect after a short delay (20 ticks = 1 second)
            if (item.delayBeforeCanPickup > 0) continue;

            ItemStack stackToInsert = item.getEntityItem();
            if (stackToInsert == null) continue;

            for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
                ItemStack currentStack = inventory.getStackInSlot(slot);

                if (currentStack == null) {
                    inventory.setInventorySlotContents(slot, stackToInsert);
                    inventory.markDirty();
                    item.setDead();
                    break;
                } else if (currentStack.isItemEqual(stackToInsert)
                    && ItemStack.areItemStackTagsEqual(currentStack, stackToInsert)) {
                        int maxStack = Math.min(currentStack.getMaxStackSize(), inventory.getInventoryStackLimit());
                        int space = maxStack - currentStack.stackSize;

                        if (space > 0) {
                            if (stackToInsert.stackSize <= space) {
                                currentStack.stackSize += stackToInsert.stackSize;
                                inventory.markDirty();
                                item.setDead();
                                break;
                            } else {
                                currentStack.stackSize += space;
                                stackToInsert.stackSize -= space;
                                inventory.markDirty();
                            }
                        }
                    }
            }
        }
    }

    private AxisAlignedBB getRadiusAABB() {
        return AxisAlignedBB.getBoundingBox(
            xCoord - size,
            yCoord - size,
            zCoord - size,
            xCoord + size + 1,
            yCoord + size + 1,
            zCoord + size + 1);
    }

}
