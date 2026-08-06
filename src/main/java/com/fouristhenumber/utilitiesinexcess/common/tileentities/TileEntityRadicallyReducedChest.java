package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.LimitingItemStackHandler;

public class TileEntityRadicallyReducedChest extends TileEntitySignificantlyShrunkChest
    implements IGuiHolder<PosGuiData>, ISidedInventory {

    @Override
    protected IItemHandler makeHandler() {
        return new LimitingItemStackHandler(chestContents, 1);
    }

    @Override
    public String getInventoryName() {
        return "tile.radically_reduced_chest.name";
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
        return new int[] { 0 };
    }

    @Override
    public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
        return chestContents[0] == null;
    }

    @Override
    public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return chestContents[0] == null;
    }
}
