package com.fouristhenumber.utilitiesinexcess.common.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeInvertedIngot extends ShapedRecipes {

    public RecipeInvertedIngot(int width, int height, ItemStack[] input, ItemStack output) {
        super(width, height, input, output);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        // Reject if not a vanilla workbench
        if (inv.eventHandler == null || inv.eventHandler.getClass() != net.minecraft.inventory.ContainerWorkbench.class) {
            return false;
        }

        return super.matches(inv, world);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack result = super.getCraftingResult(inv);
        if (result == null) return null;

        result = result.copy();

        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("ExplosionTimer", 200);
        result.setTagCompound(tag);

        return result;
    }
}
