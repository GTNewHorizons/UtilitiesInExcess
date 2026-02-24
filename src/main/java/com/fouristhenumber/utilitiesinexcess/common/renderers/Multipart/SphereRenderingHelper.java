package com.fouristhenumber.utilitiesinexcess.common.renderers.Multipart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.BlockMicroMaterial;
import codechicken.microblock.GrassMicroMaterial;
import codechicken.microblock.MaterialRenderHelper;
import codechicken.microblock.MicroMaterialRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Arrays;

public class SphereRenderingHelper {

    // Verts are ordered as follows for easy UV mapping [latitude][longitude]
    static Vector3[][] verts;

    // Wil do a 2 part slerp. First part to generate the equitorial verts, then the second
    // part will generate the longitudinal verts. Have to do this because antipodal points break slerp.
    static {
        Vector3[] baseEquitorialVert = new Vector3[]
            {
                new Vector3(0, 0, 1),
                new Vector3(1, 0, 0),
                new Vector3(0, 0, -1),
                new Vector3(-1, 0, 0),
            };

        // Slerp along equitorial latitudes
        Vector3[] equitorialVerts = new Vector3[16];
        for (int j = 0; j < 4; j++) {
            Vector3 p0 = baseEquitorialVert[j];
            Vector3 p1;
            if (j != 3) {
                p1 = baseEquitorialVert[j + 1];
            } else {
                p1 = baseEquitorialVert[0];
            }

            int offsetIndex = j * 4;
            equitorialVerts[offsetIndex] = p0.copy();
            for (int i = 1; i < 4; i++) {
                double t = (double) i / 4;
                equitorialVerts[offsetIndex + i] = slerp(p0.copy(), p1.copy(), t);
            }
        }

        Vector3[] polarVerts = new Vector3[]
            {
                new Vector3(0, -1, 0),
                new Vector3(0, 1, 0)
            };

        // Slerp along longitude and assemble verts
        int latBands = 9; // 2 pole verts + 7 band verts
        int longBands = 16;

        // Note that we use longBAnds for latitude positions in parts of this algorithm and vice versa.
        // This is because longitudinal bands are located on specific latitudes.

        verts = new Vector3[latBands][longBands];
        for (int latPos = 0; latPos < longBands; latPos++) {
            verts[0][latPos] = polarVerts[0].copy().multiply(.375);
            verts[latBands - 1][latPos] = polarVerts[1].copy().multiply(.375);
        }

        // Bottom
        for (int latitude = 0; latitude < longBands; latitude++) {
            for (int longitude = 1; longitude < 4; longitude++) {
                double t = (double) longitude / 4;
                verts[longitude][latitude] = slerp(polarVerts[0].copy(), equitorialVerts[latitude].copy(), t).multiply(.375);
            }
        }

        // Equator
        for (int i = 0; i < equitorialVerts.length; i++) {
            verts[4][i] = equitorialVerts[i].copy().multiply(.375);
        }

        // Top
        for (int latitude = 0; latitude < longBands; latitude++) {
            for (int longitude = 1; longitude < 4; longitude++) {
                double t = (double) longitude / 4;
                verts[longitude + 4][latitude] = slerp(equitorialVerts[latitude].copy(), polarVerts[1].copy(), t).multiply(.375);
            }
        }
    }

    public static Vector3 slerp(Vector3 p0, Vector3 p1, double t) {
        double omega = Math.acos(p0.dotProduct(p1));
        return p0.multiply((Math.sin((1 - t) * omega)) / (Math.sin(omega)))
            .add(p1.multiply((Math.sin(omega * t) / (Math.sin(omega)))));
    }

    public static void RenderMicroMaterialSphere(Vector3 position, int pass, MicroMaterialRegistry.IMicroMaterial mat, World world) {
        double ox = position.x + 0.5;
        double oy = position.y + 0.5;
        double oz = position.z + 0.5;
        Tessellator tess = Tessellator.instance;
        if (mat instanceof GrassMicroMaterial gMat) {
            IIcon[] icons = gMat.icont().icons;
            int color = getBlockColor(gMat.block(), world, (int) position.x, (int) position.y, (int) position.z, pass);
            int brightness;
            if (pass != -1)
            {
                brightness = gMat.block().getMixedBrightnessForBlock(world, (int) position.x, (int) position.y, (int) position.z);
            }
            else
            {
                brightness = 0xF000F0;
            }
            // Base
            RenderSphereBottom(tess, icons[0], ox, oy, oz, brightness, 0xFFFFFFFF);
            RenderSphereSides(tess, Arrays.copyOfRange(icons, 2, icons.length), ox, oy, oz, brightness, 0xFFFFFFFF);

            // Do biome overlays
            IIcon[] overlayArr = new IIcon[4];
            Arrays.fill(overlayArr, gMat.sideIconT().icon);
            RenderSphereSides(tess, overlayArr, ox, oy, oz, brightness, color);
            RenderSphereTop(tess, icons[1], ox, oy, oz, brightness, color);
        }
        else if (mat instanceof BlockMicroMaterial bMat)
        {
            IIcon[] icons = bMat.icont().icons;
            int color = getBlockColor(bMat.block(), world, (int) position.x, (int) position.y, (int) position.z, pass);
            int brightness;
            if (pass != -1)
            {
                brightness = bMat.block().getMixedBrightnessForBlock(world, (int) position.x, (int) position.y, (int) position.z);
            }
            else
            {
                brightness = 0xF000F0;
            }
            RenderSphere(tess, icons, ox, oy, oz, brightness, color);
        }
    }

