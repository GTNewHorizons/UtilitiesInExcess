package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.fluid.FluidStackTank;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.fouristhenumber.utilitiesinexcess.utils.mui.FilteredFluidSlotSyncHandler;

public class TileEntityLavaGenerator extends TileEntityBaseGenerator implements IFluidHandler {

    protected FluidStack fluid;
    protected FluidStackTank fluidTank = new FluidStackTank(() -> fluid, fluidStack -> fluid = fluidStack, 4000);

    public TileEntityLavaGenerator() {
        super(100_000, 5000);
    }

    @Override
    protected boolean consumeFuel() {
        if (fluid != null && energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()
            && fluid.amount >= 50) {
            currentFuelBurnTime = 50;
            currentRFPerTick = 40;
            fluidTank.drain(50, true);
            return true;
        }
        return false;
    }

    @Override
    protected String getGUIName() {
        return "tile.lava_generator.name";
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource == null || resource.getFluid() != FluidRegistry.LAVA) return 0;
        return fluidTank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid == FluidRegistry.LAVA;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { fluidTank.getInfo() };
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("Fluid")) {
            fluid = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("Fluid"));
        } else {
            fluid = null;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (fluid != null) {
            NBTTagCompound fluidTag = new NBTTagCompound();
            fluid.writeToNBT(fluidTag);
            tag.setTag("Fluid", fluidTag);
        }
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = super.buildUI(data, syncManager, settings);

        FilteredFluidSlotSyncHandler fluidSync = new FilteredFluidSlotSyncHandler(
            fluidTank,
            input -> input == FluidRegistry.LAVA);

        panel.child(
            new FluidSlot().syncHandler(fluidSync)
                .pos(79, 34));

        return panel;
    }
}
