package com.fouristhenumber.utilitiesinexcess.utils.bw;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import com.fouristhenumber.utilitiesinexcess.common.items.ItemScribe;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;

import gregtech.api.items.MetaGeneratedTool;
import gregtech.common.tools.ToolTrowel;
import xonin.backhand.api.core.BackhandUtils;

/**
 * What the offhand tells the Builder's Wand to do.
 * <ul>
 * <li>EXTRUDE: a Scribe, every cell is pushed out by one, reproduced exactly as it is
 * <li>TROWEL: paints the hotbar blocks over any surface
 * <li>OFFHAND_BLOCK: extends the clicked kind of surface with the offhand block
 * <li>CLICKED_BLOCK: extends the clicked kind of surface with more of the same
 * </ul>
 */
public enum BWMode {

    EXTRUDE,
    TROWEL,
    OFFHAND_BLOCK,
    CLICKED_BLOCK;

    public static BWMode of(EntityPlayer player) {
        ItemStack offhand = offhand(player);
        if (isScribe(offhand)) return EXTRUDE;
        if (isTrowel(offhand)) return TROWEL;
        if (isItemBlock(offhand)) return OFFHAND_BLOCK;
        return CLICKED_BLOCK;
    }

    @Nullable
    static ItemStack offhand(EntityPlayer player) {
        return Mods.Backhand.isLoaded() ? BackhandUtils.getOffhandItem(player) : null;
    }

    /** The Backhand offhand slot index in mainInventory, -1 without Backhand. */
    static int offhandSlot(EntityPlayer player) {
        return Mods.Backhand.isLoaded() ? BackhandUtils.getOffhandSlot(player) : -1;
    }

    /** Trowels wear out as they are used; false when the trowel cannot take the damage. */
    static boolean tryDamageTrowel(EntityPlayer player, int damage) {
        if (player.capabilities.isCreativeMode) return true;

        ItemStack offhand = offhand(player);
        if (!isTrowel(offhand)) return true;

        return ((MetaGeneratedTool) offhand.getItem()).doDamage(offhand, damage);
    }

    static boolean isTrowel(@Nullable ItemStack stack) {
        if (stack == null || !Mods.GregTech.isLoaded()) return false;

        return stack.getItem() instanceof MetaGeneratedTool tool && tool.getToolStats(stack) instanceof ToolTrowel;
    }

    static boolean isItemBlock(@Nullable ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemBlock;
    }

    private static boolean isScribe(@Nullable ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemScribe;
    }
}
