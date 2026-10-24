package com.fouristhenumber.utilitiesinexcess.utils.bw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.fouristhenumber.utilitiesinexcess.utils.InventoryBudget;
import com.fouristhenumber.utilitiesinexcess.utils.MovingObjectPositionUtil;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

/**
 * Chooses which block the Builder's Wand puts down
 * - extrude mode reproduces whatever the source cell is
 * - otherwise it draws from a palette: the offhand block, hotbar, or the clicked block
 */
public final class BWBlockPicker {

    private final World world;
    private final InventoryBudget budget;
    private final BWMode mode;
    private final Random random;

    /** The blocks this mode may place, empty while extruding: there every cell reproduces itself. */
    private final List<ItemStack> palette;

    private final int[] scratchTemplate;
    private final int[] scratch;

    public BWBlockPicker(World world, EntityPlayer player, BWMode mode, @Nullable ItemStack lookedAtBlock,
        InventoryBudget budget) {
        this.world = world;
        this.budget = budget;
        this.mode = mode;
        this.random = ThreadLocalRandom.current();
        this.palette = palette(mode, player, lookedAtBlock);

        this.scratch = new int[palette.size()];
        this.scratchTemplate = new int[palette.size()];
        for (int i = 0; i < palette.size(); i++) this.scratchTemplate[i] = i;
    }

    /**
     * Picks a block for this cell: one that fits the target cell and that the player can still pay for,
     * already reserved against the budget.
     *
     * @param mop         the source block on which the new one will be placed
     * @param sourceBlock the stack the source block hands over when picked
     * @param extraCosts  what this cell costs besides the block itself, reserved together with it
     * @return block to be placed, already reserved against the budget, or null
     */
    @Nullable
    public ItemStack pickBlockFor(MovingObjectPosition mop, @Nullable ItemStack sourceBlock,
        List<ItemStack> extraCosts) {
        // extrude: the cell is its own palette
        if (mode == BWMode.EXTRUDE) return tryClaim(sourceBlock, mop, extraCosts) ? sourceBlock : null;

        System.arraycopy(scratchTemplate, 0, scratch, 0, scratch.length);

        // try every block in the palette in a random order
        for (int i = 0; i < palette.size(); i++) {

            // shuffle one step
            int j = i + random.nextInt(palette.size() - i);
            int tmp = scratch[i];
            scratch[i] = scratch[j];
            scratch[j] = tmp;

            ItemStack stack = palette.get(scratch[i]);

            if (tryClaim(stack, mop, extraCosts)) return stack;
        }
        return null;
    }

    /** Whether the stack fits the cell and the budget, reserving it when it does. */
    private boolean tryClaim(@Nullable ItemStack stack, MovingObjectPosition mop, List<ItemStack> extraCosts) {
        return stack != null && canPlaceBlock(world, stack, mop) && tryReserve(stack, extraCosts);
    }

    /** Whether the stack could be placed into the target cell, the one in front of the clicked face. */
    private static boolean canPlaceBlock(World world, ItemStack toPlace, MovingObjectPosition mop) {
        // items like doors and signs place by their own rules, which nothing here can predict
        if (!BWMode.isItemBlock(toPlace)) return false;

        Block block = Block.getBlockFromItem(toPlace.getItem());
        BlockPos targetPos = MovingObjectPositionUtil.targetPos(mop);

        // Blocks with no collision box (torches, rails, plants) are placeable through entities,
        // For the rest use a whole cube, otherwise weird things happen, better safe than sorry.
        boolean solid = block.getCollisionBoundingBoxFromPool(world, targetPos.x, targetPos.y, targetPos.z) != null;

        return block.canPlaceBlockOnSide(world, targetPos.x, targetPos.y, targetPos.z, mop.sideHit)
            && world
                .canPlaceEntityOnSide(block, targetPos.x, targetPos.y, targetPos.z, true, mop.sideHit, null, toPlace)
            && (!solid || world.checkNoEntityCollision(
                AxisAlignedBB.getBoundingBox(
                    targetPos.x,
                    targetPos.y,
                    targetPos.z,
                    targetPos.x + 1,
                    targetPos.y + 1,
                    targetPos.z + 1)));
    }

    /** Reserves the block and everything else the cell costs, all of them or none. */
    private boolean tryReserve(ItemStack stack, List<ItemStack> extraCosts) {
        if (extraCosts.isEmpty()) return budget.tryReserve(stack);

        List<ItemStack> costs = new ArrayList<>(extraCosts.size() + 1);
        costs.add(stack);
        costs.addAll(extraCosts);
        return budget.tryReserve(costs);
    }

    private static List<ItemStack> palette(BWMode mode, EntityPlayer player, @Nullable ItemStack lookedAtBlock) {
        return switch (mode) {
            case EXTRUDE -> Collections.emptyList();
            case OFFHAND_BLOCK -> Collections.singletonList(BWMode.offhand(player));
            case TROWEL -> hotbarBlocks(player);
            case CLICKED_BLOCK -> lookedAtBlock == null ? Collections.emptyList()
                : Collections.singletonList(lookedAtBlock);
        };
    }

    private static List<ItemStack> hotbarBlocks(EntityPlayer player) {
        List<ItemStack> candidates = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            if (i == player.inventory.currentItem) {
                continue;
            }
            ItemStack item = player.inventory.mainInventory[i];
            if (!BWMode.isItemBlock(item)) {
                continue;
            }
            candidates.add(item.copy());
        }
        return candidates;
    }

}
