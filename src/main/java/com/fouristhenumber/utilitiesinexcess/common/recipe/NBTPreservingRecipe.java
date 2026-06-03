package com.fouristhenumber.utilitiesinexcess.common.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class NBTPreservingRecipe extends ShapelessRecipes
{

    // Which item in the input items to take the NBT from
    int slot;

    public NBTPreservingRecipe(ItemStack outputItem, List<ItemStack> inputItems, int slot)
    {
        super(outputItem, inputItems);
        this.slot = slot;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack result = super.getCraftingResult(inv);

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);

            if (stack != null && stack.isItemEqual(recipeItems.get(slot)) && stack.hasTagCompound()) {
                NBTTagCompound incomingTag = stack.getTagCompound();

                for (String key : incomingTag.func_150296_c())
                {
                    result.stackTagCompound.setTag(key, incomingTag.getTag(key));
                }
                break;
            }
        }

        return result;
    }
}
