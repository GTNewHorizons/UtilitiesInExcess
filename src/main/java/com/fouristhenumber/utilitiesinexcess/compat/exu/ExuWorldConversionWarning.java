package com.fouristhenumber.utilitiesinexcess.compat.exu;

import net.minecraft.util.StatCollector;

import com.gtnewhorizon.gtnhlib.api.gui.GuiConfirmationWCW;
import com.gtnewhorizon.gtnhlib.api.gui.WorldConversionWarning;

import cpw.mods.fml.common.StartupQuery;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExuWorldConversionWarning extends WorldConversionWarning {

    public static boolean SHOW = false;

    @Override
    public boolean shouldShow() {
        return SHOW;
    }

    @Override
    public String getServerMessage() {
        return StatCollector.translateToLocal("uie.world_conversion.warning.server");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getClientMessage() {
        return StatCollector.translateToLocal("uie.world_conversion.warning.client");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiConfirmationWCW getGui(StartupQuery startupQuery) {
         return new ExtendedConfirmationGui(startupQuery);
    }
}
