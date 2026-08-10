package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import static com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter.canStacksMerge;

public abstract class BaseTransferNodeLogic<T extends ITransferNetworkComponent> extends BaseNodeLogic<T> implements IInventory
{
    protected ItemStack buffer;
    protected boolean isStackUpgrade = false;

    public BaseTransferNodeLogic(T host) {
        super(host);
    }

    protected int addToOwnInventory(ItemStack sourceStack)
    {
        ItemStack existing = this.getStackInSlot(0);

        // empty slot
        if (existing == null)
        {
            ItemStack copy = sourceStack.copy();

            if (!this.isStackUpgrade)
            {
                copy.stackSize = 1;
            }

            this.setInventorySlotContents(0, copy);
            sourceStack.stackSize -= copy.stackSize;
            return copy.stackSize;
        }

        // can merge
        if (canStacksMerge(existing, sourceStack))
        {
            int space = existing.getMaxStackSize() - existing.stackSize;

            if (space <= 0)
                return 0;

            int toMove = this.isStackUpgrade ? Math.min(space, sourceStack.stackSize) : 1;

            existing.stackSize += toMove;
            this.setInventorySlotContents(0, existing);

            // shrink source
            sourceStack.stackSize -= toMove;

            return toMove;
        }

        return 0;
    }
}
