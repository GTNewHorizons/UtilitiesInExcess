package com.fouristhenumber.utilitiesinexcess.common.renderers;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.spikeRenderID;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class SpikeRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        Tessellator t = Tessellator.instance;
        IIcon icon = renderer.getBlockIcon(block);
        makeAllCubes(t, x, y, z, icon);
        return true;
    }

    @Override
    public void renderInventoryBlock(Block block, int meta, int modelId, RenderBlocks renderer) {
        Tessellator t = Tessellator.instance;
        IIcon icon = renderer.getBlockIcon(block);
        t.startDrawingQuads();
        makeAllCubes(t, 0, 0, 0, icon);
        t.draw();
    }

    private void makeAllCubes(Tessellator t, int x, int y, int z, IIcon icon) {
        // base
        addCube(
            t,
            x,
            y,
            z,
            icon,
            new Cube(
                0,
                0,
                0,
                1F,
                0.0625F,
                1F,
                new float[][] { { 16, 0, 31, 15 }, { 32, 0, 47, 15 }, { 0, 16, 15, 16 }, { 0, 16, 15, 16 },
                    { 0, 16, 15, 16 }, { 0, 16, 15, 16 } }));

        // layer 2 plates

        float[][] layer2uv = new float[][] { { 6, 17, 11, 22 }, { 12, 17, 17, 22 }, { 0, 23, 5, 23 }, { 6, 23, 11, 23 },
            { 12, 23, 17, 23 }, { 18, 23, 23, 23 } };

        addCube(t, x, y, z, icon, new Cube(0.0625F, 0.0625F, 0.0625F, 0.4375F, 0.125F, 0.4375F, layer2uv));

        addCube(t, x, y, z, icon, new Cube(0.5625F, 0.0625F, 0.0625F, 0.9375F, 0.125F, 0.4375F, layer2uv));

        addCube(t, x, y, z, icon, new Cube(0.0625F, 0.0625F, 0.5625F, 0.4375F, 0.125F, 0.9375F, layer2uv));

        addCube(t, x, y, z, icon, new Cube(0.5625F, 0.0625F, 0.5625F, 0.9375F, 0.125F, 0.9375F, layer2uv));

        // layer 3 plates

        float[][] layer3uv = new float[][] { { 28, 18, 31, 21 }, { 32, 18, 35, 21 }, { 24, 22, 27, 23 },
            { 28, 22, 31, 23 }, { 32, 22, 35, 23 }, { 36, 22, 39, 23 } };

        addCube(t, x, y, z, icon, new Cube(0.125F, 0.125F, 0.125F, 0.375F, 0.25F, 0.375F, layer3uv));

        addCube(t, x, y, z, icon, new Cube(0.625F, 0.125F, 0.125F, 0.875F, 0.25F, 0.375F, layer3uv));

        addCube(t, x, y, z, icon, new Cube(0.125F, 0.125F, 0.625F, 0.375F, 0.25F, 0.875F, layer3uv));

        addCube(t, x, y, z, icon, new Cube(0.625F, 0.125F, 0.625F, 0.875F, 0.25F, 0.875F, layer3uv));

        // layer 4 plates

        float[][] layer4uv = new float[][] { { 2, 0, 3, 1 }, { 4, 0, 5, 1 }, { 0, 2, 1, 4 }, { 2, 2, 3, 4 },
            { 4, 2, 5, 4 }, { 6, 2, 7, 4 } };

        addCube(t, x, y, z, icon, new Cube(0.1875F, 0.25F, 0.1875F, 0.3125F, 0.4375F, 0.3125F, layer4uv));

        addCube(t, x, y, z, icon, new Cube(0.6875F, 0.25F, 0.1875F, 0.8125F, 0.4375F, 0.3125F, layer4uv));

        addCube(t, x, y, z, icon, new Cube(0.1875F, 0.25F, 0.6875F, 0.3125F, 0.4375F, 0.8125F, layer4uv));

        addCube(t, x, y, z, icon, new Cube(0.6875F, 0.25F, 0.6875F, 0.8125F, 0.4375F, 0.8125F, layer4uv));
    }

    static final int TEXTURE_SIZE = 64;

    public static void addCube(Tessellator t, double x, double y, double z, IIcon icon, Cube c) {
        double X1 = x + c.minX, Y1 = y + c.minY, Z1 = z + c.minZ;
        double X2 = x + c.maxX, Y2 = y + c.maxY, Z2 = z + c.maxZ;

        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();

        float iconWidth = maxU - minU;
        float iconHeight = maxV - minV;

        float[][] uv = new float[6][4];

        // Convert pixel-based UVs to their actual position on the atlas
        for (int f = 0; f < 6; f++) {
            float u0 = minU + (c.uv[f][0] / TEXTURE_SIZE) * iconWidth;
            float v0 = minV + (c.uv[f][1] / TEXTURE_SIZE) * iconHeight;
            float u1 = minU + (c.uv[f][2] / TEXTURE_SIZE) * iconWidth;
            float v1 = minV + (c.uv[f][3] / TEXTURE_SIZE) * iconHeight;
            uv[f] = new float[] { u0, v0, u1, v1 };
        }

        float[] topUV = uv[0];
        float[] bottomUV = uv[1];
        float[] northUV = uv[2];
        float[] southUV = uv[3];
        float[] westUV = uv[4];
        float[] eastUV = uv[5];

        // +Y
        t.setNormal(0, 1, 0);
        // These calls are to manually recreate the side brightness that minecraft applies to each face
        t.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        t.addVertexWithUV(X1, Y2, Z2, topUV[0], topUV[1]);
        t.addVertexWithUV(X2, Y2, Z2, topUV[2], topUV[1]);
        t.addVertexWithUV(X2, Y2, Z1, topUV[2], topUV[3]);
        t.addVertexWithUV(X1, Y2, Z1, topUV[0], topUV[3]);

        // -Y
        t.setNormal(0, -1, 0);
        t.setColorOpaque_F(0.5F, 0.5F, 0.5F);
        t.addVertexWithUV(X1, Y1, Z1, bottomUV[0], bottomUV[3]);
        t.addVertexWithUV(X2, Y1, Z1, bottomUV[2], bottomUV[3]);
        t.addVertexWithUV(X2, Y1, Z2, bottomUV[2], bottomUV[1]);
        t.addVertexWithUV(X1, Y1, Z2, bottomUV[0], bottomUV[1]);

        // +Z
        t.setNormal(0, 0, 1);
        t.setColorOpaque_F(0.8F, 0.8F, 0.8F);
        t.addVertexWithUV(X1, Y1, Z2, northUV[0], northUV[1]);
        t.addVertexWithUV(X2, Y1, Z2, northUV[2], northUV[1]);
        t.addVertexWithUV(X2, Y2, Z2, northUV[2], northUV[3]);
        t.addVertexWithUV(X1, Y2, Z2, northUV[0], northUV[3]);

        // -Z
        t.setColorOpaque_F(0.8F, 0.8F, 0.8F);
        t.setNormal(0, 0, -1);
        t.addVertexWithUV(X1, Y2, Z1, southUV[0], southUV[3]);
        t.addVertexWithUV(X2, Y2, Z1, southUV[2], southUV[3]);
        t.addVertexWithUV(X2, Y1, Z1, southUV[2], southUV[1]);
        t.addVertexWithUV(X1, Y1, Z1, southUV[0], southUV[1]);

        // +X
        t.setColorOpaque_F(0.6F, 0.6F, 0.6F);
        t.setNormal(1, 0, 0);
        t.addVertexWithUV(X2, Y1, Z2, westUV[0], westUV[1]);
        t.addVertexWithUV(X2, Y1, Z1, westUV[2], westUV[1]);
        t.addVertexWithUV(X2, Y2, Z1, westUV[2], westUV[3]);
        t.addVertexWithUV(X2, Y2, Z2, westUV[0], westUV[3]);

        // -X
        t.setColorOpaque_F(0.6F, 0.6F, 0.6F);
        t.setNormal(-1, 0, 0);
        t.addVertexWithUV(X1, Y1, Z1, eastUV[0], eastUV[3]);
        t.addVertexWithUV(X1, Y1, Z2, eastUV[2], eastUV[3]);
        t.addVertexWithUV(X1, Y2, Z2, eastUV[2], eastUV[1]);
        t.addVertexWithUV(X1, Y2, Z1, eastUV[0], eastUV[1]);
    }

    public static class Cube {

        public double minX, minY, minZ, maxX, maxY, maxZ;
        public float[][] uv;

        public Cube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float[][] uv) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
            this.uv = uv;
        }
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return spikeRenderID;
    }
}
