package com.fouristhenumber.utilitiesinexcess.utils.mui;

import java.util.function.Predicate;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.utils.fluid.FluidStackTank;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;

public class FilteredFluidSlotSyncHandler extends FluidSlotSyncHandler {

    private final Predicate<Fluid> filter;

    public FilteredFluidSlotSyncHandler(FluidStackTank tank, Predicate<Fluid> filter) {
        super(tank);
        this.filter = filter;
    }

    @Override
    protected void fillFluid(@NotNull FluidStack heldFluid, boolean processFullStack) {
        if (!filter.test(heldFluid.getFluid())) return;
        super.fillFluid(heldFluid, processFullStack);
    }
}
