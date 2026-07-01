package com.fouristhenumber.utilitiesinexcess.compat.waila;

import java.awt.Dimension;

import net.minecraft.client.gui.Gui;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaVariableWidthTooltipRenderer;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.OverlayConfig;

// Based on Waila's new progress bar renderer by super
public class TTRenderUIETimeLeftBar implements IWailaVariableWidthTooltipRenderer {

    int maxStringW;

    public static void register() {
        ModuleRegistrar.instance()
            .registerTooltipRenderer("waila.uie.progress", new TTRenderUIETimeLeftBar()); // Registration code would go
                                                                                          // here if needed
    }

    @Override
    public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
        return new Dimension(DisplayUtil.getDisplayWidth(params[2]), 12);
    }

    @Override
    public void draw(String[] params, IWailaCommonAccessor accessor) {
        drawThickBeveledBox(0, 0, maxStringW, 12, 1, 0xFF505050, 0xFF505050, -1);
        long progresstime = Long.parseLong(params[0]);
        long maxProgresstime = Long.parseLong(params[1]);
        String text = params[2];
        if (progresstime > maxProgresstime) maxProgresstime = progresstime;
        int progress = (int) ((maxStringW - 1) * ((double) progresstime / maxProgresstime));
        for (int xx = 1; xx < progress; xx++) {
            int color = (xx & 1) == 0 ? 0xFF176087 : 0xFF1D84B5;
            drawVerticalLine(xx, 1, 12 - 1, color);
        }
        DisplayUtil.drawString(text, 2, 2, OverlayConfig.fontcolor, true);
    }

    public static void drawThickBeveledBox(int x1, int y1, int x2, int y2, int thickness, int topleftcolor,
        int botrightcolor, int fillcolor) {
        if (fillcolor != -1) {
            Gui.drawRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillcolor);
        }
        Gui.drawRect(x1, y1, x2 - 1, y1 + thickness, topleftcolor);
        Gui.drawRect(x1, y1, x1 + thickness, y2 - 1, topleftcolor);
        Gui.drawRect(x2 - thickness, y1, x2, y2 - 1, botrightcolor);
        Gui.drawRect(x1, y2 - thickness, x2, y2, botrightcolor);
    }

    public static void drawVerticalLine(int x1, int y1, int y2, int color) {
        Gui.drawRect(x1, y1, x1 + 1, y2, color);
    }

    @Override
    public void setMaxLineWidth(int width) {
        maxStringW = width + 2;
    }

    @Override
    public int getMaxLineWidth() {
        return maxStringW;
    }
}
