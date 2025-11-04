package com.fouristhenumber.utilitiesinexcess.common.recipe;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.item.crafting.IRecipe;

public class RecipeInvertedIngot implements IRecipe {

    private final ItemStack output;
    private final ItemStack diamond = new ItemStack(Items.diamond, 1);
    private final ItemStack iron = new ItemStack(Items.iron_ingot, 1);
    private final ItemStack sigil = ModItems.INVERSION_SIGIL_ACTIVE.newItemStack(1);

    public RecipeInvertedIngot(ItemStack output) {
        this.output = output;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        // Reject if not a vanilla workbench
        if (!(inv.getInventoryName().equals("container.crafting"))) {
            return false;
        }

        for (int col = 0; col < 3; col++) {
            if (columnMatches(inv, col) && othersEmpty(inv, col)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting p_77572_1_) {
        return output;
    }

    private boolean columnMatches(InventoryCrafting inv, int col) {
        return stackMatches(inv.getStackInRowAndColumn(col, 0), iron)
            && stackMatches(inv.getStackInRowAndColumn(col, 1), sigil)
            && stackMatches(inv.getStackInRowAndColumn(col, 2), diamond);
    }

    private boolean othersEmpty(InventoryCrafting inv, int usedCol) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (col == usedCol) continue;
                if (inv.getStackInRowAndColumn(col, row) != null) return false;
            }
        }
        return true;
    }

    private boolean stackMatches(ItemStack slot, ItemStack requirement) {
        if (slot == null || requirement == null) return false;
        return slot.getItem() == requirement.getItem() &&
            slot.getItemDamage() == requirement.getItemDamage();
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output.copy();
    }
}
