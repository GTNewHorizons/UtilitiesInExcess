package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.Connection;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class ItemTargetResolver implements TargetResolver<IInventory> {

    @Override
    public List<Target<IInventory>> getValidTargets(ITransferNetworkComponent from, ForgeDirection fromDir)
    {
        Connection[] conns = from.getValidExternalNeighbors(fromDir);

        List<Target<IInventory>> validTargets = new ArrayList<>();
        for (Connection conn : conns)
        {
            if (conn != null && (conn.flags() & 1) != 0)
            {
                validTargets.add(new Target<>((IInventory) conn.target(), conn.side()));
            }
        }
        return validTargets;
    }
}
