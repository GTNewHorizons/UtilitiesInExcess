package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.util;

import net.minecraft.entity.player.EntityPlayer;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.factory.PosGuiData;

public class PartGuiData extends PosGuiData {

    public final int partIndex;

    public PartGuiData(@NotNull EntityPlayer player, int x, int y, int z, int partIndex) {
        super(player, x, y, z);
        this.partIndex = partIndex;
    }
}
