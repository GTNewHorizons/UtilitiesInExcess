package com.fouristhenumber.utilitiesinexcess.utils.bw;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.fouristhenumber.utilitiesinexcess.utils.InventoryBudget;
import com.gtnewhorizon.gtnhlib.util.ItemUtil;

/** Everything a cell handler needs during one pass of a Builder's Wand use. */
public final class BWContext {

    public final World world;
    public final EntityPlayer player;
    public final BWMode mode;
    public final BWBlockPicker picker;
    public final InventoryBudget budget;
    /** The clicked cell, traced again; a multipart hands back an ExtendedMOP naming the part. */
    public final MovingObjectPosition originalMop;
    /** What the clicked cell hands over when picked, null when it cannot be expressed as one stack. */
    @Nullable
    public final ItemStack clickedBlockStack;

    public BWContext(World world, MovingObjectPosition originalMop, @Nullable ItemStack clickedBlockStack,
        EntityPlayer player, BWMode mode, BWBlockPicker picker, InventoryBudget budget) {
        this.world = world;
        this.player = player;
        this.mode = mode;
        this.picker = picker;
        this.budget = budget;
        this.originalMop = originalMop;
        this.clickedBlockStack = clickedBlockStack;
    }

    public boolean isCreative() {
        return player.capabilities.isCreativeMode;
    }

    public boolean isExtrudeMode() {
        return mode == BWMode.EXTRUDE;
    }

    public boolean isTrowelMode() {
        return mode == BWMode.TROWEL;
    }

    /** Whether the current mode spreads over any surface, no matter what is underneath */
    public boolean spreadsOverAnything() {
        return mode == BWMode.EXTRUDE || mode == BWMode.TROWEL;
    }

    /**
     * Takes one of the stack from the inventory, unless creative. Storage first, then the hotbar, the offhand last.
     */
    public void spend(ItemStack stack) {
        if (isCreative()) return;

        InventoryPlayer inventory = player.inventory;
        int offhand = BWMode.offhandSlot(player);
        for (int slot = inventory.mainInventory.length - 1; slot >= 0; slot--) {
            if (slot != offhand && take(inventory, slot, stack)) return;
        }
        if (offhand >= 0) take(inventory, offhand, stack);
    }

    private static boolean take(InventoryPlayer inventory, int slot, ItemStack stack) {
        ItemStack inSlot = inventory.mainInventory[slot];
        if (!ItemUtil.areStacksEqual(inSlot, stack)) return false;

        inSlot.stackSize--;
        if (inSlot.stackSize <= 0) inventory.setInventorySlotContents(slot, null);
        return true;
    }

}
