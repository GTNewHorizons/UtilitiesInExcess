package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.cleanroommc.modularui.screen.ModularScreen;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.ItemTransferNodeLogic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;


public class TileEntityItemTransferNode extends TileEntityTransferNodeBase<ItemTransferNodeLogic>
    implements IGuiHolder<PosGuiData>, IWalkingComponent<ItemStack>
{
    public TileEntityItemTransferNode()
    {
        logic = new ItemTransferNodeLogic(this);
    }

    @Override
    public void updateEntity()
    {
        this.logic.updateEntity();
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
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        return logic.buildUI(data, syncManager, settings);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return logic.createScreen(data, mainPanel);
    }

    @Override
    public void updateSource()
    {
        logic.updateSourceInventory();
    }

    @Override
    public ItemStack getWalkingObject()
    {
        return logic.getStackInSlot(0);
    }

    @Override
    protected ItemTransferNodeLogic createLogic() {
        return new ItemTransferNodeLogic(this);
    }
}
