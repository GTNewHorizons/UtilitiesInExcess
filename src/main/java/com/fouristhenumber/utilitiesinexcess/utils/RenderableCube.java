package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

public class RenderableCube {

    public double minX, minY, minZ, maxX, maxY, maxZ;
    public float[][] uv;

    public RenderableCube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float[][] uv) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.uv = uv;
    }

    public void draw(Tessellator t, double x, double y, double z, float textureSize, boolean applyBlockShading) {
        draw(t, x, y, z, 0, 1, 0, 1, textureSize, applyBlockShading);
    }

    public void draw(Tessellator t, double x, double y, double z, IIcon icon, float textureSize,
        boolean applyBlockShading) {
        draw(
            t,
            x,
            y,
            z,
            icon.getMinU(),
            icon.getMaxU(),
            icon.getMinV(),
            icon.getMaxV(),
            textureSize,
            applyBlockShading);
    }

    public void draw(Tessellator t, double x, double y, double z, float minU, float maxU, float minV, float maxV,
        float textureSize, boolean applyBlockShading) {
        double X1 = x + minX, Y1 = y + minY, Z1 = z + minZ;
        double X2 = x + maxX, Y2 = y + maxY, Z2 = z + maxZ;

        float iconWidth = maxU - minU;
        float iconHeight = maxV - minV;

        float[][] uv = new float[6][4];

        // Convert pixel-based UVs to their actual position on the atlas
        for (int f = 0; f < 6; f++) {
            float u0 = minU + (this.uv[f][0] / textureSize) * iconWidth;
            float v0 = minV + (this.uv[f][1] / textureSize) * iconHeight;
            float u1 = minU + (this.uv[f][2] / textureSize) * iconWidth;
            float v1 = minV + (this.uv[f][3] / textureSize) * iconHeight;
            uv[f] = new float[] { u0, v0, u1, v1 };
        }

        float[] topUV = uv[0];
        float[] bottomUV = uv[1];
        float[] northUV = uv[2];
        float[] southUV = uv[3];
        float[] westUV = uv[4];
        float[] eastUV = uv[5];

        // +Y
        // These calls are to manually recreate the side brightness that minecraft applies to each face
        if (applyBlockShading) t.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        t.setNormal(0, 1, 0);
        t.addVertexWithUV(X1, Y2, Z2, topUV[0], topUV[1]);
        t.addVertexWithUV(X2, Y2, Z2, topUV[2], topUV[1]);
        t.addVertexWithUV(X2, Y2, Z1, topUV[2], topUV[3]);
        t.addVertexWithUV(X1, Y2, Z1, topUV[0], topUV[3]);

        // -Y
        if (applyBlockShading) t.setColorOpaque_F(0.5F, 0.5F, 0.5F);
        t.setNormal(0, -1, 0);
        t.addVertexWithUV(X1, Y1, Z1, bottomUV[0], bottomUV[3]);
        t.addVertexWithUV(X2, Y1, Z1, bottomUV[2], bottomUV[3]);
        t.addVertexWithUV(X2, Y1, Z2, bottomUV[2], bottomUV[1]);
        t.addVertexWithUV(X1, Y1, Z2, bottomUV[0], bottomUV[1]);

        // +Z
        if (applyBlockShading) t.setColorOpaque_F(0.8F, 0.8F, 0.8F);
        t.setNormal(0, 0, 1);
        t.addVertexWithUV(X2, Y2, Z2, northUV[0], northUV[1]);
        t.addVertexWithUV(X1, Y2, Z2, northUV[2], northUV[1]);
        t.addVertexWithUV(X1, Y1, Z2, northUV[2], northUV[3]);
        t.addVertexWithUV(X2, Y1, Z2, northUV[0], northUV[3]);

        // -Z
        if (applyBlockShading) t.setColorOpaque_F(0.8F, 0.8F, 0.8F);
        t.setNormal(0, 0, -1);
        t.addVertexWithUV(X2, Y1, Z1, southUV[0], southUV[3]);
        t.addVertexWithUV(X1, Y1, Z1, southUV[2], southUV[3]);
        t.addVertexWithUV(X1, Y2, Z1, southUV[2], southUV[1]);
        t.addVertexWithUV(X2, Y2, Z1, southUV[0], southUV[1]);

        // +X
        if (applyBlockShading) t.setColorOpaque_F(0.6F, 0.6F, 0.6F);
        t.setNormal(1, 0, 0);
        t.addVertexWithUV(X2, Y1, Z2, westUV[2], westUV[3]);
        t.addVertexWithUV(X2, Y1, Z1, westUV[0], westUV[3]);
        t.addVertexWithUV(X2, Y2, Z1, westUV[0], westUV[1]);
        t.addVertexWithUV(X2, Y2, Z2, westUV[2], westUV[1]);

        // -X
        if (applyBlockShading) t.setColorOpaque_F(0.6F, 0.6F, 0.6F);
        t.setNormal(-1, 0, 0);
        t.addVertexWithUV(X1, Y1, Z1, eastUV[0], eastUV[3]);
        t.addVertexWithUV(X1, Y1, Z2, eastUV[2], eastUV[3]);
        t.addVertexWithUV(X1, Y2, Z2, eastUV[2], eastUV[1]);
        t.addVertexWithUV(X1, Y2, Z1, eastUV[0], eastUV[1]);
    }
}
