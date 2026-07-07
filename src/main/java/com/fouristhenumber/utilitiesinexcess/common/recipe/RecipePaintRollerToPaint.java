package com.fouristhenumber.utilitiesinexcess.common.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColored;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemPaintRoller;

public class RecipePaintRollerToPaint extends ShapedRecipes {

    public RecipePaintRollerToPaint(int width, int height, ItemStack[] input, ItemStack output) {
        super(width, height, input, output);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting crafting) {
        ItemStack out = super.getCraftingResult(crafting);
        ItemStack roller = crafting.getStackInSlot(4);
        boolean paintStripper = ItemPaintRoller.getPaintStripperFromStack(roller);

        if (paintStripper) {
            out = new ItemStack(
                ((BlockColored) ((ItemBlock) out.getItem()).field_150939_a).getBase(),
                out.stackSize,
                0);
        } else {
            out.setItemDamage(BlockColored.getEIDMetaFromRGB(ItemPaintRoller.getColorFromStack(roller)));
        }
        return out;
    }
}
