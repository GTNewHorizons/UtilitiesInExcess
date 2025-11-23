package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

public class RenderUtils {

    public static void renderItemIcon(IIcon icon, double xStart, double yStart, double xEnd, double yEnd, double z,
        float nx, float ny, float nz) {
        if (icon == null) {
            return;
        }
        final Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.setNormal(nx, ny, nz);
        if (nz > 0.0F) {
            tess.addVertexWithUV(xStart, yStart, z, icon.getMinU(), icon.getMinV());
            tess.addVertexWithUV(xEnd, yStart, z, icon.getMaxU(), icon.getMinV());
            tess.addVertexWithUV(xEnd, yEnd, z, icon.getMaxU(), icon.getMaxV());
            tess.addVertexWithUV(xStart, yEnd, z, icon.getMinU(), icon.getMaxV());
        } else {
            tess.addVertexWithUV(xStart, yEnd, z, icon.getMinU(), icon.getMaxV());
            tess.addVertexWithUV(xEnd, yEnd, z, icon.getMaxU(), icon.getMaxV());
            tess.addVertexWithUV(xEnd, yStart, z, icon.getMaxU(), icon.getMinV());
            tess.addVertexWithUV(xStart, yStart, z, icon.getMinU(), icon.getMinV());
        }
        tess.draw();
    }
}
