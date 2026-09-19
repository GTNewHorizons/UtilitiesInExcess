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
public abstract class BWBlockPicker {

    protected final World world;
    protected final InventoryBudget budget;

    protected BWBlockPicker(World world, InventoryBudget budget) {
        this.world = world;
        this.budget = budget;
    }

    /**
     * pick a suitable block to be placed
     * -> has to be placeable, use canPlaceBlock()
     *
     * @param mop         the source block on which the new one will be placed
     * @param sourceBlock the stack the source block hands over when picked
     * @param extraCosts  what this cell costs besides the block itself, reserved together with it
     * @return block to be placed, already reserved against the budget, or null
     */
    public abstract ItemStack pickBlock(MovingObjectPosition mop, @Nullable ItemStack sourceBlock,
        List<ItemStack> extraCosts);

    /** Reserves the block and everything else the cell costs, all of them or none. */
    protected boolean tryReserve(ItemStack stack, List<ItemStack> extraCosts) {
        if (extraCosts.isEmpty()) return budget.tryReserve(stack);

        List<ItemStack> costs = new ArrayList<>(extraCosts.size() + 1);
        costs.add(stack);
        costs.addAll(extraCosts);
        return budget.tryReserve(costs);
    }

    /**
     * @return Copy or Palette BlockPicker based on the mode
     */
    public static BWBlockPicker create(World world, EntityPlayer player, BWMode mode, @Nullable ItemStack lookedAtBlock,
        InventoryBudget budget) {
        if (mode == BWMode.EXTRUDE) {
            return new ExtrudeBlockPicker(world, budget);
        }
        return new PaletteBlockPicker(world, budget, palette(mode, player, lookedAtBlock), ThreadLocalRandom.current());
    }

    private static List<ItemStack> palette(BWMode mode, EntityPlayer player, @Nullable ItemStack lookedAtBlock) {
        return switch (mode) {
            case OFFHAND_BLOCK -> Collections.singletonList(BWMode.offhand(player));
            case TROWEL -> hotbarBlocks(player);
            case CLICKED_BLOCK -> lookedAtBlock == null ? Collections.emptyList()
                : Collections.singletonList(lookedAtBlock);
            case EXTRUDE -> Collections.emptyList();
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

    /** Whether the stack could be placed into the target cell, the one in front of the clicked face. */
    static boolean canPlaceBlock(World world, ItemStack toPlace, MovingObjectPosition mop) {
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

    static class PaletteBlockPicker extends BWBlockPicker {

        private final Random random;
        private final List<ItemStack> palette;

        private final int[] scratchTemplate;
        private final int[] scratch;

        PaletteBlockPicker(World world, InventoryBudget budget, List<ItemStack> palette, Random random) {
            super(world, budget);
            this.palette = palette;
            this.random = random;
            this.scratch = new int[palette.size()];
            this.scratchTemplate = new int[palette.size()];
            for (int i = 0; i < palette.size(); i++) this.scratchTemplate[i] = i;
        }

        @Override
        public ItemStack pickBlock(MovingObjectPosition mop, @Nullable ItemStack ignored, List<ItemStack> extraCosts) {
            System.arraycopy(scratchTemplate, 0, scratch, 0, scratch.length);

            // try every block in the palette in a random order
            for (int i = 0; i < palette.size(); i++) {

                // shuffle one step
                int j = i + random.nextInt(palette.size() - i);
                int tmp = scratch[i];
                scratch[i] = scratch[j];
                scratch[j] = tmp;

                ItemStack stack = palette.get(scratch[i]);

                if (canPlaceBlock(world, stack, mop) && tryReserve(stack, extraCosts)) {
                    return stack;
                }
            }
            return null;
        }
    }

    static class ExtrudeBlockPicker extends BWBlockPicker {

        ExtrudeBlockPicker(World world, InventoryBudget budget) {
            super(world, budget);
        }

        @Override
        public ItemStack pickBlock(MovingObjectPosition mop, @Nullable ItemStack sourceBlock,
            List<ItemStack> extraCosts) {
            return sourceBlock != null && canPlaceBlock(world, sourceBlock, mop) && tryReserve(sourceBlock, extraCosts)
                ? sourceBlock
                : null;
        }
    }
}
