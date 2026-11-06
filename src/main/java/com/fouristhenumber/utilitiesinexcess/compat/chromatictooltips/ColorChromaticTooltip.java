package com.fouristhenumber.utilitiesinexcess.compat.chromatictooltips;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.utils.Color;
import com.fouristhenumber.utilitiesinexcess.utils.ColorTooltipDrawHelper;
import com.slprime.chromatictooltips.TooltipHandler;
import com.slprime.chromatictooltips.api.ITooltipComponent;
import com.slprime.chromatictooltips.api.TooltipContext;
import com.slprime.chromatictooltips.util.TooltipFontContext;

public class ColorChromaticTooltip implements ITooltipComponent {

    private final int color;
    private final String string;

    public ColorChromaticTooltip(int color) {
        this.color = color;
        this.string = " #" + Color.rgbToFullHexString(color);
    }

    @Override
    public int getWidth() {
        return getHeight() + TooltipFontContext.getStringWidth(string);
    }

    @Override
    public int getHeight() {
        return TooltipFontContext.getFontHeight() - getSpacing();
    }

    @Override
    public int getSpacing() {
        return TooltipFontContext.DEFAULT_SPACING;
    }

    @Override
    public void draw(int x, int y, int availableWidth, TooltipContext context) {
        GL11.glTranslated(x, y, 0);
        ColorTooltipDrawHelper.draw(getHeight(), string, color, TooltipFontContext.getColor(7));
        GL11.glTranslated(-x, -y, 0);
    }

    public static String makeTooltip(int color) {
        return TooltipHandler.getComponentId(new ColorChromaticTooltip(color));
    }

    @Override
    public int hashCode() {
        return color;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ColorChromaticTooltip && ((ColorChromaticTooltip) obj).color == color;
    }
}