    public static void RenderSphereTop(Tessellator tess, IIcon topIcon, double x, double y, double z, int brightness, int color) {
        double uWindow = topIcon.getMaxU() - topIcon.getMinU();
        double vWindow = topIcon.getMaxV() - topIcon.getMinV();

        for (int latitude = 0; latitude < 16; latitude++) {
            for (int longitude = 6; longitude < 8; longitude++) {

                // Take bottom left corner of the block tex to be lat == 2 and long = 6
                // Take top right corner of the block tex to be lat == 10 and long = 6

                // Relative side
                int side = WrapNumber(0, 3, ((latitude + 2) / 4) - 1);

                int k = latitude & 3;
                int min = (k + 2) & 3;
                int max = min + 1;

                double u1 = topIcon.getMinU();
                double u2 = topIcon.getMaxU();
                double v1 = topIcon.getMinV();
                double v2 = topIcon.getMaxV();
                switch(side)
                {
                    case(0): // bottom
                    {
                        u1 = topIcon.getMinU() + (uWindow * (double)(longitude - 6) / 4);
                        u2 = topIcon.getMinU() + (uWindow * (double)(longitude - 5) / 4);
                        v1 = topIcon.getMinV() + (vWindow * (double)(min) / 4);
                        v2 = topIcon.getMinV() + (vWindow * (double)(max) / 4);
                        break;
                    }
                    case(1): // right
                    {
                        u1 = topIcon.getMinU() + (uWindow * ((double) 1 - (double)(min) / 4));
                        u2 = topIcon.getMinU() + (uWindow * ((double) 1 - (double)(max) / 4));
                        v1 = topIcon.getMinV() + (vWindow * (double)(longitude - 5) / 4);
                        v2 = topIcon.getMinV() + (vWindow * (double)(longitude - 6) / 4);
                        break;
                    }
                    case(2): // top
                    {
                        u1 = topIcon.getMinU() + (uWindow *)
                        break;
                    }
                    case(3): // left
                    {
                        break;
                    }
                }

                int lon0 = longitude;
                int lon1 = longitude + 1;
                int lat0 = latitude;
                int lat1 = WrapNumber(0, 15, latitude + 1);

                Vector3 vert0 = SphereRenderingHelper.verts[lon0][lat0].copy().add(x, y, z);
                Vector3 vert1 = SphereRenderingHelper.verts[lon0][lat1].copy().add(x, y, z);
                Vector3 vert2 = SphereRenderingHelper.verts[lon1][lat1].copy().add(x, y, z);
                Vector3 vert3 = SphereRenderingHelper.verts[lon1][lat0].copy().add(x, y, z);

                renderSphericFaceVanilla(
                    tess,
                    vert0, vert1, vert2, vert3,
                    u1, v1,
                    u2, v2,
                    brightness,
                    color
                );
            }
        }
    }

    public static void RenderSphereBottom(Tessellator tess, IIcon bottomIcon, double x, double y, double z, int brightness, int color)
    {
        for (int latitude = 0; latitude < 16; latitude++) {
            for (int longitude = 0; longitude < 2; longitude++) {
                double u1 = bottomIcon.getMinU();
                double u2 = bottomIcon.getMaxU();
                double v1 = bottomIcon.getMinV();
                double v2 = bottomIcon.getMaxV();

                int lon0 = longitude;
                int lon1 = longitude + 1;
                int lat0 = latitude;
                int lat1 = WrapNumber(0, 15, latitude + 1);

                Vector3 vert0 = SphereRenderingHelper.verts[lon0][lat0].copy().add(x, y, z);
                Vector3 vert1 = SphereRenderingHelper.verts[lon0][lat1].copy().add(x, y, z);
                Vector3 vert2 = SphereRenderingHelper.verts[lon1][lat1].copy().add(x, y, z);
                Vector3 vert3 = SphereRenderingHelper.verts[lon1][lat0].copy().add(x, y, z);

                renderSphericFaceVanilla(
                    tess,
                    vert0, vert1, vert2, vert3,
                    u1, v1,
                    u2, v2,
                    brightness,
                    color
                );
            }
        }
    }

