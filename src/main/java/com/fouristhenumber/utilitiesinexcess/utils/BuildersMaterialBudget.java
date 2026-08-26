package com.fouristhenumber.utilitiesinexcess.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.util.ItemUtil;

/**
 * Tracks how much of each material a fill has committed to, so it never promises more than the player owns.
 */
public final class BuildersMaterialBudget {

    private static final class Reservation {

        ItemStack stack;
        int remaining;
    }

    private final List<Reservation> reservations = new ArrayList<>();
    private final InventoryPlayer inventory;
    private final boolean creative;

    public BuildersMaterialBudget(InventoryPlayer inventory, boolean creative) {
        this.inventory = inventory;
        this.creative = creative;
    }

    /**
     * Claims one of the given material, returning false if none is left to spend.
     */
    public boolean tryReserve(ItemStack material) {
        for (Reservation reservation : reservations) {
            if (ItemUtil.areStacksEqual(reservation.stack, material)) {
                if (reservation.remaining <= 0) {
                    return false;
                }
                reservation.remaining--;
                return true;
            }
        }

        int owned = 0;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack inSlot = inventory.getStackInSlot(slot);
            if (inSlot != null && ItemUtil.areStacksEqual(inSlot, material)) {
                owned += inSlot.stackSize;
            }
        }

        // Creative still requires owning one, matching the stock wand.
        if (owned == 0) {
            return false;
        }
        if (creative) {
            owned = Integer.MAX_VALUE;
        }

        Reservation reservation = new Reservation();
        reservation.stack = material;
        reservation.remaining = owned - 1;
        reservations.add(reservation);
        return true;
    }
}
