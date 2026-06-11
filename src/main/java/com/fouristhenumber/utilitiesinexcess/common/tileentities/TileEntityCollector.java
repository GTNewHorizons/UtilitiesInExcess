package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
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

    private boolean showBorder = false;
    public int borderTimer = 0;
    public List<Vec3> itemPositions = new ArrayList<>();
    private float size = 4f;

    public float getSize() {
        return size;
    }

    public void incrementSize(EntityPlayer player) {
        if (player.isSneaking()) {

            size -= 0.5f;
            if (size == 0f) size = 4f;
        } else {
            size += 0.5f;
            if (size > 4f) size = 0.5f;
        }
    }

    public void showBorderFor(int ticks) {
        this.showBorder = true;
        this.borderTimer = ticks;
    }

    @Override
    public void updateEntity() {
        AxisAlignedBB area = getRadiusAABB();

        if (worldObj.isRemote) {
            updateClientEffects(area);
            return;
        }

        updateServerItemInsertion(area);
    }

    private void updateClientEffects(AxisAlignedBB area) {
        if (borderTimer > 0 && --borderTimer <= 0) {
            showBorder = false;
        }

        itemPositions.clear();

        for (EntityItem item : worldObj.getEntitiesWithinAABB(EntityItem.class, area)) {

            if (item.isDead || !item.onGround) {
                continue;
            }

            itemPositions.add(Vec3.createVectorHelper(item.posX, item.posY + 0.25, item.posZ));
        }
    }

    private void updateServerItemInsertion(AxisAlignedBB area) {
        TileEntity te = worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
        if (!(te instanceof IInventory chest)) {
            return;
        }

        ItemSink sink = ItemUtil.getItemSink(chest, ForgeDirection.UP);
        if (sink == null) {
            return;
        }

        for (EntityItem item : worldObj.getEntitiesWithinAABB(EntityItem.class, area)) {

            if (item.isDead || !item.onGround || item.delayBeforeCanPickup > 0) {
                continue;
            }

            ItemStack stack = item.getEntityItem();
            if (stack == null) {
                continue;
            }

            int leftover = sink.store(new InsertionItemStack(stack));

            if (leftover <= 0) {
                item.setDead();
            } else {
                stack.stackSize = leftover;
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

    public boolean showBorder() {
        return showBorder;
    }
}
