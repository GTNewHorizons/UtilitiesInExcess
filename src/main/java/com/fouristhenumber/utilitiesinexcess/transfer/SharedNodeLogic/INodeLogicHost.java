package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

// Class that is implemented by
public interface INodeLogicHost extends ISidedInventory, ITransferNetworkComponent
{
    World getWorld();

    ForgeDirection getFacing();
}
