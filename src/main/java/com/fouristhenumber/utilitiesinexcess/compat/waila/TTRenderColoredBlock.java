package com.fouristhenumber.utilitiesinexcess.compat.waila;

import java.awt.Dimension;

import com.cleanroommc.modularui.utils.Color;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColored;
import com.fouristhenumber.utilitiesinexcess.utils.ColorTooltipDrawHelper;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.OverlayConfig;

public class TTRenderColoredBlock implements IWailaTooltipRenderer {

    public static void register() {
        if (BlockColored.allowDyingBlocks()) {
            ModuleRegistrar.instance()
                .registerTooltipRenderer("waila.uie.coloredblock", new TTRenderColoredBlock());
        }
    }

    private String string;

    @Override
    public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
        string = " #" + Color.rgbToFullHexString(BlockColored.getRGBFromEIDMeta(accessor.getMetadata()));
        return new Dimension(8 + DisplayUtil.getDisplayWidth(string), 8);
    }

    @Override
    public void draw(String[] params, IWailaCommonAccessor accessor) {
        ColorTooltipDrawHelper
            .draw(8, string, BlockColored.getRGBFromEIDMeta(accessor.getMetadata()), OverlayConfig.fontcolor);
    }
}
