package com.fouristhenumber.utilitiesinexcess.utils;

import static com.fouristhenumber.utilitiesinexcess.utils.MovingObjectPositionUtil.TranslateMovingObjectPoistionToLocation;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import gregtech.api.items.MetaGeneratedTool;
import gregtech.common.tools.ToolTrowel;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import org.jetbrains.annotations.Nullable;
import xonin.backhand.api.core.BackhandUtils;

public class ArchitectsWandUtils {

    public ArchitectsWandUtils() {
    }

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
     * Finds the blocks adjacent to the start position that are connected cardinally, or diagonally
     * and have air in front of them relative to the side clicked on.
     *
     * @param world          The world in which to place
     * @param findCount      The maximum amount of blocks it should search
     * @param clickedSide    The side of the block that was clicked
     * @param startPos       The position to start
     * @param selectedBlocks The pattern used to search adjacent blocks
     * @return The set of 1<=x<=findCount adjacent blocks with air on their face
     */
    public static Set<BlockPos> findAdjacentBlocks(World world, ItemStack itemStackToPlace, int findCount,
                                                   ForgeDirection clickedSide, BlockPos startPos, MovingObjectPosition mop, EntityPlayer player,
                                                   Set<ItemStack> selectedBlocks) {
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
                new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}, // Cardinal
                    {1, 0, 1}, {1, 0, -1}, {-1, 0, 1}, {-1, 0, -1}}; // Diagonal
            case NORTH, SOUTH ->
                // Plane: x/y plane (z remains constant)
                new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, // Cardinal
                    {1, 1, 0}, {1, -1, 0}, {-1, 1, 0}, {-1, -1, 0}}; // Diagonal
            case EAST, WEST ->
                // Plane: y/z plane (x remains constant)
                new int[][]{{0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}, // Cardinal
                    {0, 1, 1}, {0, 1, -1}, {0, -1, 1}, {0, -1, -1}}; // Diagonal
            default -> throw new RuntimeException("UE's BuilderWand's findAdjacentBlocks called with invalid side");
        };

        // translate the mop
        TranslateMovingObjectPoistionToLocation(mop, startPos);

        // Base case
        if (IsValidForWireFrame(world, itemStackToPlace, startPos, mop, player, clickedSide, selectedBlocks)) {
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
                if (IsValidForWireFrame(world, itemStackToPlace, key, mop, player, clickedSide, selectedBlocks)) {
                    region.add(key);
                    queue.add(key);
                }
            }
        }

        return region;
    }

    private static boolean IsValidForWireFrame(World world, ItemStack itemStackToPlace, BlockPos targetLocation,
                                               MovingObjectPosition movingObjectPosition, EntityPlayer player, ForgeDirection clickedSide,
                                               Set<ItemStack> patternStack) {
        ItemStack currentBlock = getBlockByLocation(world, targetLocation, movingObjectPosition, player);
        if (currentBlock != null) {
            Block block = Block.getBlockFromItem(itemStackToPlace.getItem());
            UtilitiesInExcess.LOG.info("{}{}: {}", currentBlock, patternStack, patternStack.stream().filter(Objects::nonNull).anyMatch(selectedBlock -> ItemStack.areItemStacksEqual(selectedBlock, currentBlock)));
            return (patternStack.stream().filter(Objects::nonNull).anyMatch(selectedBlock -> selectedBlock.getItem() == currentBlock.getItem()) || ItemStack.areItemStacksEqual(currentBlock, itemStackToPlace))
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
        }
        return false;
    }

    private static boolean isTrowel(@Nullable ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack.getItem() instanceof MetaGeneratedTool metaGeneratedTool) {
            return metaGeneratedTool.getToolStats(stack) instanceof ToolTrowel;
        }
        return false;
    }

    private static List<ItemStack> HotbarBlocks(EntityPlayer player) {
        List<ItemStack> candidates = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            if (i == player.inventory.currentItem) {
                continue;
            }
            ItemStack item = player.inventory.mainInventory[i];
            if (item == null || !(item.getItem() instanceof ItemBlock)) {
                continue;
            }
            candidates.add(item);
        }
        return candidates;
    }

    private static ItemStack randomHotbarItem(EntityPlayer player) {
        List<ItemStack> candidates = HotbarBlocks(player);
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
    }

    public static ItemStack getItemStackToPlace(EntityPlayer player, Set<ItemStack> patternBlock) {
        if (Mods.Backhand.isLoaded()) {
            ItemStack offHandItem = BackhandUtils.getOffhandItem(player);
            if (offHandItem == null) {
                return patternBlock.stream().findFirst().orElse(null);
            }
            if (Mods.GT.isLoaded() && isTrowel(offHandItem)) {
                ItemStack randomItem = randomHotbarItem(player);
                return randomItem != null ? randomItem.copy() : null;
            }

            if (offHandItem.getItem() instanceof ItemBlock) {
                return offHandItem.copy();
            }
        }

        return patternBlock.stream().findFirst().orElse(null);
    }

    public static ItemStack getBlockByLocation(World world, BlockPos pos, MovingObjectPosition movingObjectPosition,
                                               EntityPlayer player) {
        Block block = world.getBlock(pos.x, pos.y, pos.z);
        if (block == null) {
            return null;
        }

        return block.getPickBlock(movingObjectPosition, world, pos.x, pos.y, pos.z, player);
    }

    /**
     * @return The block whose structure the wand must follow.
     */
    public static Set<ItemStack> getPatternBlock(World world, BlockPos pos, MovingObjectPosition movingObjectPosition,
                                                 EntityPlayer player) {
        if (Mods.GT.isLoaded() && Mods.Backhand.isLoaded() && isTrowel(BackhandUtils.getOffhandItem(player))) {
            HashSet<ItemStack> patternBlocks = new HashSet<>(HotbarBlocks(player));
//            patternBlocks.add(getBlockByLocation(world, pos, movingObjectPosition, player));
            return patternBlocks;
        }
        return Collections.singleton(getBlockByLocation(world, pos, movingObjectPosition, player));
    }
}
