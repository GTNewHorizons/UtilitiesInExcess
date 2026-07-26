package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.FluidTransferNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityFluidTransferNode extends TileEntityTransferNodeBase<FluidTransferNodeLogic>
    implements IGuiHolder<PosGuiData>, IWalkingComponent<FluidStack>
{
    public TileEntityFluidTransferNode()
    {
    }

    @Override
    public void updateEntity()
    {
        this.logic.updateEntity();
    }

    @Override
    public ModularPanel buildUI(PosGuiData posGuiData, PanelSyncManager panelSyncManager, UISettings uiSettings) {
        return logic.buildUI(posGuiData, panelSyncManager, uiSettings);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        this.logic.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.logic.readFromNBT(nbt);
    }

    @Override
    public void updateSource()
    {
        logic.updateSourceTank();
    }

    @Override
    public FluidStack getWalkingObject() {
        return logic.buffer.getFluid();
    }

    @Override
    protected FluidTransferNodeLogic createLogic() {
        return new FluidTransferNodeLogic(this);
    }
}
