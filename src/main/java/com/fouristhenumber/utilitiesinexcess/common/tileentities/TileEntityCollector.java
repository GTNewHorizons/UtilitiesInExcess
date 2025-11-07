package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.capability.item.ItemSink;
import com.gtnewhorizon.gtnhlib.item.InsertionItemStack;
import com.gtnewhorizon.gtnhlib.util.ItemUtil;

public class TileEntityCollector extends TileEntity {

    public boolean showBorder = false;
    public int borderTimer = 0;
    public List<Vec3> itemPositions = new ArrayList<>();
    private float size = 6f;

    public float getSize() {
        return size;
    }

    public void incrementSize() {
        size++;
        if (size > 9) size = 1;
    }

    public void showBorderFor(int ticks) {
        this.showBorder = true;
        this.borderTimer = ticks;
    }

    @Override
    public void updateEntity() {

        if (worldObj.isRemote) {
            if (borderTimer > 0) {
                borderTimer--;
                if (borderTimer <= 0) showBorder = false;
            }

            itemPositions.clear();
            List<EntityItem> items = worldObj.getEntitiesWithinAABB(EntityItem.class, getRadiusAABB());
            for (EntityItem item : items) {
                if (!item.isDead && item.onGround) {
                    itemPositions.add(Vec3.createVectorHelper(item.posX, item.posY + 0.25, item.posZ));
                }
            }
        }

        if (!worldObj.isRemote) {
            List<EntityItem> items = worldObj.getEntitiesWithinAABB(EntityItem.class, getRadiusAABB());
            TileEntity chest = worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
            if (!(chest instanceof IInventory)) return;

            for (EntityItem item : items) {
                if (item.isDead || !item.onGround || item.delayBeforeCanPickup > 0) continue;

                ItemStack stackInsert = item.getEntityItem();
                if (stackInsert == null) continue;

                ItemSink sink = ItemUtil.getItemSink(chest, ForgeDirection.UP);
                if (sink != null) {
                    int leftover = sink.store(new InsertionItemStack(stackInsert));
                    if (leftover <= 0) item.setDead();
                    else stackInsert.stackSize = leftover;
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
