package com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockFilingCabinet;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.base.TileFilingCabinetBaseItem;
import com.fouristhenumber.utilitiesinexcess.config.blocks.FilingCabinetsConfig;

public class TileFilingCabinetBasic extends TileFilingCabinetBaseItem {

    public TileFilingCabinetBasic() {
        super(
            FilingCabinetsConfig.cabinetBasic,
            is -> ModItems.INVERTED_INGOT.newItemStack()
                .isItemEqual(is));
    }

    @Override
    public BlockFilingCabinet.Type getCabinetType() {
        return BlockFilingCabinet.Type.BASIC;
    }

    @Override
    public boolean isItemAllowed(ItemStack stack) {
        return stack.getMaxStackSize() != 1;
    }

    @Override
    public Predicate<ItemStack> extractMatcher(ItemStack stack) {
        return is -> stack.getItem()
            .equals(is.getItem());
    }

}
