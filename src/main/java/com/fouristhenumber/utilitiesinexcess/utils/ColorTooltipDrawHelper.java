package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.utils.Color;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ColorTooltipDrawHelper {

    // Moved to a helper class with minimal imports
    // Used by both waila and chromatic tooltips compat
    public static void draw(int size, String string, int color, int textColor) {
        int previousTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.setColorRGBA(Color.getRed(color), Color.getGreen(color), Color.getBlue(color), 255);
        t.addVertex(0, size, 0);
        t.addVertex(size, size, 0);
        t.addVertex(size, 0, 0);
        t.addVertex(0, 0, 0);
        t.draw();
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(string, 8, 0, textColor);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTex);
    }
}
