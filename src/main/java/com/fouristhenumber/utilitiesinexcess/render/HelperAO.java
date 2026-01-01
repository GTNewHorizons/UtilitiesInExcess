package com.fouristhenumber.utilitiesinexcess.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class HelperAO {

    public static boolean RenderYNeg(RenderBlocks renderer, Block block, int x, int y, int z, float red, float green,
        float blue, IIcon iicon, ForgeDirection rotation) {
        renderer.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);
        int i1;
        float f7;

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y - 1, z, 0)) {
            if (renderer.renderMinY <= 0.0D) {
                --y;
            }

            renderer.aoBrightnessXYNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z);
            renderer.aoBrightnessYZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1);
            renderer.aoBrightnessYZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1);
            renderer.aoBrightnessXYPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z);
            renderer.aoLightValueScratchXYNN = renderer.blockAccess.getBlock(x - 1, y, z)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZNN = renderer.blockAccess.getBlock(x, y, z - 1)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZNP = renderer.blockAccess.getBlock(x, y, z + 1)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXYPN = renderer.blockAccess.getBlock(x + 1, y, z)
                .getAmbientOcclusionLightValue();

            renderer.aoLightValueScratchXYZNNN = renderer.aoLightValueScratchXYNN;
            renderer.aoBrightnessXYZNNN = renderer.aoBrightnessXYNN;
            renderer.aoLightValueScratchXYZNNP = renderer.aoLightValueScratchXYNN;
            renderer.aoBrightnessXYZNNP = renderer.aoBrightnessXYNN;
            renderer.aoLightValueScratchXYZPNN = renderer.aoLightValueScratchXYPN;
            renderer.aoBrightnessXYZPNN = renderer.aoBrightnessXYPN;
            renderer.aoLightValueScratchXYZPNP = renderer.aoLightValueScratchXYPN;
            renderer.aoBrightnessXYZPNP = renderer.aoBrightnessXYPN;

            if (renderer.renderMinY <= 0.0D) {
                ++y;
            }

            i1 = l;

            if (renderer.renderMinY <= 0.0D || !renderer.blockAccess.getBlock(x, y - 1, z)
                .isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z);
            }

            f7 = renderer.blockAccess.getBlock(x, y - 1, z)
                .getAmbientOcclusionLightValue();
            f3 = (renderer.aoLightValueScratchXYZNNP + renderer.aoLightValueScratchXYNN
                + renderer.aoLightValueScratchYZNP
                + f7) / 4.0F;
            f6 = (renderer.aoLightValueScratchYZNP + f7
                + renderer.aoLightValueScratchXYZPNP
                + renderer.aoLightValueScratchXYPN) / 4.0F;
            f5 = (f7 + renderer.aoLightValueScratchYZNN
                + renderer.aoLightValueScratchXYPN
                + renderer.aoLightValueScratchXYZPNN) / 4.0F;
            f4 = (renderer.aoLightValueScratchXYNN + renderer.aoLightValueScratchXYZNNN
                + f7
                + renderer.aoLightValueScratchYZNN) / 4.0F;
            renderer.brightnessTopLeft = renderer
                .getAoBrightness(renderer.aoBrightnessXYZNNP, renderer.aoBrightnessXYNN, renderer.aoBrightnessYZNP, i1);
            renderer.brightnessTopRight = renderer
                .getAoBrightness(renderer.aoBrightnessYZNP, renderer.aoBrightnessXYZPNP, renderer.aoBrightnessXYPN, i1);
            renderer.brightnessBottomRight = renderer
                .getAoBrightness(renderer.aoBrightnessYZNN, renderer.aoBrightnessXYPN, renderer.aoBrightnessXYZPNN, i1);
            renderer.brightnessBottomLeft = renderer
                .getAoBrightness(renderer.aoBrightnessXYNN, renderer.aoBrightnessXYZNNN, renderer.aoBrightnessYZNN, i1);

            renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = red
                * 0.5F;
            renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = green
                * 0.5F;
            renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = blue
                * 0.5F;

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderFaceYNeg(renderer, block, (double) x, (double) y, (double) z, iicon, rotation);
            flag = true;
        }

        renderer.enableAO = false;
        return flag;
    }

    /**
     * Renders the bottom face with optional UV rotation (0-3) for top/bottom fronts
     */
    public static void renderFaceYNeg(RenderBlocks renderer, Block block, double x, double y, double z, IIcon icon,
        ForgeDirection rotationDir) {
        Tessellator tessellator = Tessellator.instance;

        if (renderer.hasOverrideBlockTexture()) {
            icon = renderer.overrideBlockTexture;
        }

        // Base UVs
        double uMin = icon.getInterpolatedU(renderer.renderMinX * 16.0D);
        double uMax = icon.getInterpolatedU(renderer.renderMaxX * 16.0D);
        double vMin = icon.getInterpolatedV(renderer.renderMinZ * 16.0D);
        double vMax = icon.getInterpolatedV(renderer.renderMaxZ * 16.0D);

        double[] u = new double[] { uMin, uMax, uMax, uMin };
        double[] v = new double[] { vMin, vMin, vMax, vMax };

        int rotation = rotationFromDirection(rotationDir);

        // Apply rotation
        int bottomRotation = (1 - rotation + 4) % 4;
        if (bottomRotation != 0) {
            double[] tmpU = u.clone();
            double[] tmpV = v.clone();
            for (int i = 0; i < 4; i++) {
                u[i] = tmpU[(i + bottomRotation) % 4];
                v[i] = tmpV[(i + bottomRotation) % 4];
            }
        }

        // Vertices positions
        double x1 = x + renderer.renderMinX;
        double x2 = x + renderer.renderMaxX;
        double yPos = y + renderer.renderMinY;
        double z1 = z + renderer.renderMinZ;
        double z2 = z + renderer.renderMaxZ;

        if (renderer.renderFromInside) {
            double tmp = x1;
            x1 = x2;
            x2 = tmp;
        }

        if (renderer.enableAO) {
            tessellator
                .setColorOpaque_F(renderer.colorRedTopLeft, renderer.colorGreenTopLeft, renderer.colorBlueTopLeft);
            tessellator.setBrightness(renderer.brightnessTopLeft);
            tessellator.addVertexWithUV(x1, yPos, z2, u[0], v[0]);

            tessellator.setColorOpaque_F(
                renderer.colorRedBottomLeft,
                renderer.colorGreenBottomLeft,
                renderer.colorBlueBottomLeft);
            tessellator.setBrightness(renderer.brightnessBottomLeft);
            tessellator.addVertexWithUV(x1, yPos, z1, u[1], v[1]);

            tessellator.setColorOpaque_F(
                renderer.colorRedBottomRight,
                renderer.colorGreenBottomRight,
                renderer.colorBlueBottomRight);
            tessellator.setBrightness(renderer.brightnessBottomRight);
            tessellator.addVertexWithUV(x2, yPos, z1, u[2], v[2]);

            tessellator
                .setColorOpaque_F(renderer.colorRedTopRight, renderer.colorGreenTopRight, renderer.colorBlueTopRight);
            tessellator.setBrightness(renderer.brightnessTopRight);
            tessellator.addVertexWithUV(x2, yPos, z2, u[3], v[3]);
        } else {
            tessellator.addVertexWithUV(x1, yPos, z2, u[0], v[0]);
            tessellator.addVertexWithUV(x1, yPos, z1, u[1], v[1]);
            tessellator.addVertexWithUV(x2, yPos, z1, u[2], v[2]);
            tessellator.addVertexWithUV(x2, yPos, z2, u[3], v[3]);
        }
    }

    public static boolean RenderYPos(RenderBlocks renderer, Block block, int x, int y, int z, float red, float green,
        float blue, IIcon iicon, ForgeDirection rotation) {
        renderer.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);
        int i1;
        float f7;

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y + 1, z, 1)) {
            if (renderer.renderMaxY >= 1.0D) {
                ++y;
            }

            renderer.aoBrightnessXYNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z);
            renderer.aoBrightnessXYPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z);
            renderer.aoBrightnessYZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1);
            renderer.aoBrightnessYZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1);
            renderer.aoLightValueScratchXYNP = renderer.blockAccess.getBlock(x - 1, y, z)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXYPP = renderer.blockAccess.getBlock(x + 1, y, z)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZPN = renderer.blockAccess.getBlock(x, y, z - 1)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZPP = renderer.blockAccess.getBlock(x, y, z + 1)
                .getAmbientOcclusionLightValue();

            renderer.aoLightValueScratchXYZNPN = renderer.aoLightValueScratchXYNP;
            renderer.aoBrightnessXYZNPN = renderer.aoBrightnessXYNP;
            renderer.aoLightValueScratchXYZPPN = renderer.aoLightValueScratchXYPP;
            renderer.aoBrightnessXYZPPN = renderer.aoBrightnessXYPP;
            renderer.aoLightValueScratchXYZNPP = renderer.aoLightValueScratchXYNP;
            renderer.aoBrightnessXYZNPP = renderer.aoBrightnessXYNP;
            renderer.aoLightValueScratchXYZPPP = renderer.aoLightValueScratchXYPP;
            renderer.aoBrightnessXYZPPP = renderer.aoBrightnessXYPP;

            if (renderer.renderMaxY >= 1.0D) {
                --y;
            }

            i1 = l;

            if (renderer.renderMaxY >= 1.0D || !renderer.blockAccess.getBlock(x, y + 1, z)
                .isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z);
            }

            f7 = renderer.blockAccess.getBlock(x, y + 1, z)
                .getAmbientOcclusionLightValue();
            f6 = (renderer.aoLightValueScratchXYZNPP + renderer.aoLightValueScratchXYNP
                + renderer.aoLightValueScratchYZPP
                + f7) / 4.0F;
            f3 = (renderer.aoLightValueScratchYZPP + f7
                + renderer.aoLightValueScratchXYZPPP
                + renderer.aoLightValueScratchXYPP) / 4.0F;
            f4 = (f7 + renderer.aoLightValueScratchYZPN
                + renderer.aoLightValueScratchXYPP
                + renderer.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (renderer.aoLightValueScratchXYNP + renderer.aoLightValueScratchXYZNPN
                + f7
                + renderer.aoLightValueScratchYZPN) / 4.0F;
            renderer.brightnessTopRight = renderer
                .getAoBrightness(renderer.aoBrightnessXYZNPP, renderer.aoBrightnessXYNP, renderer.aoBrightnessYZPP, i1);
            renderer.brightnessTopLeft = renderer
                .getAoBrightness(renderer.aoBrightnessYZPP, renderer.aoBrightnessXYZPPP, renderer.aoBrightnessXYPP, i1);
            renderer.brightnessBottomLeft = renderer
                .getAoBrightness(renderer.aoBrightnessYZPN, renderer.aoBrightnessXYPP, renderer.aoBrightnessXYZPPN, i1);
            renderer.brightnessBottomRight = renderer
                .getAoBrightness(renderer.aoBrightnessXYNP, renderer.aoBrightnessXYZNPN, renderer.aoBrightnessYZPN, i1);
            renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = red;
            renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = green;
            renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = blue;
            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderFaceYPos(renderer, block, (double) x, (double) y, (double) z, iicon, rotation);
            flag = true;
        }

        renderer.enableAO = false;
        return flag;
    }

    /**
     * Renders the top face with optional UV rotation (0-3) for top/bottom fronts
     */
    public static void renderFaceYPos(RenderBlocks renderer, Block block, double x, double y, double z, IIcon icon,
        ForgeDirection rotationDir) {
        Tessellator tessellator = Tessellator.instance;

        if (renderer.hasOverrideBlockTexture()) {
            icon = renderer.overrideBlockTexture;
        }

        // Base UVs
        double uMin = icon.getInterpolatedU(renderer.renderMinX * 16.0D);
        double uMax = icon.getInterpolatedU(renderer.renderMaxX * 16.0D);
        double vMin = icon.getInterpolatedV(renderer.renderMinZ * 16.0D);
        double vMax = icon.getInterpolatedV(renderer.renderMaxZ * 16.0D);

        int rotation = rotationFromDirection(rotationDir);

        double[] u = { uMin, uMax, uMax, uMin };
        double[] v = { vMin, vMin, vMax, vMax };

        // Apply rotation
        int rotationAdjusted = (rotation + 1) & 3; // rotate one step clockwise
        if (rotationAdjusted != 0) {
            double[] tmpU = u.clone();
            double[] tmpV = v.clone();
            for (int i = 0; i < 4; i++) {
                u[i] = tmpU[(i + rotationAdjusted) % 4];
                v[i] = tmpV[(i + rotationAdjusted) % 4];
            }
        }

        // Vertices positions
        double x1 = x + renderer.renderMinX;
        double x2 = x + renderer.renderMaxX;
        double yPos = y + renderer.renderMaxY;
        double z1 = z + renderer.renderMinZ;
        double z2 = z + renderer.renderMaxZ;

        if (renderer.renderFromInside) {
            double tmp = x1;
            x1 = x2;
            x2 = tmp;
        }

        if (renderer.enableAO) {
            tessellator
                .setColorOpaque_F(renderer.colorRedTopLeft, renderer.colorGreenTopLeft, renderer.colorBlueTopLeft);
            tessellator.setBrightness(renderer.brightnessTopLeft);
            tessellator.addVertexWithUV(x2, yPos, z2, u[0], v[0]);

            tessellator.setColorOpaque_F(
                renderer.colorRedBottomLeft,
                renderer.colorGreenBottomLeft,
                renderer.colorBlueBottomLeft);
            tessellator.setBrightness(renderer.brightnessBottomLeft);
            tessellator.addVertexWithUV(x2, yPos, z1, u[1], v[1]);

            tessellator.setColorOpaque_F(
                renderer.colorRedBottomRight,
                renderer.colorGreenBottomRight,
                renderer.colorBlueBottomRight);
            tessellator.setBrightness(renderer.brightnessBottomRight);
            tessellator.addVertexWithUV(x1, yPos, z1, u[2], v[2]);

            tessellator
                .setColorOpaque_F(renderer.colorRedTopRight, renderer.colorGreenTopRight, renderer.colorBlueTopRight);
            tessellator.setBrightness(renderer.brightnessTopRight);
            tessellator.addVertexWithUV(x1, yPos, z2, u[3], v[3]);
        } else {
            tessellator.addVertexWithUV(x2, yPos, z2, u[0], v[0]);
            tessellator.addVertexWithUV(x2, yPos, z1, u[1], v[1]);
            tessellator.addVertexWithUV(x1, yPos, z1, u[2], v[2]);
            tessellator.addVertexWithUV(x1, yPos, z2, u[3], v[3]);
        }
    }

    public static boolean RenderXNeg(RenderBlocks renderer, Block block, int x, int y, int z, float red, float green,
        float blue, IIcon iicon) {
        renderer.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);
        int i1;
        float f7;
        float f8;
        float f9;
        float f10;
        float f11;
        int j1;
        int k1;
        int l1;
        int i2;

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x - 1, y, z, 4)) {
            if (renderer.renderMinX <= 0.0D) {
                --x;
            }

            renderer.aoLightValueScratchXYNN = renderer.blockAccess.getBlock(x, y - 1, z)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZNN = renderer.blockAccess.getBlock(x, y, z - 1)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZNP = renderer.blockAccess.getBlock(x, y, z + 1)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXYNP = renderer.blockAccess.getBlock(x, y + 1, z)
                .getAmbientOcclusionLightValue();
            renderer.aoBrightnessXYNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z);
            renderer.aoBrightnessXZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1);
            renderer.aoBrightnessXZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1);
            renderer.aoBrightnessXYNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z);

            renderer.aoLightValueScratchXYZNNN = renderer.aoLightValueScratchXZNN;
            renderer.aoBrightnessXYZNNN = renderer.aoBrightnessXZNN;
            renderer.aoLightValueScratchXYZNNP = renderer.aoLightValueScratchXZNP;
            renderer.aoBrightnessXYZNNP = renderer.aoBrightnessXZNP;
            renderer.aoLightValueScratchXYZNPN = renderer.aoLightValueScratchXZNN;
            renderer.aoBrightnessXYZNPN = renderer.aoBrightnessXZNN;
            renderer.aoLightValueScratchXYZNPP = renderer.aoLightValueScratchXZNP;
            renderer.aoBrightnessXYZNPP = renderer.aoBrightnessXZNP;

            if (renderer.renderMinX <= 0.0D) {
                ++x;
            }

            i1 = l;

            if (renderer.renderMinX <= 0.0D || !renderer.blockAccess.getBlock(x - 1, y, z)
                .isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z);
            }

            f7 = renderer.blockAccess.getBlock(x - 1, y, z)
                .getAmbientOcclusionLightValue();
            f8 = (renderer.aoLightValueScratchXYNN + renderer.aoLightValueScratchXYZNNP
                + f7
                + renderer.aoLightValueScratchXZNP) / 4.0F;
            f9 = (f7 + renderer.aoLightValueScratchXZNP
                + renderer.aoLightValueScratchXYNP
                + renderer.aoLightValueScratchXYZNPP) / 4.0F;
            f10 = (renderer.aoLightValueScratchXZNN + f7
                + renderer.aoLightValueScratchXYZNPN
                + renderer.aoLightValueScratchXYNP) / 4.0F;
            f11 = (renderer.aoLightValueScratchXYZNNN + renderer.aoLightValueScratchXYNN
                + renderer.aoLightValueScratchXZNN
                + f7) / 4.0F;
            f3 = (float) ((double) f9 * renderer.renderMaxY * renderer.renderMaxZ
                + (double) f10 * renderer.renderMaxY * (1.0D - renderer.renderMaxZ)
                + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxZ)
                + (double) f8 * (1.0D - renderer.renderMaxY) * renderer.renderMaxZ);
            f4 = (float) ((double) f9 * renderer.renderMaxY * renderer.renderMinZ
                + (double) f10 * renderer.renderMaxY * (1.0D - renderer.renderMinZ)
                + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinZ)
                + (double) f8 * (1.0D - renderer.renderMaxY) * renderer.renderMinZ);
            f5 = (float) ((double) f9 * renderer.renderMinY * renderer.renderMinZ
                + (double) f10 * renderer.renderMinY * (1.0D - renderer.renderMinZ)
                + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinZ)
                + (double) f8 * (1.0D - renderer.renderMinY) * renderer.renderMinZ);
            f6 = (float) ((double) f9 * renderer.renderMinY * renderer.renderMaxZ
                + (double) f10 * renderer.renderMinY * (1.0D - renderer.renderMaxZ)
                + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxZ)
                + (double) f8 * (1.0D - renderer.renderMinY) * renderer.renderMaxZ);
            j1 = renderer
                .getAoBrightness(renderer.aoBrightnessXYNN, renderer.aoBrightnessXYZNNP, renderer.aoBrightnessXZNP, i1);
            k1 = renderer
                .getAoBrightness(renderer.aoBrightnessXZNP, renderer.aoBrightnessXYNP, renderer.aoBrightnessXYZNPP, i1);
            l1 = renderer
                .getAoBrightness(renderer.aoBrightnessXZNN, renderer.aoBrightnessXYZNPN, renderer.aoBrightnessXYNP, i1);
            i2 = renderer
                .getAoBrightness(renderer.aoBrightnessXYZNNN, renderer.aoBrightnessXYNN, renderer.aoBrightnessXZNN, i1);
            renderer.brightnessTopLeft = renderer.mixAoBrightness(
                k1,
                l1,
                i2,
                j1,
                renderer.renderMaxY * renderer.renderMaxZ,
                renderer.renderMaxY * (1.0D - renderer.renderMaxZ),
                (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxZ),
                (1.0D - renderer.renderMaxY) * renderer.renderMaxZ);
            renderer.brightnessBottomLeft = renderer.mixAoBrightness(
                k1,
                l1,
                i2,
                j1,
                renderer.renderMaxY * renderer.renderMinZ,
                renderer.renderMaxY * (1.0D - renderer.renderMinZ),
                (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinZ),
                (1.0D - renderer.renderMaxY) * renderer.renderMinZ);
            renderer.brightnessBottomRight = renderer.mixAoBrightness(
                k1,
                l1,
                i2,
                j1,
                renderer.renderMinY * renderer.renderMinZ,
                renderer.renderMinY * (1.0D - renderer.renderMinZ),
                (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinZ),
                (1.0D - renderer.renderMinY) * renderer.renderMinZ);
            renderer.brightnessTopRight = renderer.mixAoBrightness(
                k1,
                l1,
                i2,
                j1,
                renderer.renderMinY * renderer.renderMaxZ,
                renderer.renderMinY * (1.0D - renderer.renderMaxZ),
                (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxZ),
                (1.0D - renderer.renderMinY) * renderer.renderMaxZ);

            renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = red
                * 0.6F;
            renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = green
                * 0.6F;
            renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = blue
                * 0.6F;

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderer.renderFaceXNeg(block, (double) x, (double) y, (double) z, iicon);

            flag = true;
        }

        renderer.enableAO = false;
        return flag;
    }

    public static boolean RenderXPos(RenderBlocks renderer, Block block, int x, int y, int z, float red, float green,
        float blue, IIcon iicon) {
        renderer.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);
        int i1;
        float f7;
        float f8;
        float f9;
        float f10;
        float f11;
        int j1;
        int k1;
        int l1;
        int i2;

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x + 1, y, z, 5)) {
            if (renderer.renderMaxX >= 1.0D) {
                ++x;
            }

            renderer.aoLightValueScratchXYPN = renderer.blockAccess.getBlock(x, y - 1, z)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZPN = renderer.blockAccess.getBlock(x, y, z - 1)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZPP = renderer.blockAccess.getBlock(x, y, z + 1)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXYPP = renderer.blockAccess.getBlock(x, y + 1, z)
                .getAmbientOcclusionLightValue();
            renderer.aoBrightnessXYPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z);
            renderer.aoBrightnessXZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1);
            renderer.aoBrightnessXZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1);
            renderer.aoBrightnessXYPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z);

            renderer.aoLightValueScratchXYZPNN = renderer.aoLightValueScratchXZPN;
            renderer.aoBrightnessXYZPNN = renderer.aoBrightnessXZPN;
            renderer.aoLightValueScratchXYZPNP = renderer.aoLightValueScratchXZPP;
            renderer.aoBrightnessXYZPNP = renderer.aoBrightnessXZPP;
            renderer.aoLightValueScratchXYZPPN = renderer.aoLightValueScratchXZPN;
            renderer.aoBrightnessXYZPPN = renderer.aoBrightnessXZPN;
            renderer.aoLightValueScratchXYZPPP = renderer.aoLightValueScratchXZPP;
            renderer.aoBrightnessXYZPPP = renderer.aoBrightnessXZPP;

            if (renderer.renderMaxX >= 1.0D) {
                --x;
            }

            i1 = l;

            if (renderer.renderMaxX >= 1.0D || !renderer.blockAccess.getBlock(x + 1, y, z)
                .isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z);
            }

            f7 = renderer.blockAccess.getBlock(x + 1, y, z)
                .getAmbientOcclusionLightValue();
            f8 = (renderer.aoLightValueScratchXYPN + renderer.aoLightValueScratchXYZPNP
                + f7
                + renderer.aoLightValueScratchXZPP) / 4.0F;
            f9 = (renderer.aoLightValueScratchXYZPNN + renderer.aoLightValueScratchXYPN
                + renderer.aoLightValueScratchXZPN
                + f7) / 4.0F;
            f10 = (renderer.aoLightValueScratchXZPN + f7
                + renderer.aoLightValueScratchXYZPPN
                + renderer.aoLightValueScratchXYPP) / 4.0F;
            f11 = (f7 + renderer.aoLightValueScratchXZPP
                + renderer.aoLightValueScratchXYPP
                + renderer.aoLightValueScratchXYZPPP) / 4.0F;
            f3 = (float) ((double) f8 * (1.0D - renderer.renderMinY) * renderer.renderMaxZ
                + (double) f9 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxZ)
                + (double) f10 * renderer.renderMinY * (1.0D - renderer.renderMaxZ)
                + (double) f11 * renderer.renderMinY * renderer.renderMaxZ);
            f4 = (float) ((double) f8 * (1.0D - renderer.renderMinY) * renderer.renderMinZ
                + (double) f9 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinZ)
                + (double) f10 * renderer.renderMinY * (1.0D - renderer.renderMinZ)
                + (double) f11 * renderer.renderMinY * renderer.renderMinZ);
            f5 = (float) ((double) f8 * (1.0D - renderer.renderMaxY) * renderer.renderMinZ
                + (double) f9 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinZ)
                + (double) f10 * renderer.renderMaxY * (1.0D - renderer.renderMinZ)
                + (double) f11 * renderer.renderMaxY * renderer.renderMinZ);
            f6 = (float) ((double) f8 * (1.0D - renderer.renderMaxY) * renderer.renderMaxZ
                + (double) f9 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxZ)
                + (double) f10 * renderer.renderMaxY * (1.0D - renderer.renderMaxZ)
                + (double) f11 * renderer.renderMaxY * renderer.renderMaxZ);
            j1 = renderer
                .getAoBrightness(renderer.aoBrightnessXYPN, renderer.aoBrightnessXYZPNP, renderer.aoBrightnessXZPP, i1);
            k1 = renderer
                .getAoBrightness(renderer.aoBrightnessXZPP, renderer.aoBrightnessXYPP, renderer.aoBrightnessXYZPPP, i1);
            l1 = renderer
                .getAoBrightness(renderer.aoBrightnessXZPN, renderer.aoBrightnessXYZPPN, renderer.aoBrightnessXYPP, i1);
            i2 = renderer
                .getAoBrightness(renderer.aoBrightnessXYZPNN, renderer.aoBrightnessXYPN, renderer.aoBrightnessXZPN, i1);
            renderer.brightnessTopLeft = renderer.mixAoBrightness(
                j1,
                i2,
                l1,
                k1,
                (1.0D - renderer.renderMinY) * renderer.renderMaxZ,
                (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxZ),
                renderer.renderMinY * (1.0D - renderer.renderMaxZ),
                renderer.renderMinY * renderer.renderMaxZ);
            renderer.brightnessBottomLeft = renderer.mixAoBrightness(
                j1,
                i2,
                l1,
                k1,
                (1.0D - renderer.renderMinY) * renderer.renderMinZ,
                (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinZ),
                renderer.renderMinY * (1.0D - renderer.renderMinZ),
                renderer.renderMinY * renderer.renderMinZ);
            renderer.brightnessBottomRight = renderer.mixAoBrightness(
                j1,
                i2,
                l1,
                k1,
                (1.0D - renderer.renderMaxY) * renderer.renderMinZ,
                (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinZ),
                renderer.renderMaxY * (1.0D - renderer.renderMinZ),
                renderer.renderMaxY * renderer.renderMinZ);
            renderer.brightnessTopRight = renderer.mixAoBrightness(
                j1,
                i2,
                l1,
                k1,
                (1.0D - renderer.renderMaxY) * renderer.renderMaxZ,
                (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxZ),
                renderer.renderMaxY * (1.0D - renderer.renderMaxZ),
                renderer.renderMaxY * renderer.renderMaxZ);

            renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = red
                * 0.6F;
            renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = green
                * 0.6F;
            renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = blue
                * 0.6F;

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderer.renderFaceXPos(block, (double) x, (double) y, (double) z, iicon);

            flag = true;
        }

        renderer.enableAO = false;
        return flag;
    }

    public static boolean RenderZNeg(RenderBlocks renderer, Block block, int x, int y, int z, float red, float green,
        float blue, IIcon iicon) {
        renderer.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);
        int i1;
        float f7;
        float f8;
        float f9;
        float f10;
        float f11;
        int j1;
        int k1;
        int l1;
        int i2;

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y, z - 1, 2)) {
            if (renderer.renderMinZ <= 0.0D) {
                --z;
            }

            renderer.aoLightValueScratchXZNN = renderer.blockAccess.getBlock(x - 1, y, z)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZNN = renderer.blockAccess.getBlock(x, y - 1, z)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZPN = renderer.blockAccess.getBlock(x, y + 1, z)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZPN = renderer.blockAccess.getBlock(x + 1, y, z)
                .getAmbientOcclusionLightValue();
            renderer.aoBrightnessXZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z);
            renderer.aoBrightnessYZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z);
            renderer.aoBrightnessYZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z);
            renderer.aoBrightnessXZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z);

            renderer.aoLightValueScratchXYZNNN = renderer.aoLightValueScratchXZNN;
            renderer.aoBrightnessXYZNNN = renderer.aoBrightnessXZNN;
            renderer.aoLightValueScratchXYZNPN = renderer.aoLightValueScratchXZNN;
            renderer.aoBrightnessXYZNPN = renderer.aoBrightnessXZNN;
            renderer.aoLightValueScratchXYZPNN = renderer.aoLightValueScratchXZPN;
            renderer.aoBrightnessXYZPNN = renderer.aoBrightnessXZPN;
            renderer.aoLightValueScratchXYZPPN = renderer.aoLightValueScratchXZPN;
            renderer.aoBrightnessXYZPPN = renderer.aoBrightnessXZPN;

            if (renderer.renderMinZ <= 0.0D) {
                ++z;
            }

            i1 = l;

            if (renderer.renderMinZ <= 0.0D || !renderer.blockAccess.getBlock(x, y, z - 1)
                .isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1);
            }

            f7 = renderer.blockAccess.getBlock(x, y, z - 1)
                .getAmbientOcclusionLightValue();
            f8 = (renderer.aoLightValueScratchXZNN + renderer.aoLightValueScratchXYZNPN
                + f7
                + renderer.aoLightValueScratchYZPN) / 4.0F;
            f9 = (f7 + renderer.aoLightValueScratchYZPN
                + renderer.aoLightValueScratchXZPN
                + renderer.aoLightValueScratchXYZPPN) / 4.0F;
            f10 = (renderer.aoLightValueScratchYZNN + f7
                + renderer.aoLightValueScratchXYZPNN
                + renderer.aoLightValueScratchXZPN) / 4.0F;
            f11 = (renderer.aoLightValueScratchXYZNNN + renderer.aoLightValueScratchXZNN
                + renderer.aoLightValueScratchYZNN
                + f7) / 4.0F;
            f3 = (float) ((double) f8 * renderer.renderMaxY * (1.0D - renderer.renderMinX)
                + (double) f9 * renderer.renderMaxY * renderer.renderMinX
                + (double) f10 * (1.0D - renderer.renderMaxY) * renderer.renderMinX
                + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinX));
            f4 = (float) ((double) f8 * renderer.renderMaxY * (1.0D - renderer.renderMaxX)
                + (double) f9 * renderer.renderMaxY * renderer.renderMaxX
                + (double) f10 * (1.0D - renderer.renderMaxY) * renderer.renderMaxX
                + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxX));
            f5 = (float) ((double) f8 * renderer.renderMinY * (1.0D - renderer.renderMaxX)
                + (double) f9 * renderer.renderMinY * renderer.renderMaxX
                + (double) f10 * (1.0D - renderer.renderMinY) * renderer.renderMaxX
                + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxX));
            f6 = (float) ((double) f8 * renderer.renderMinY * (1.0D - renderer.renderMinX)
                + (double) f9 * renderer.renderMinY * renderer.renderMinX
                + (double) f10 * (1.0D - renderer.renderMinY) * renderer.renderMinX
                + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinX));
            j1 = renderer
                .getAoBrightness(renderer.aoBrightnessXZNN, renderer.aoBrightnessXYZNPN, renderer.aoBrightnessYZPN, i1);
            k1 = renderer
                .getAoBrightness(renderer.aoBrightnessYZPN, renderer.aoBrightnessXZPN, renderer.aoBrightnessXYZPPN, i1);
            l1 = renderer
                .getAoBrightness(renderer.aoBrightnessYZNN, renderer.aoBrightnessXYZPNN, renderer.aoBrightnessXZPN, i1);
            i2 = renderer
                .getAoBrightness(renderer.aoBrightnessXYZNNN, renderer.aoBrightnessXZNN, renderer.aoBrightnessYZNN, i1);
            renderer.brightnessTopLeft = renderer.mixAoBrightness(
                j1,
                k1,
                l1,
                i2,
                renderer.renderMaxY * (1.0D - renderer.renderMinX),
                renderer.renderMaxY * renderer.renderMinX,
                (1.0D - renderer.renderMaxY) * renderer.renderMinX,
                (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinX));
            renderer.brightnessBottomLeft = renderer.mixAoBrightness(
                j1,
                k1,
                l1,
                i2,
                renderer.renderMaxY * (1.0D - renderer.renderMaxX),
                renderer.renderMaxY * renderer.renderMaxX,
                (1.0D - renderer.renderMaxY) * renderer.renderMaxX,
                (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxX));
            renderer.brightnessBottomRight = renderer.mixAoBrightness(
                j1,
                k1,
                l1,
                i2,
                renderer.renderMinY * (1.0D - renderer.renderMaxX),
                renderer.renderMinY * renderer.renderMaxX,
                (1.0D - renderer.renderMinY) * renderer.renderMaxX,
                (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxX));
            renderer.brightnessTopRight = renderer.mixAoBrightness(
                j1,
                k1,
                l1,
                i2,
                renderer.renderMinY * (1.0D - renderer.renderMinX),
                renderer.renderMinY * renderer.renderMinX,
                (1.0D - renderer.renderMinY) * renderer.renderMinX,
                (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinX));

            renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = red
                * 0.8F;
            renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = green
                * 0.8F;
            renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = blue
                * 0.8F;

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderer.renderFaceZNeg(block, (double) x, (double) y, (double) z, iicon);

            flag = true;
        }

        renderer.enableAO = false;
        return flag;
    }

    public static boolean RenderZPos(RenderBlocks renderer, Block block, int x, int y, int z, float red, float green,
        float blue, IIcon iicon) {
        renderer.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);
        int i1;
        float f7;
        float f8;
        float f9;
        float f10;
        float f11;
        int j1;
        int k1;
        int l1;
        int i2;

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y, z + 1, 3)) {
            if (renderer.renderMaxZ >= 1.0D) {
                ++z;
            }

            renderer.aoLightValueScratchXZNP = renderer.blockAccess.getBlock(x - 1, y, z)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZPP = renderer.blockAccess.getBlock(x + 1, y, z)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZNP = renderer.blockAccess.getBlock(x, y - 1, z)
                .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZPP = renderer.blockAccess.getBlock(x, y + 1, z)
                .getAmbientOcclusionLightValue();
            renderer.aoBrightnessXZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z);
            renderer.aoBrightnessXZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z);
            renderer.aoBrightnessYZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z);
            renderer.aoBrightnessYZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z);

            renderer.aoLightValueScratchXYZNNP = renderer.aoLightValueScratchXZNP;
            renderer.aoBrightnessXYZNNP = renderer.aoBrightnessXZNP;
            renderer.aoLightValueScratchXYZNPP = renderer.aoLightValueScratchXZNP;
            renderer.aoBrightnessXYZNPP = renderer.aoBrightnessXZNP;
            renderer.aoLightValueScratchXYZPNP = renderer.aoLightValueScratchXZPP;
            renderer.aoBrightnessXYZPNP = renderer.aoBrightnessXZPP;
            renderer.aoLightValueScratchXYZPPP = renderer.aoLightValueScratchXZPP;
            renderer.aoBrightnessXYZPPP = renderer.aoBrightnessXZPP;

            if (renderer.renderMaxZ >= 1.0D) {
                --z;
            }

            i1 = l;

            if (renderer.renderMaxZ >= 1.0D || !renderer.blockAccess.getBlock(x, y, z + 1)
                .isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1);
            }

            f7 = renderer.blockAccess.getBlock(x, y, z + 1)
                .getAmbientOcclusionLightValue();
            f8 = (renderer.aoLightValueScratchXZNP + renderer.aoLightValueScratchXYZNPP
                + f7
                + renderer.aoLightValueScratchYZPP) / 4.0F;
            f9 = (f7 + renderer.aoLightValueScratchYZPP
                + renderer.aoLightValueScratchXZPP
                + renderer.aoLightValueScratchXYZPPP) / 4.0F;
            f10 = (renderer.aoLightValueScratchYZNP + f7
                + renderer.aoLightValueScratchXYZPNP
                + renderer.aoLightValueScratchXZPP) / 4.0F;
            f11 = (renderer.aoLightValueScratchXYZNNP + renderer.aoLightValueScratchXZNP
                + renderer.aoLightValueScratchYZNP
                + f7) / 4.0F;
            f3 = (float) ((double) f8 * renderer.renderMaxY * (1.0D - renderer.renderMinX)
                + (double) f9 * renderer.renderMaxY * renderer.renderMinX
                + (double) f10 * (1.0D - renderer.renderMaxY) * renderer.renderMinX
                + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinX));
            f4 = (float) ((double) f8 * renderer.renderMinY * (1.0D - renderer.renderMinX)
                + (double) f9 * renderer.renderMinY * renderer.renderMinX
                + (double) f10 * (1.0D - renderer.renderMinY) * renderer.renderMinX
                + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinX));
            f5 = (float) ((double) f8 * renderer.renderMinY * (1.0D - renderer.renderMaxX)
                + (double) f9 * renderer.renderMinY * renderer.renderMaxX
                + (double) f10 * (1.0D - renderer.renderMinY) * renderer.renderMaxX
                + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxX));
            f6 = (float) ((double) f8 * renderer.renderMaxY * (1.0D - renderer.renderMaxX)
                + (double) f9 * renderer.renderMaxY * renderer.renderMaxX
                + (double) f10 * (1.0D - renderer.renderMaxY) * renderer.renderMaxX
                + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxX));
            j1 = renderer
                .getAoBrightness(renderer.aoBrightnessXZNP, renderer.aoBrightnessXYZNPP, renderer.aoBrightnessYZPP, i1);
            k1 = renderer
                .getAoBrightness(renderer.aoBrightnessYZPP, renderer.aoBrightnessXZPP, renderer.aoBrightnessXYZPPP, i1);
            l1 = renderer
                .getAoBrightness(renderer.aoBrightnessYZNP, renderer.aoBrightnessXYZPNP, renderer.aoBrightnessXZPP, i1);
            i2 = renderer
                .getAoBrightness(renderer.aoBrightnessXYZNNP, renderer.aoBrightnessXZNP, renderer.aoBrightnessYZNP, i1);
            renderer.brightnessTopLeft = renderer.mixAoBrightness(
                j1,
                i2,
                l1,
                k1,
                renderer.renderMaxY * (1.0D - renderer.renderMinX),
                (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinX),
                (1.0D - renderer.renderMaxY) * renderer.renderMinX,
                renderer.renderMaxY * renderer.renderMinX);
            renderer.brightnessBottomLeft = renderer.mixAoBrightness(
                j1,
                i2,
                l1,
                k1,
                renderer.renderMinY * (1.0D - renderer.renderMinX),
                (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinX),
                (1.0D - renderer.renderMinY) * renderer.renderMinX,
                renderer.renderMinY * renderer.renderMinX);
            renderer.brightnessBottomRight = renderer.mixAoBrightness(
                j1,
                i2,
                l1,
                k1,
                renderer.renderMinY * (1.0D - renderer.renderMaxX),
                (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxX),
                (1.0D - renderer.renderMinY) * renderer.renderMaxX,
                renderer.renderMinY * renderer.renderMaxX);
            renderer.brightnessTopRight = renderer.mixAoBrightness(
                j1,
                i2,
                l1,
                k1,
                renderer.renderMaxY * (1.0D - renderer.renderMaxX),
                (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxX),
                (1.0D - renderer.renderMaxY) * renderer.renderMaxX,
                renderer.renderMaxY * renderer.renderMaxX);

            renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = red
                * 0.8F;
            renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = green
                * 0.8F;
            renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = blue
                * 0.8F;

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderer.renderFaceZPos(block, (double) x, (double) y, (double) z, iicon);

            flag = true;
        }

        renderer.enableAO = false;
        return flag;
    }

    private static int rotationFromDirection(ForgeDirection dir) {
        switch (dir) {
            case SOUTH:
                return 0; // 0°
            case WEST:
                return 1; // 90°
            case NORTH:
                return 2; // 180°
            case EAST:
                return 3; // 270°
            default:
                return 0;
        }
    }
}
