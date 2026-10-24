package com.fouristhenumber.utilitiesinexcess.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.util.ItemUtil;

/**
 * Tracks what an inventory can still afford, without touching it.
 */
public final class InventoryBudget {

    private static final class Reservation {

        ItemStack stack;
        int remaining;
    }

    private final List<Reservation> reservations = new ArrayList<>();
    private final InventoryPlayer inventory;
    private final boolean creative;

    public InventoryBudget(InventoryPlayer inventory, boolean creative) {
        this.inventory = inventory;
        this.creative = creative;
    }

    /**
     * Claims one of every listed material, or none of them.
     *
     * @return true on success
     */
    public boolean tryReserve(List<ItemStack> materials) {
        int mark = reservations.size();
        int[] spent = new int[mark];
        for (int i = 0; i < mark; i++) spent[i] = reservations.get(i).remaining;

        for (ItemStack material : materials) {
            if (tryReserve(material)) continue;

            // Rollback
            for (int i = 0; i < mark; i++) reservations.get(i).remaining = spent[i];
            while (reservations.size() > mark) reservations.removeLast();
            return false;
        }
        return true;
    }

    /**
     * Claims one of the given material, or not.
     *
     * @return true on success
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
        for (int slot = 0; slot < inventory.mainInventory.length; slot++) {
            ItemStack inSlot = inventory.mainInventory[slot];
            if (inSlot != null && ItemUtil.areStacksEqual(inSlot, material)) {
                owned += inSlot.stackSize;
            }
        }

        // Creative still requires owning one, matching the stock wand.
        if (owned == 0) return false;

        if (creative) owned = Integer.MAX_VALUE;

        Reservation reservation = new Reservation();
        reservation.stack = material;
        reservation.remaining = owned - 1;
        reservations.add(reservation);
        return true;
    }
}
