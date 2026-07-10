package com.fouristhenumber.utilitiesinexcess.compat.waila;

import java.awt.Dimension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.utils.Color;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColored;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.OverlayConfig;

public class TTRenderColoredBlock implements IWailaTooltipRenderer {

    public static void register() {
        ModuleRegistrar.instance()
            .registerTooltipRenderer("waila.uie.coloredblock", new TTRenderColoredBlock());
    }

    private String string;

    @Override
    public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
        string = " #" + Color.rgbToFullHexString(BlockColored.getRGBFromEIDMeta(accessor.getMetadata()));
        return new Dimension(DisplayUtil.getDisplayWidth(string), 8);
    }

    @Override
    public void draw(String[] params, IWailaCommonAccessor accessor) {
        int previousTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        int size = 8;
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        int color = BlockColored.getRGBFromEIDMeta(accessor.getMetadata());
        t.setColorRGBA(Color.getRed(color), Color.getGreen(color), Color.getBlue(color), 255);
        t.addVertex(0, size, 0);
        t.addVertex(size, size, 0);
        t.addVertex(size, 0, 0);
        t.addVertex(0, 0, 0);
        t.draw();
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(string, 8, 0, OverlayConfig.fontcolor);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTex);
    }
}
