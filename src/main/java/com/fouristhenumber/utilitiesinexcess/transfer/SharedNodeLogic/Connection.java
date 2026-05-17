package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic;

import com.github.bsideup.jabel.Desugar;
import net.minecraft.tileentity.TileEntity;

@Desugar
public record Connection(TileEntity target, int flags, int side) {}
