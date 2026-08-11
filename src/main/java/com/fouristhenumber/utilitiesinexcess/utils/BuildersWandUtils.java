package com.fouristhenumber.utilitiesinexcess.utils;

import static com.fouristhenumber.utilitiesinexcess.utils.BuildersBlockSelectionFilter.getBlockByLocation;
import static com.fouristhenumber.utilitiesinexcess.utils.BuildersBlockSelectionFilter.isTrowel;
import static com.fouristhenumber.utilitiesinexcess.utils.MovingObjectPositionUtil.TranslateMovingObjectPositionToLocation;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.util.ItemUtil;

import gregtech.api.items.MetaGeneratedTool;
import xonin.backhand.api.core.BackhandUtils;

public class BuildersWandUtils {

    private BuildersWandUtils() {}

    /**
     * Decreases an ItemStack containing the item in the player's inventory by 1
     *
     * @param player    The player in question
     * @param itemStack The itemstack to compare against, including metadata and NBT
     * @return True if the ItemStack has been decremented, otherwise false
     */
    public static boolean decreaseFromInventory(EntityPlayer player, ItemStack itemStack) {
        for (int slotIndex = player.inventory.mainInventory.length - 1; slotIndex >= 0; slotIndex--) {
            ItemStack stack = player.inventory.mainInventory[slotIndex];
            if (ItemUtil.areStacksEqual(stack, itemStack)) {
                stack.stackSize -= 1;
                if (stack.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(slotIndex, null);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean containsItemStack(InventoryPlayer player, ItemStack itemStack) {
        for (int slotIndex = player.mainInventory.length - 1; slotIndex >= 0; slotIndex--) {
            ItemStack stack = player.mainInventory[slotIndex];
            if (ItemUtil.areStacksEqual(stack, itemStack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the blocks adjacent to the start position that are connected cardinally, or diagonally
     * and have a placeable block (e.g. air, fluid) in front of them relative to the side clicked on.
     *
     * @param world       The world in which to place
     * @param maxCount    The maximum amount of blocks it should search
     * @param filter      The pattern used to search adjacent blocks
     * @param mop         The position of the block that was clicked (with its clicked side)
     * @param blockPicker decides which block to place
     * @return The set of 1<=x<=maxCount adjacent blocks with air on their face
     */
    public static Set<BlockPos> findAdjacentBlocks(World world, int maxCount, MovingObjectPosition mop,
        EntityPlayer player, BuildersBlockSelectionFilter filter, BuildersBlockPicker blockPicker,
        BuildersWandUtils.WandAxisMode axisMode) {
        Set<BlockPos> region = LinkedHashSet.newLinkedHashSet(maxCount);
        if (maxCount <= 0) {
            return region;
        }
        Set<BlockPos> visited = HashSet.newHashSet(maxCount);
        Queue<BlockPos> queue = new LinkedList<>();

        // copy the mop so we can translate it without affecting the original
        mop = MovingObjectPositionUtil.copy(mop);

        // Determine allowed offsets depending on the face that was clicked.
        int[][] allowedOffsets = switch (ForgeDirection.getOrientation(mop.sideHit)) {
            case UP, DOWN -> switch (axisMode) {
                    case FREE -> new int[][] { { 1, 0, 0 }, { -1, 0, 0 }, { 0, 0, 1 }, { 0, 0, -1 }, { 1, 0, 1 },
                        { 1, 0, -1 }, { -1, 0, 1 }, { -1, 0, -1 } };
                    case HORIZONTAL -> new int[][] { { 1, 0, 0 }, { -1, 0, 0 } };
                    case VERTICAL -> new int[][] { { 0, 0, 1 }, { 0, 0, -1 } };
                };
            case NORTH, SOUTH -> switch (axisMode) {
                    case FREE -> new int[][] { { 1, 0, 0 }, { -1, 0, 0 }, { 0, 1, 0 }, { 0, -1, 0 }, { 1, 1, 0 },
                        { 1, -1, 0 }, { -1, 1, 0 }, { -1, -1, 0 } };
                    case HORIZONTAL -> new int[][] { { 1, 0, 0 }, { -1, 0, 0 } };
                    case VERTICAL -> new int[][] { { 0, 1, 0 }, { 0, -1, 0 } };
                };
            case EAST, WEST -> switch (axisMode) {
                    case FREE -> new int[][] { { 0, 1, 0 }, { 0, -1, 0 }, { 0, 0, 1 }, { 0, 0, -1 }, { 0, 1, 1 },
                        { 0, 1, -1 }, { 0, -1, 1 }, { 0, -1, -1 } };
                    case HORIZONTAL -> new int[][] { { 0, 0, 1 }, { 0, 0, -1 } };
                    case VERTICAL -> new int[][] { { 0, 1, 0 }, { 0, -1, 0 } };
                };
            default -> throw new RuntimeException("UE's BuilderWand's findAdjacentBlocks called with invalid side");
        };

        // start block
        BlockPos startPos = new BlockPos(mop.blockX, mop.blockY, mop.blockZ);
        queue.add(startPos);
        visited.add(startPos);

        // Flood-fill the contiguous region in the allowed plane.
        while (!queue.isEmpty() && region.size() < maxCount) {
            BlockPos current = queue.poll();

            // translate the mop
            TranslateMovingObjectPositionToLocation(mop, current);
            if (!IsValidForWireFrame(world, mop, player, filter, blockPicker)) continue;

            region.add(current);

            for (int[] off : allowedOffsets) {
                if (region.size() >= maxCount) break;
                BlockPos key = current.offset(off[0], off[1], off[2]);
                if (visited.contains(key)) continue;
                visited.add(key);
                queue.add(key);
            }
        }
        return region;
    }

    private static boolean IsValidForWireFrame(World world, MovingObjectPosition mop, EntityPlayer player,
        BuildersBlockSelectionFilter filter, BuildersBlockPicker blockPicker) {

        // 0. cannot place on air
        ItemStack sourceBlock = getBlockByLocation(world, mop, player);
        if (sourceBlock == null) return false;

        // 1. matches the filter?
        if (!filter.matches(sourceBlock)) return false;

        // 2. pick a suitable block
        return blockPicker.pickSomeBlock(mop, sourceBlock);
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

    public static boolean canPlaceBlock(World world, ItemStack targetBlock, MovingObjectPosition mop) {
        Block block = Block.getBlockFromItem(targetBlock.getItem());
        ForgeDirection direction = ForgeDirection.getOrientation(mop.sideHit);
        BlockPos targetPos = new BlockPos(
            mop.blockX + direction.offsetX,
            mop.blockY + direction.offsetY,
            mop.blockZ + direction.offsetZ);

        // Blocks with no collision box (torches, rails, plants) are placeable through entities,
        // For the rest use a whole cube, otherwise weird things happen, better safe than sorry.
        boolean solid = block.getCollisionBoundingBoxFromPool(world, targetPos.x, targetPos.y, targetPos.z) != null;

        return block.canPlaceBlockOnSide(world, targetPos.x, targetPos.y, targetPos.z, mop.sideHit) && world
            .canPlaceEntityOnSide(block, targetPos.x, targetPos.y, targetPos.z, true, mop.sideHit, null, targetBlock)
            && (!solid || world.checkNoEntityCollision(
                AxisAlignedBB.getBoundingBox(
                    targetPos.x,
                    targetPos.y,
                    targetPos.z,
                    targetPos.x + 1,
                    targetPos.y + 1,
                    targetPos.z + 1)));
    }

    public enum WandAxisMode {
        FREE,
        HORIZONTAL,
        VERTICAL
    }
}
