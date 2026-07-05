package com.fouristhenumber.utilitiesinexcess.compat.exu;

import net.minecraft.util.StatCollector;

import com.fouristhenumber.utilitiesinexcess.config.OtherConfig;
import com.gtnewhorizon.gtnhlib.api.gui.GuiConfirmationWCW;
import com.gtnewhorizon.gtnhlib.api.gui.IWorldConversionWarning;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.StartupQuery;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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

    @SuppressWarnings("unused")
    @EventBusSubscriber
    public static class Events {

        @EventBusSubscriber.Condition
        public static boolean shouldSubscribe() {
            return OtherConfig.enableWorldConversion;
        }

        @SubscribeEvent
        public void onMissingMapping(FMLMissingMappingsEvent event) {
            if (ExuWorldConversionWarning.show) {
                return;
            }

            for (FMLMissingMappingsEvent.MissingMapping mapping : event.getAll()) {
                if (mapping.name.startsWith("ExtraUtilities")) {
                    ExuWorldConversionWarning.show = true;
                    return;
                }
            }
        }
    }
}
