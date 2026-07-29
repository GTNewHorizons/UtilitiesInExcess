package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

// Class that is implemented by
public interface INodeLogicHost extends ITransferNetworkComponent
{
    World getWorld();

    ForgeDirection getFacing();
}
