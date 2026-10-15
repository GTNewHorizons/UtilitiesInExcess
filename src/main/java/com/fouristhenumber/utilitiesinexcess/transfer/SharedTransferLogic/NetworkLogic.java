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
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

public abstract class NetworkLogic
{
    protected ITransferNetworkComponent host;

    public NetworkLogic(ITransferNetworkComponent host)
    {
        this.host = host;
    }

    // TODO Move this somewhere else?
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

    // Doesn't need to be abstract because some transfer TE's don't have contents, but we do want it here
    // because then it's easier to access from parts.
    public void dropContents(World world, int x, int y, int z) {}

    // When a part is converted the break block call is made. We need to clear the inventory before that happens.
    public void invalidateInventory() {}

    // This is used to transfer the TE logic to a part as the host. Also note that this doesn't need to mark
    // the host dirty since FMP does this for us.
    public abstract void initFromOtherLogic(NetworkLogic logic);

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
