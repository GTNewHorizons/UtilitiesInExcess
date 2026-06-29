package com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import com.fouristhenumber.utilitiesinexcess.config.blocks.FilingCabinetsConfig;
import com.fouristhenumber.utilitiesinexcess.config.blocks.FilingCabinetsConfig.CabinetConfig;

public enum CabinetTier {

    BASIC(FilingCabinetsConfig.cabinetBasic, stack -> stack.getMaxStackSize() != 1, true),
    ADVANCED(FilingCabinetsConfig.cabinetAdvanced, stack -> stack.getMaxStackSize() == 1, false),
    ELITE(FilingCabinetsConfig.cabinetElite, stack -> true, false);

    private static final CabinetTier[] VALUES = values();

    private final CabinetConfig config;
    private final Predicate<ItemStack> accepts;
    private final boolean lockToFirstItem;

    CabinetTier(CabinetConfig config, Predicate<ItemStack> accepts, boolean lockToFirstItem) {
        this.config = config;
        this.accepts = accepts;
        this.lockToFirstItem = lockToFirstItem;
    }

    public static CabinetTier from(int meta) {
        return (meta >= 0 && meta < VALUES.length) ? VALUES[meta] : BASIC;
    }

    public CabinetConfig config() {
        return config;
    }

    public boolean isItemAllowed(ItemStack stack) {
        return accepts.test(stack);
    }

    public Predicate<ItemStack> extractMatcher(ItemStack first) {
        return lockToFirstItem ? is -> first.getItem()
            .equals(is.getItem()) : is -> true;
    }

    public int meta() {
        return ordinal();
    }

    public String getName() {
        return name().toLowerCase();
    }

    public boolean isEnabled() {
        return config.enable;
    }

    public TileEntity createTile() {
        TileEntityFilingCabinet tile = new TileEntityFilingCabinet();
        tile.setTier(this);
        return tile;
    }
}