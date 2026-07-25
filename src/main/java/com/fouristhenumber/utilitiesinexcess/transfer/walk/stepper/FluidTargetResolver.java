package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.Connection;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class FluidTargetResolver implements TargetResolver<IFluidHandler>
{
    @Override
    public List<Target<IFluidHandler>> getValidTargets(ITransferNetworkComponent from, IWalkingComponent<?> walking, ForgeDirection fromDir)
    {
        Connection[] conns = from.getValidExternalNeighbors(fromDir, walking);

        List<Target<IFluidHandler>> validTargets = new ArrayList<>();
        for (Connection conn : conns)
        {
            if (conn != null && conn.canConnectFluid())
            {
                validTargets.add(new Target<>((IFluidHandler) conn.target(), conn.side()));
            }
        }
        return validTargets;
    }
}
