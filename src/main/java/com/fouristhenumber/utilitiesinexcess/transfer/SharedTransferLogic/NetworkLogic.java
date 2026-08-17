package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import cofh.api.energy.IEnergyHandler;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

public abstract class NetworkLogic<T extends ITransferNetworkComponent>
{
    protected T host;

    public NetworkLogic(T host)
    {
        this.host = host;
    }

    // TODO Move this somewhere else.
    public static boolean isValidConnectable(IBlockAccess world, int x, int y, int z, ForgeDirection dir)
    {
        boolean connects;
        List<IConnectable> connectables = IConnectable.getConnectables(world, x, y, z);

        if (!connectables.isEmpty())
        {
            connects = IConnectable.canConnectInDirection(connectables, world, x, y, z, dir.getOpposite());
        }
        else
        {
            TileEntity te = world.getTileEntity(x, y, z);
            connects = te instanceof IFluidHandler || te instanceof IInventory || te instanceof IEnergyHandler;
        }
        return connects;
    }

    public abstract void writeToNBT(NBTTagCompound nbt);

    public abstract void readFromNBT(NBTTagCompound nbt);

    public abstract void writeDesc(MCDataOutput output);

    public abstract void readDesc(MCDataInput input);

    public abstract ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings);

    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(UtilitiesInExcess.MODID, mainPanel);
    }
}
