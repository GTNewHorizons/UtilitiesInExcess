package com.fouristhenumber.utilitiesinexcess.utils;

import static com.fouristhenumber.utilitiesinexcess.utils.BuildersWandUtils.canPlaceBlock;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.util.ItemUtil;

public abstract class BuildersBlockPicker {

    /**
     * pick a suitable block to be placed, including whether its already in the inventory
     *
     * @param mop         the source block on which the new one will be placed
     * @param sourceBlock the stack the source block would drop when picked, already resolved by the caller
     * @return block to be placed or null
     */
    public abstract ItemStack pickBlock(MovingObjectPosition mop, ItemStack sourceBlock);

    /**
     * pick a suitable block to be placed, including whether its already in the inventory
     *
     * @param mop         the source block on which the new one will be placed
     * @param sourceBlock the stack the source block would drop when picked, already resolved by the caller
     * @return true if suitable block was picked
     */
    public abstract boolean pickSomeBlock(MovingObjectPosition mop, ItemStack sourceBlock);

    public static BuildersBlockPicker create(World world, EntityPlayer player, BuildersBlockSelectionFilter filter,
        BuildersMaterialBudget budget) {
        if (filter.ignoresVariants()) {
            return new CopyBlockPicker(world, budget);
        }
        return new PaletteBlockPicker(
            world,
            player.inventory,
            filter.generatePalette(player),
            budget,
            ThreadLocalRandom.current());
    }

    public static class PaletteBlockPicker extends BuildersBlockPicker {

        private final BuildersMaterialBudget budget;
        private final Random random;
        private final List<ItemStack> palette;
        private final World world;

        private int remaining;

        private final int[] scratchTemplate;
        private final int[] scratch;

        public PaletteBlockPicker(World world, InventoryPlayer inventory, List<ItemStack> palette,
            BuildersMaterialBudget budget, Random random) {
            this.world = world;
            this.palette = palette;
            this.budget = budget;
            this.random = random;
            this.remaining = 0;
            this.scratch = new int[palette.size()];
            this.scratchTemplate = new int[palette.size()];
            for (int i = 0; i < palette.size(); i++) this.scratchTemplate[i] = i;

            for (ItemStack stack : inventory.mainInventory) {
                for (ItemStack entry : palette) {
                    if (ItemUtil.areStacksEqual(entry, stack)) {
                        remaining += stack.stackSize;
                        break;
                    }
                }
            }
        }

        @Override
        public ItemStack pickBlock(MovingObjectPosition mop, ItemStack ignored) {
            if (remaining <= 0) return null;

            // prepare shuffling
            System.arraycopy(scratchTemplate, 0, scratch, 0, scratch.length);

            // try every block in the palette in a random order
            for (int i = 0; i < palette.size(); i++) {

                // shuffle one step
                int j = i + random.nextInt(palette.size() - i);
                int tmp = scratch[i];
                scratch[i] = scratch[j];
                scratch[j] = tmp;

                // pick one block from shuffled palette
                ItemStack stack = palette.get(scratch[i]);

                if (canPlaceBlock(world, stack, mop) && budget.tryReserve(stack)) {
                    remaining--;
                    return stack;
                }
            }
            return null;
        }

        @Override
        public boolean pickSomeBlock(MovingObjectPosition mop, ItemStack sourceBlock) {
            if (remaining <= 0) return false;

            for (ItemStack stack : palette) {
                if (canPlaceBlock(world, stack, mop)) {
                    remaining--;
                    return true;
                }
            }
            return false;
        }
    }

    public static class CopyBlockPicker extends BuildersBlockPicker {

        private final BuildersMaterialBudget budget;
        private final World world;

        public CopyBlockPicker(World world, BuildersMaterialBudget budget) {
            this.budget = budget;
            this.world = world;
        }

        @Override
        public ItemStack pickBlock(MovingObjectPosition mop, ItemStack sourceBlock) {
            return sourceBlock != null && canPlaceBlock(world, sourceBlock, mop) && budget.tryReserve(sourceBlock)
                ? sourceBlock
                : null;
        }

        @Override
        public boolean pickSomeBlock(MovingObjectPosition mop, ItemStack sourceBlock) {
            return pickBlock(mop, sourceBlock) != null;
        }
    }
}
