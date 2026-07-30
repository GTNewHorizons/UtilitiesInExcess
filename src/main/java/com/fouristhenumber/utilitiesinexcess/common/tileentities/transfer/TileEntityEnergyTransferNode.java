package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.EnergyTransferNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.ItemRetrievalNodeLogic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityEnergyTransferNode extends TileEntityTransferNodeBase<EnergyTransferNodeLogic>
    implements IGuiHolder<PosGuiData>, IWalkingComponent<Integer>
{

    public TileEntityEnergyTransferNode() {}

    @Override
    public void updateEntity()
    {
        this.logic.updateEntity();
    }

    @Override
    public void validate()
    {
        if (worldObj != null && !init)
        {
            if (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 1)
            {
                this.logic = new EnergyTransferNodeLogic(this, true);
            }
            else
            {
                this.logic = new EnergyTransferNodeLogic(this, false);
            }
            init = true;
        }
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
    public Integer getWalkingObject() {
        return logic.containedEnergy;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        return logic.buildUI(data, syncManager, settings);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return logic.createScreen(data, mainPanel);
    }
}
