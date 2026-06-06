package com.fouristhenumber.utilitiesinexcess.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
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
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        // Determine allowed offsets depending on the face that was clicked.
        int[][] allowedOffsets = switch (clickedSide) {
            case UP, DOWN ->
                // Plane: x/z plane (y remains constant)
                new int[][] { { 1, 0, 0 }, { -1, 0, 0 }, { 0, 0, 1 }, { 0, 0, -1 } };
            case NORTH, SOUTH ->
                // Plane: x/y plane (z remains constant)
                new int[][] { { 1, 0, 0 }, { -1, 0, 0 }, { 0, 1, 0 }, { 0, -1, 0 } };
            case EAST, WEST ->
                // Plane: y/z plane (x remains constant)
                new int[][] { { 0, 1, 0 }, { 0, -1, 0 }, { 0, 0, 1 }, { 0, 0, -1 } };
            default -> throw new RuntimeException("UE's BuilderWand's findAdjacentBlocks called with invalid side");
        };

        int sx = startPos.x;
        int sy = startPos.y;
        int sz = startPos.z;

        // Base case
        if (world.getBlock(sx, sy, sz) == blockToFind && world.getBlockMetadata(sx, sy, sz) == metaToFind
            && world.isAirBlock(sx + clickedSide.offsetX, sy + clickedSide.offsetY, sz + clickedSide.offsetZ)) {

            BlockPos neighbor = new BlockPos(sx, sy, sz);
            region.add(neighbor);
            queue.add(neighbor);
            visited.add(startPos);
        } else {
            return region;
        }

        // Flood-fill the contiguous region in the allowed plane.
        while (!queue.isEmpty() && region.size() < findCount) {
            BlockPos current = queue.poll();
            int cx = current.x;
            int cy = current.y;
            int cz = current.z;

            for (int[] off : allowedOffsets) {
                // Check if already visited
                int nx = cx + off[0];
                int ny = cy + off[1];
                int nz = cz + off[2];

                BlockPos key = new BlockPos(nx, ny, nz);
                if (visited.contains(key)) {
                    continue;
                }
                visited.add(key);

                // Check and add to region+queue
                int airx = nx + clickedSide.offsetX;
                int airy = ny + clickedSide.offsetY;
                int airz = nz + clickedSide.offsetZ;

                if (world.getBlock(nx, ny, nz) == blockToFind && world.getBlockMetadata(nx, ny, nz) == metaToFind
                    && world.isAirBlock(airx, airy, airz)) {

                    BlockPos neighbor = new BlockPos(nx, ny, nz);
                    region.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return region;
    }

}
