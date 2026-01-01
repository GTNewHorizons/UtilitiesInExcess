package com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockFilingCabinet;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.base.TileFilingCabinetBaseItem;
import com.fouristhenumber.utilitiesinexcess.config.blocks.FilingCabinetsConfig;

public class TileFilingCabinetAdvanced extends TileFilingCabinetBaseItem {

    public TileFilingCabinetAdvanced() {
        super(
            FilingCabinetsConfig.cabinetAdvanced,
            is -> ModItems.INVERTED_INGOT.newItemStack()
                .isItemEqual(is));
    }

    @Override
    public BlockFilingCabinet.Type getCabinetType() {
        return BlockFilingCabinet.Type.ADVANCED;
    }

    @Override
    public boolean isItemAllowed(ItemStack stack) {
        return stack.getMaxStackSize() == 1;
    }

    @Override
    public Predicate<ItemStack> extractMatcher(ItemStack stack) {
        return is -> true;
    }

}
