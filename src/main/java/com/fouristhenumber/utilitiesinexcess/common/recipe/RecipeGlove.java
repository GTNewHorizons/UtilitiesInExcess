package com.fouristhenumber.utilitiesinexcess.common.recipe;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;

public class RecipeGlove extends ShapedRecipes {

    public RecipeGlove(int p_i1917_1_, int p_i1917_2_, ItemStack[] p_i1917_3_, ItemStack p_i1917_4_) {
        super(p_i1917_1_, p_i1917_2_, p_i1917_3_, p_i1917_4_);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting crafting) {
        boolean foundFirst = false;
        int meta = 0;
        for (int i = 0; i < crafting.getSizeInventory(); i++) {
            ItemStack stack = crafting.getStackInSlot(i);
            if (stack != null && stack.getItem() == Item.getItemFromBlock(Blocks.wool)) {
                if (foundFirst) {
                    meta += stack.getItemDamage();
                    break;
                } else {
                    meta += stack.getItemDamage() * 16;
                    foundFirst = true;
                }
            }
        }
        ItemStack out = super.getCraftingResult(crafting);
        out.setItemDamage(meta);
        return out;
    }
}
