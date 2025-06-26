package com.fouristhenumber.utilitiesinexcess.render;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

public class ScaledCubeFaceIcon implements IIcon {

    IIcon baseIcon;

    // These represent the bounds of the face when viewed from straight on
    double verticalMin;
    double verticalMax;
    double horizontalMin;
    double horizontalMax;

    public ScaledCubeFaceIcon(IIcon icon, Block block, boolean baseFace) {
        baseIcon = icon;

        if (baseFace) {
            // Top and bottom faces of the cube
            verticalMin = block.getBlockBoundsMinX();
            verticalMax = block.getBlockBoundsMaxX();
            horizontalMin = block.getBlockBoundsMinZ();
            horizontalMax = block.getBlockBoundsMaxZ();
        } else {
            // Lateral faces of the cube
            verticalMax = 1 - block.getBlockBoundsMinY();
            verticalMin = 1 - block.getBlockBoundsMaxY();
            horizontalMin = block.getBlockBoundsMinX();
            horizontalMax = block.getBlockBoundsMaxX();
        }
    }

    @Override
    public int getIconWidth() {
        return baseIcon.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return baseIcon.getIconHeight();
    }

    @Override
    public float getMinU() {
        return baseIcon.getMinU();
    }

    @Override
    public float getMaxU() {
        return baseIcon.getMaxU();
    }

    @Override
    public float getInterpolatedU(double coordinate) {
        double clamped = MathHelper.clamp_double(coordinate / 16, horizontalMin, horizontalMax);
        return (float) (((clamped - horizontalMin) / (horizontalMax - horizontalMin)) * (getMaxU() - getMinU())) + getMinU();
    }

    @Override
    public float getMinV() {
        return baseIcon.getMinV();
    }

    @Override
    public float getMaxV() {
        return baseIcon.getMaxV();
    }

    @Override
    public float getInterpolatedV(double coordinate) {
        double clamped = MathHelper.clamp_double(coordinate / 16, verticalMin, verticalMax);
        return (float) (((clamped - verticalMin) / (verticalMax - verticalMin)) * (getMaxV() - getMinV())) + getMinV();
    }

    @Override
    public String getIconName() {
        return baseIcon.getIconName();
    }
}
