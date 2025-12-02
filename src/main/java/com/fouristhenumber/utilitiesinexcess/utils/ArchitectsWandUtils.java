package com.fouristhenumber.utilitiesinexcess.utils;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class ArchitectsWandUtils {

    public ArchitectsWandUtils() {};

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
        for (int slotIndex = 0; slotIndex < player.inventory.mainInventory.length; slotIndex++) {
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
     * Finds the blocks adjacent to the start position that are connected cardinally
     * and have air infront of them relative to the side clicked on.
     *
     * @param world       The world in which to place
     * @param blockToFind The block that is being found
     * @param metaToFind  The metadata of the block that is being found
     * @param findCount   The maximum amount of blocks it should search
     * @param clickedSide The side of the block that was clicked
     * @param startPos    The position to start
     * @return The set of 1<=x<=findCount adjacent blocks with air on their face
     */
    public static Set<BlockPos> findAdjacentBlocks(World world, Block blockToFind, int metaToFind, int findCount,
        ForgeDirection clickedSide, BlockPos startPos) {
        Set<BlockPos> region = new HashSet<>();
        if (findCount <= 0) {
            return region;
        }

        // Determine allowed offsets depending on the face that was clicked.
        int[][] allowedOffsets = switch (clickedSide) {
            case UP, DOWN ->
                // Plane: x/z plane (y remains constant)
                new int[][] { { 1, 0, 0 }, { 0, 0, 1 }, { -1, 0, 0 }, { 0, 0, -1 } };
            case NORTH, SOUTH ->
                // Plane: x/y plane (z remains constant)
                new int[][] { { 1, 0, 0 }, { 0, 1, 0 }, { -1, 0, 0 }, { 0, -1, 0 } };
            case EAST, WEST ->
                // Plane: y/z plane (x remains constant)
                new int[][] { { 0, 1, 0 }, { 0, 0, 1 }, { 0, -1, 0 }, { 0, 0, -1 } };
            default -> throw new RuntimeException("UE's BuilderWand's findAdjacentBlocks called with invalid side");
        };

        HashSet<BlockPos> validBlocks = new HashSet<>();

        int cx = startPos.x;
        int cy = startPos.y;
        int cz = startPos.z;

        int airx = cx + clickedSide.offsetX;
        int airy = cy + clickedSide.offsetY;
        int airz = cz + clickedSide.offsetZ;
        // base case
        if (world.getBlock(cx, cy, cz) == blockToFind && world.getBlockMetadata(cx, cy, cz) == metaToFind
            && world.isAirBlock(airx, airy, airz)) {
            region.add(new BlockPos(cx, cy, cz));
            for (int i = 0; i < allowedOffsets.length; i++) {
                int[] o = allowedOffsets[i];
                validBlocks.add(new BlockPos(cx + o[0], cy + o[1], cz + o[2]));
                int[] o2 = allowedOffsets[i == 3 ? 0 : i + 1];
                validBlocks.add(new BlockPos(cx + o[0] + o2[0], cy + o[1] + o2[1], cz + o[2] + o2[2]));
            }
        }

        // Rotate around the center and check
        int curOffset = 0;
        int curCount = 0;
        int maxCount = 1;
        while (region.size() < findCount && maxCount < findCount + 1) {
            int[] o = allowedOffsets[curOffset];
            cx += o[0];
            cy += o[1];
            cz += o[2];

            airx = cx + clickedSide.offsetX;
            airy = cy + clickedSide.offsetY;
            airz = cz + clickedSide.offsetZ;

            // check if this is a valid location
            if (world.getBlock(cx, cy, cz) == blockToFind && world.getBlockMetadata(cx, cy, cz) == metaToFind
                && world.isAirBlock(airx, airy, airz)) {

                BlockPos pos = new BlockPos(cx, cy, cz);

                if (validBlocks.remove(pos)) {
                    region.add(pos);

                    // mark adjacent blocks as valid
                    for (int i = 0; i < allowedOffsets.length; i++) {
                        int[] b = allowedOffsets[i];
                        validBlocks.add(new BlockPos(cx + b[0], cy + b[1], cz + b[2]));
                        int[] b2 = allowedOffsets[i == 3 ? 0 : i + 1];
                        validBlocks.add(new BlockPos(cx + b[0] + b2[0], cy + b[1] + b2[1], cz + b[2] + b2[2]));
                    }
                }
            }

            // Increment for next iteration
            curCount++;
            if (curCount == maxCount) {
                if (curOffset == 1 || curOffset == 3) {
                    maxCount++;
                }
                curCount = 0;
                curOffset = curOffset == 3 ? 0 : curOffset + 1;
            }
        }

        return region;
    }

}
