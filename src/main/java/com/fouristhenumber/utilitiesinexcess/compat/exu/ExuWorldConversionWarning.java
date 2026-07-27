package com.fouristhenumber.utilitiesinexcess.compat.exu;

import net.minecraft.util.StatCollector;

import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.config.OtherConfig;
import com.gtnewhorizon.gtnhlib.api.gui.GuiConfirmationWCW;
import com.gtnewhorizon.gtnhlib.api.gui.IWorldConversionWarning;

import cpw.mods.fml.common.StartupQuery;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExuWorldConversionWarning implements IWorldConversionWarning {

    public static boolean show = false;

    @Override
    public boolean shouldShow() {
        return show;
    }

    @Override
    public String getServerMessage() {
        return doWorldConversion() ? StatCollector.translateToLocal("uie.world_conversion.warning_enabled.server")
            : StatCollector.translateToLocal("uie.world_conversion.warning_disabled.server");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getClientMessage() {
        return doWorldConversion() ? StatCollector.translateToLocal("uie.world_conversion.warning_enabled.client")
            : StatCollector.translateToLocal("uie.world_conversion.warning_disabled.client");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiConfirmationWCW getGui(StartupQuery startupQuery) {
        return new ExtendedConfirmationGui(startupQuery);
    }

    public static boolean doWorldConversion() {
        return OtherConfig.enableWorldConversion && !Mods.ExtraUtilities.isLoaded() && Mods.Postea.isLoaded();
    }

    public static void onMissingMapping(FMLMissingMappingsEvent event) {
        if (!OtherConfig.enableWorldConversionWarning) return;
        if (Mods.ExtraUtilities.isLoaded()) return;
        if (ExuWorldConversionWarning.show) return;

        for (FMLMissingMappingsEvent.MissingMapping mapping : event.getAll()) {
            if (mapping.name.startsWith("ExtraUtilities")) {
                ExuWorldConversionWarning.show = true;
                return;
            }
        }
    }
}