    public static void RenderSphereSides(Tessellator tess, IIcon[] sideIcons, double x, double y, double z, int brightness, int color) {
        for (int latitude = 0; latitude < 16; latitude++) {
            for (int longitude = 2; longitude < 6; longitude++) {
                int side = latitude / 4;
                switch(side)
                {
                    case(0):
                    {
                        side = ForgeDirection.SOUTH.ordinal() - 2;
                        break;
                    }
                    case(1):
                    {
                        side = ForgeDirection.EAST.ordinal() - 2;
                        break;
                    }
                    case(2):
                    {
                        side -= 2;
                        break; // Side is already at north ordinal
                    }
                    case(3):
                    {
                        side = ForgeDirection.WEST.ordinal() - 2;
                        break;
                    }
                }

                double uWindow = sideIcons[side].getMaxU() - sideIcons[side].getMinU();
                double vWindow = sideIcons[side].getMaxV() - sideIcons[side].getMinV();

                int k = latitude & 3;
                int min = (k + 2) & 3;
                int max = min + 1;

                double u1 = sideIcons[side].getMinU() + (uWindow * ((double) min / 4));
                double u2 = sideIcons[side].getMinU() + (uWindow * ((double) max / 4));
                double v1 = sideIcons[side].getMaxV() - (vWindow * ((double) (longitude - 1) / 4));
                double v2 = sideIcons[side].getMaxV() - (vWindow * ((double) (longitude - 2) / 4));

                int lon0 = longitude;
                int lon1 = longitude + 1;
                int lat0 = latitude;
                int lat1 = WrapNumber(0, 15, latitude + 1);

                Vector3 vert0 = SphereRenderingHelper.verts[lon0][lat0].copy().add(x, y, z);
                Vector3 vert1 = SphereRenderingHelper.verts[lon0][lat1].copy().add(x, y, z);
                Vector3 vert2 = SphereRenderingHelper.verts[lon1][lat1].copy().add(x, y, z);
                Vector3 vert3 = SphereRenderingHelper.verts[lon1][lat0].copy().add(x, y, z);

                renderSphericFaceVanilla(
                    tess,
                    vert0, vert1, vert2, vert3,
                    u1, v1,
                    u2, v2,
                    brightness,
                    color
                );
            }
        }
    }

    // Most blocks can just use this
    public static void RenderSphere(Tessellator tess, IIcon[] icons, double x, double y, double z, int brightness, int color)
    {
        RenderSphereTop(tess, icons[1], x, y, z, brightness, color);
        RenderSphereSides(tess, Arrays.copyOfRange(icons, 2, icons.length), x, y, z, brightness, color);
        RenderSphereBottom(tess, icons[0], x, y, z, brightness, color);
    }

    public static int getBlockColor(Block block, World world, int x, int y, int z, int pass)
    {
        if (pass == -1)
        {
            return block.getBlockColor();
        }
        else
        {
            return block.colorMultiplier(world, x, y, z);
        }
    }

    public static void renderSphericFaceVanilla(
        Tessellator tess,
        Vector3 v0, Vector3 v1, Vector3 v2, Vector3 v3,
        double u1, double v1u,
        double u2, double v2u,
        int brightness,
        int color)
    {
        tess.setBrightness(brightness);

        Vector3 n = v0.copy().normalize();
        float shade = (float)(0.6 + 0.4 * Math.max(0, n.y));

        float cr = ((color >> 16) & 0xFF) / 255f;
        float cg = ((color >> 8) & 0xFF) / 255f;
        float cb = (color & 0xFF) / 255f;

        tess.setColorOpaque_F(
            cr * shade,
            cg * shade,
            cb * shade
        );
        tess.setNormal((float)n.x, (float)n.y, (float)n.z);

        tess.addVertexWithUV(v0.x, v0.y, v0.z, u1, v2u);
        tess.addVertexWithUV(v1.x, v1.y, v1.z, u2, v2u);
        tess.addVertexWithUV(v2.x, v2.y, v2.z, u2, v1u);
        tess.addVertexWithUV(v3.x, v3.y, v3.z, u1, v1u);
    }

    public static int WrapNumber(int min, int max, int in)
    {
        if (in > max)
        {
            return in - max + min - 1;
        }
        else if (in < min)
        {
            return in - min + max + 1;
        }
        return in;
    }
}
