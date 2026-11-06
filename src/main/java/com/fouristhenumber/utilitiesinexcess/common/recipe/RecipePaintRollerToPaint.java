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
            BlockColored bc = ((BlockColored) ((ItemBlock) out.getItem()).field_150939_a);
            out = new ItemStack(
                bc.getBase()
                    .getBlock(),
                out.stackSize,
                bc.getBase()
                    .getMeta());
        } else {
            // TODO revert if https://github.com/GTMEGA/EndlessIDs/issues/291 is solved
            out.setItemDamage(BlockColored.getEIDMetaFromRGB(ItemPaintRoller.getColorFromStack(roller)));
            // if (((BlockColored) ((ItemBlock) out.getItem()).field_150939_a).usesExtraBit()) {
            // out.setItemDamage(
            // BlockColored.getEIDMetaFromRGBWithExtraBit(ItemPaintRoller.getColorFromStack(roller)));
            // } else {
            // out.setItemDamage(BlockColored.getEIDMetaFromRGB(ItemPaintRoller.getColorFromStack(roller)));
            // }
        }
        return out;
    }
}
