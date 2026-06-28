package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.pipe;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.Connection;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.DefaultNetworkLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class ModSortingPipeLogic extends DefaultNetworkLogic
{

    public ModSortingPipeLogic(ITransferNetworkComponent host) {
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

    // Only valid external connection is across from the fromDirection
    @Override
    public Connection[] getValidExternalConnections(ForgeDirection fromDirection, IWalkingComponent walker)
    {
        {
            // If we have an item Node we need to just return the valid connections
            Object walkingObject = walker.getWalkingObject();
            if (walkingObject instanceof ItemStack stack)
            {
                Connection[] ret = new Connection[6];
                for (int i = 0; i < 6; i++)
                {
                    Connection conn = externalConnections[i];
                    if (conn != null && conn.canConnectItem())
                    {
                        if (conn.target() instanceof IInventory inventory)
                        {
                            if (containsModItemOrEmpty(inventory, stack))
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
    }

    private boolean containsModItemOrEmpty(IInventory inv, ItemStack target)
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
                GameRegistry.UniqueIdentifier idA = GameRegistry.findUniqueIdentifierFor(target.getItem());
                GameRegistry.UniqueIdentifier idB = GameRegistry.findUniqueIdentifierFor(slot.getItem());

                if (idA == null || idB == null)
                {
                    return false;
                }

                return idA.modId.equals(idB.modId);
            }
        }
        return empty;
    }
}
