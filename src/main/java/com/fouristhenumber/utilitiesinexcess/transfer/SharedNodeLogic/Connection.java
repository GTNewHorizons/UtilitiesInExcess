package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic;

import com.github.bsideup.jabel.Desugar;
import net.minecraft.tileentity.TileEntity;

@Desugar
public record Connection(TileEntity target, int flags, int side)
{
    public boolean canConnectItem()
    {
        return (flags & 1) != 0;
    }

    public boolean canConnectEnergy()
    {
        return false;
    }

    public boolean canConnectFluid()
    {
        return false;
    }
}


