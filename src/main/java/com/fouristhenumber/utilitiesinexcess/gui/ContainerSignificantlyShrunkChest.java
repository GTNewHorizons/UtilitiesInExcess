package com.fouristhenumber.utilitiesinexcess.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySignificantlyShrunkChest;

public class ContainerSignificantlyShrunkChest extends Container {

    protected TileEntitySignificantlyShrunkChest chest;

    public ContainerSignificantlyShrunkChest(InventoryPlayer pInventoryPlayer, TileEntitySignificantlyShrunkChest te) {
        chest = te;
        addSlotToContainer(new Slot(te, 0, 12 + 4 * 18, 8 + 2 * 18));
        chest.openInventory();

        bindPlayerInventory(pInventoryPlayer, 184, 184);
    }

    @Override
    public boolean canInteractWith(EntityPlayer pPlayer) {
        return chest.isUseableByPlayer(pPlayer);
    }

    protected void bindPlayerInventory(InventoryPlayer playerInv, int sizex, int sizey) {
        int leftColumn = (sizex - 162) / 2 + 1;
        for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++) {
            for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++) {
                addSlotToContainer(
                    new Slot(
                        playerInv,
                        playerInvCol + playerInvRow * 9 + 9,
                        leftColumn + playerInvCol * 18,
                        sizey - (4 - playerInvRow) * 18 - 10));
            }
        }

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            addSlotToContainer(new Slot(playerInv, hotbarSlot, leftColumn + hotbarSlot * 18, sizey - 24));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            stack = stackInSlot.copy();

            if (index < chest.getSizeInventory()) {
                if (!mergeItemStack(stackInSlot, chest.getSizeInventory(), 36 + chest.getSizeInventory(), true)) {
                    return null;
                }
            } else if (!mergeItemStack(stackInSlot, 0, chest.getSizeInventory(), false)) {
                return null;
            }

            if (stackInSlot.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (stackInSlot.stackSize == stack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, stackInSlot);
        }
        return stack;
    }
}
