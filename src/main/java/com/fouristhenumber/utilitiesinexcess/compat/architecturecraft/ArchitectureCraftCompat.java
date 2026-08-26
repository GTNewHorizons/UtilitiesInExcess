package com.fouristhenumber.utilitiesinexcess.compat.architecturecraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.util.ItemUtil;

import gcewing.architecture.ArchitectureCraft;
import gcewing.architecture.common.tile.TileShape;
import gcewing.architecture.compat.BlockPos;

/**
 * Allows exact ArchitectureCraft shape copying with BuildersWand.
 *
 * ArchitectureCraft does not copy rotation and cladding into the ItemStack.
 * -> Needs to be copied from source TE to the target TE after placement.
 */
public final class ArchitectureCraftCompat {

    private ArchitectureCraftCompat() {}

    /**
     * Tries to copy the parts of a shape that live in the TileEntity rather than the item.
     * - Orientation is always copied
     * - cladding only if the player still has the matching one to spend
     *
     * @return true if the destination was an ArchitectureCraft shape and was updated
     */
    public static boolean tryCopyShapeState(World world, int fromX, int fromY, int fromZ, int toX, int toY, int toZ,
        EntityPlayer player) {

        TileShape source = TileShape.get(world, new BlockPos(fromX, fromY, fromZ));
        TileShape target = TileShape.get(world, new BlockPos(toX, toY, toZ));
        if (source == null || target == null) return false;

        target.setSide(source.side);
        target.setTurn(source.turn);
        target.setOffsetX(source.getOffsetX());
        target.disabledConnections = source.disabledConnections;

        applyCladding(source, target, player);

        target.markChanged();
        return true;
    }

    /**
     * Tries to copy the cladding if player has it in the inventory
     */
    private static void applyCladding(TileShape source, TileShape target, EntityPlayer player) {
        if (source.secondaryBlockState == null) return;

        ItemStack wanted = ArchitectureCraft.content.itemCladding.newStack(source.secondaryBlockState, 1);
        if (wanted == null) return;

        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack slot = player.inventory.mainInventory[i];
            if (!ItemUtil.areStacksEqual(slot, wanted)) continue;

            // decrements the itemStack size
            target.applySecondaryMaterial(slot, player);
            if (slot.stackSize <= 0) player.inventory.setInventorySlotContents(i, null);

            return;
        }
    }
}
