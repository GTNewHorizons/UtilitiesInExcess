package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface ITransferNetworkComponent {

    boolean canConnectFrom(ForgeDirection side);

    boolean canConnectTo(World world, int x, int y, int z, int side);
}
