package com.fouristhenumber.utilitiesinexcess.utils;

import static com.fouristhenumber.utilitiesinexcess.utils.ArchitectsSelection.getBlockByLocation;
import static com.fouristhenumber.utilitiesinexcess.utils.ArchitectsSelection.isTrowel;
import static com.fouristhenumber.utilitiesinexcess.utils.MovingObjectPositionUtil.TranslateMovingObjectPoistionToLocation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import gregtech.api.items.MetaGeneratedTool;
import xonin.backhand.api.core.BackhandUtils;

public class ArchitectsWandUtils {

    public ArchitectsWandUtils() {}

    ;

    /**
     * Counts the items of a certain type in a player's inventory
     *
     * @param player    The player whose inventory to coutnt
     * @param itemStack The itemstack incl metadata to count
     * @return The item count
     */
    public static int countItemInInventory(EntityPlayer player, ItemStack itemStack) {
        int count = 0;

        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() == itemStack.getItem()
                && stack.getItemDamage() == itemStack.getItemDamage()) {
                count += stack.stackSize;
            }
        }

        return count;
    }

    /**
     * Decreases an ItemStack containing the item in the player's inventory by 1
     *
     * @param player    The player in question
     * @param itemStack The itemstack with metadata to compare
     * @return True if the ItemStack has been decremented, otherwise false
     */
    public static boolean decreaseFromInventory(EntityPlayer player, ItemStack itemStack) {
        for (int slotIndex = player.inventory.mainInventory.length - 1; slotIndex >= 0; slotIndex--) {
            ItemStack stack = player.inventory.mainInventory[slotIndex];
            if (stack != null && stack.getItem() == itemStack.getItem()
                && stack.getItemDamage() == itemStack.getItemDamage()) {
                stack.stackSize -= 1;
                if (stack.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(slotIndex, null);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the blocks adjacent to the start position that are connected cardinally, or diagonally
     * and have air in front of them relative to the side clicked on.
     *
     * @param world               The world in which to place
     * @param findCount           The maximum amount of blocks it should search
     * @param clickedSide         The side of the block that was clicked
     * @param startPos            The position to start
     * @param architectsSelection The pattern used to search adjacent blocks
     * @return The set of 1<=x<=findCount adjacent blocks with air on their face
     */
    public static Set<BlockPos> findAdjacentBlocks(World world, List<ItemStack> possiblePlacements, int findCount,
        ForgeDirection clickedSide, BlockPos startPos, MovingObjectPosition mop, EntityPlayer player,
        ArchitectsSelection architectsSelection) {
        Set<BlockPos> region = new HashSet<>();
        if (findCount <= 0) {
            return region;
        }
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        // Determine allowed offsets depending on the face that was clicked.
        int[][] allowedOffsets = switch (clickedSide) {
            case UP, DOWN ->
                // Plane: x/z plane (y remains constant)
                new int[][] { { 1, 0, 0 }, { -1, 0, 0 }, { 0, 0, 1 }, { 0, 0, -1 }, // Cardinal
                    { 1, 0, 1 }, { 1, 0, -1 }, { -1, 0, 1 }, { -1, 0, -1 } }; // Diagonal
            case NORTH, SOUTH ->
                // Plane: x/y plane (z remains constant)
                new int[][] { { 1, 0, 0 }, { -1, 0, 0 }, { 0, 1, 0 }, { 0, -1, 0 }, // Cardinal
                    { 1, 1, 0 }, { 1, -1, 0 }, { -1, 1, 0 }, { -1, -1, 0 } }; // Diagonal
            case EAST, WEST ->
                // Plane: y/z plane (x remains constant)
                new int[][] { { 0, 1, 0 }, { 0, -1, 0 }, { 0, 0, 1 }, { 0, 0, -1 }, // Cardinal
                    { 0, 1, 1 }, { 0, 1, -1 }, { 0, -1, 1 }, { 0, -1, -1 } }; // Diagonal
            default -> throw new RuntimeException("UE's BuilderWand's findAdjacentBlocks called with invalid side");
        };

        // translate the mop
        TranslateMovingObjectPoistionToLocation(mop, startPos);

        // Base case
        if (IsValidForWireFrame(world, possiblePlacements, startPos, mop, player, clickedSide, architectsSelection)) {
            region.add(startPos);
            queue.add(startPos);
            visited.add(startPos);
        } else {
            return region;
        }

        // Flood-fill the contiguous region in the allowed plane.
        while (!queue.isEmpty() && region.size() < findCount) {
            BlockPos current = queue.poll();

            for (int[] off : allowedOffsets) {
                if (region.size() >= findCount) {
                    break;
                }

                BlockPos key = current.offset(off[0], off[1], off[2]);
                if (visited.contains(key)) {
                    continue;
                }
                visited.add(key);

                // translate the mop
                TranslateMovingObjectPoistionToLocation(mop, key);
                if (IsValidForWireFrame(
                    world,
                    possiblePlacements,
                    key,
                    mop,
                    player,
                    clickedSide,
                    architectsSelection)) {
                    region.add(key);
                    queue.add(key);
                }
            }
        }

        return region;
    }

    private static boolean IsValidForWireFrame(World world, List<ItemStack> possibleBlocks, BlockPos targetLocation,
        MovingObjectPosition mop, EntityPlayer player, ForgeDirection clickedSide, ArchitectsSelection selection) {
        ItemStack currentBlock = getBlockByLocation(world, mop, player);

        if (currentBlock == null) return false;

        return possibleBlocks.stream()
            .allMatch(itemStackToPlace -> {
                if (itemStackToPlace == null) return false;

                Block block = Block.getBlockFromItem(itemStackToPlace.getItem());
                return selection.matches(currentBlock)
                    && world.isAirBlock(
                        targetLocation.x + clickedSide.offsetX,
                        targetLocation.y + clickedSide.offsetY,
                        targetLocation.z + clickedSide.offsetZ)
                    && block.canPlaceBlockOnSide(
                        world,
                        targetLocation.x + clickedSide.offsetX,
                        targetLocation.y + clickedSide.offsetY,
                        targetLocation.z + clickedSide.offsetZ,
                        clickedSide.ordinal())
                    && world.canPlaceEntityOnSide(
                        block,
                        targetLocation.x + clickedSide.offsetX,
                        targetLocation.y + clickedSide.offsetY,
                        targetLocation.z + clickedSide.offsetZ,
                        false,
                        clickedSide.ordinal(),
                        null,
                        itemStackToPlace);
            });
    }

    public static boolean damageBackhand(int damage, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode && Mods.Backhand.isLoaded()
            && isTrowel(BackhandUtils.getOffhandItem(player))) {
            MetaGeneratedTool trowel = (MetaGeneratedTool) Objects.requireNonNull(BackhandUtils.getOffhandItem(player))
                .getItem();
            if (trowel == null) {
                return true;
            }
            return trowel.doDamage(BackhandUtils.getOffhandItem(player), damage);
        }
        return true;
    }
}
