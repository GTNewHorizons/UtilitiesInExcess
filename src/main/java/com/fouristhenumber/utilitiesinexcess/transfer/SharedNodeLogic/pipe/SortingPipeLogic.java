package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.pipe;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityItemTransferNode;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.Connection;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.NetworkLogic;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class SortingPipeLogic extends NetworkLogic
{

    public SortingPipeLogic(ITransferNetworkComponent host) {
        super(host);
    }

    @Override
    public boolean canConnectEnergy() {
        return true;
    }

    @Override
    public boolean canConnectFluid() {
        return true;
    }

    @Override
    public boolean canConnectItem() {
        return true;
    }

    @Override
    public Connection[] getValidExternalConnections(ForgeDirection fromDirection, IWalkingComponent walker)
    {
        // If we have an item Node we need to just return the valid connections
        if (walker instanceof TileEntityItemTransferNode itemNodeTE)
        {
            Connection[] ret = new Connection[6];
            for (int i = 0; i < 6; i++)
            {
                Connection conn = externalConnections[i];
                if (conn != null && conn.canConnectItem())
                {
                    if (conn.target() instanceof IInventory inventory)
                    {
                        if (containsItemOrEmpty(inventory, itemNodeTE.getStackInSlot(0)))
                        {
                            ret[i] = conn;
                            continue;
                        }
                    }
                }
                ret[i] = null;
            }
            return ret;
        }
        // Just return everything because there's no rules for a non-ItemTransferNode
        return externalConnections;
    }

    public static boolean containsItemOrEmpty(IInventory inv, ItemStack target)
    {
        boolean empty = true;
        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack slot = inv.getStackInSlot(i);

            if (slot == null)
            {
                continue;
            }
            empty = false;
            if (slot.getItem() == target.getItem() &&
                slot.getItemDamage() == target.getItemDamage())
            {
                if (ItemStack.areItemStackTagsEqual(slot, target))
                {
                    return true;
                }
            }
        }
        return empty;
    }
}
