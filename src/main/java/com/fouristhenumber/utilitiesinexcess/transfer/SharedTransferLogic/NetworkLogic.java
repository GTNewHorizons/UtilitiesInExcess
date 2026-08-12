package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import cofh.api.energy.IEnergyHandler;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

public abstract class NetworkLogic<T extends ITransferNetworkComponent>
{
    protected T host;

    public NetworkLogic(T host)
    {
        this.host = host;
    }

    public static boolean isValidConnectable(IBlockAccess world, int x, int y, int z, ForgeDirection dir)
    {
        boolean connects;
        IConnectable connectable = IConnectable.getConnectable(world, x, y, z);

        if (connectable != null)
        {
            connects = connectable.canConnectInDirection(world, x, y, z, dir.getOpposite());
        }
        else
        {
            TileEntity te = world.getTileEntity(x, y, z);
            connects = te instanceof IFluidHandler || te instanceof IInventory || te instanceof IEnergyHandler;
        }
        return connects;
    }
}
