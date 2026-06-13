package com.fouristhenumber.utilitiesinexcess.common.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;

import java.util.List;
import java.util.Objects;

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

            if (stack != null && stack.isItemEqual(recipeItems.get(slot)) && stack.hasTagCompound())
            {
                NBTTagCompound incomingTag = stack.getTagCompound();

                for (String key : incomingTag.func_150296_c())
                {
                    // Merge mode tags
                    if (Objects.equals(key, "Mode"))
                    {
                        result.stackTagCompound.setTag(key, new NBTTagInt(result.getTagCompound().getInteger(key) | incomingTag.getInteger(key)));
                    }
                    else
                    {
                        result.stackTagCompound.setTag(key, incomingTag.getTag(key));
                    }
                }
                break;
            }
        }

        return result;
    }
}
