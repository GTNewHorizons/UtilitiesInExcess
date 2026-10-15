package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;

public interface INodeLogicHost extends ITransferNetworkComponent {

    World getWorld();

    ForgeDirection getFacing();
}
