package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockNodeBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.BaseNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.INodeLogicHost;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityTransferNodeBase<T extends BaseNodeLogic<? extends ITransferNetworkComponent, V>, V> extends TileEntityNetworkComponentBase<T>
    implements INodeLogicHost, IGuiHolder<PosGuiData>, IWalkingComponent<V>
{
    @Override
    public void updateEntity()
    {
        this.getLogic().updateEntity();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        this.getLogic().writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.getLogic().readFromNBT(nbt);
    }

    @Override
    public V getWalkingObject()
    {
        return getLogic().getWalkingObject();
    }

    @Override
    public ModularPanel buildUI(PosGuiData posGuiData, PanelSyncManager panelSyncManager, UISettings uiSettings) {
        return getLogic().buildUI(posGuiData, panelSyncManager, uiSettings);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return getLogic().createScreen(data, mainPanel);
    }

    @Override
    public ForgeDirection getFacing()
    {
        return BlockNodeBase.getFacing(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
    }
}
