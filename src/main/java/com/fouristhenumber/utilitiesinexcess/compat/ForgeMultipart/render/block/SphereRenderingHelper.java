package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import codechicken.lib.vec.Vector3;
import codechicken.microblock.BlockMicroMaterial;
import codechicken.microblock.GrassMicroMaterial;
import codechicken.microblock.MicroMaterialRegistry;
import it.unimi.dsi.fastutil.Pair;

public class SphereRenderingHelper {

    // Verts are ordered as follows for easy UV mapping [latitude][longitude]
    static Vector3[][] verts;
    static Pair<Double, Double>[] UVs = new Pair[34];
    // Wil do a 2 part slerp. First part to generate the equitorial verts, then the second
    // part will generate the longitudinal verts. Have to do this because antipodal points break slerp.
    static {
        Vector3[] baseEquitorialVert = new Vector3[] { new Vector3(0, 0, 1), new Vector3(1, 0, 0),
            new Vector3(0, 0, -1), new Vector3(-1, 0, 0), };

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

        Vector3[] polarVerts = new Vector3[] { new Vector3(0, -1, 0), new Vector3(0, 1, 0) };

        // Slerp along longitude and assemble verts
        int latBands = 9; // 2 pole verts + 7 band verts
        int longBands = 16;

        // Note that we use longBAnds for latitude positions in parts of this algorithm and vice versa.
        // This is because longitudinal bands are located on specific latitudes.

        verts = new Vector3[latBands][longBands];
        for (int latPos = 0; latPos < longBands; latPos++) {
            verts[0][latPos] = polarVerts[0].copy()
                .multiply(.375);
            verts[latBands - 1][latPos] = polarVerts[1].copy()
                .multiply(.375);
        }

        // Bottom
        for (int latitude = 0; latitude < longBands; latitude++) {
            for (int longitude = 1; longitude < 4; longitude++) {
                double t = (double) longitude / 4;
                verts[longitude][latitude] = slerp(polarVerts[0].copy(), equitorialVerts[latitude].copy(), t)
                    .multiply(.375);
            }
        }

        // Equator
        for (int i = 0; i < equitorialVerts.length; i++) {
            verts[4][i] = equitorialVerts[i].copy()
                .multiply(.375);
        }

        // Top
        for (int latitude = 0; latitude < longBands; latitude++) {
            for (int longitude = 1; longitude < 4; longitude++) {
                double t = (double) longitude / 4;
                verts[longitude + 4][latitude] = slerp(equitorialVerts[latitude].copy(), polarVerts[1].copy(), t)
                    .multiply(.375);
            }
        }

        // Calculate UV points for the polar aligned faces
        // Calculations are between 0-1 for UVs
        // Assume center lies at U, V = 0.5, 0.5 and it's not included.

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 2; j++) {
                int side = i / 4;
                int subSideOrdinal = i % 4;
                double U;
                double V;
                switch (side) {
                    case (0): {
                        if (j == 0) {
                            U = 0.0;
                            V = (double) subSideOrdinal / 4;
                        } else {
                            U = 0.25;
                            V = 0.25 + (double) subSideOrdinal / 8;
                        }
                        break;
                    }
                    case (1): {
                        if (j == 0) {
                            U = (double) subSideOrdinal / 4;
                            V = 1.0;
                        } else {
                            U = 0.25 + (double) subSideOrdinal / 8;
                            V = 0.75;
                        }
                        break;
                    }
                    case (2): {
                        if (j == 0) {
                            U = 1.0;
                            V = 1.0 - ((double) subSideOrdinal / 4);
                        } else {
                            U = 0.75;
                            V = 0.75 - ((double) subSideOrdinal / 8);
                        }
                        break;
                    }
                    case (3): {
                        if (j == 0) {
                            U = 1.0 - ((double) subSideOrdinal / 4);
                            V = 0.0;
                        } else {
                            U = 0.75 - ((double) subSideOrdinal / 8);
                            V = 0.25;
                        }
                        break;
                    }
                    default: {
                        U = 0.0;
                        V = 0.0;
                    }
                }
                UVs[(i * 2) + j] = Pair.of(U, V);
            }

            // For ease of iteration we make a copy of 0 and 1 at 32 and 33 for coordinate wrapping.
            UVs[32] = UVs[0];
            UVs[33] = UVs[1];
        }
    }

    public static Vector3 slerp(Vector3 p0, Vector3 p1, double t) {
        double omega = Math.acos(p0.dotProduct(p1));
        return p0.multiply((Math.sin((1 - t) * omega)) / (Math.sin(omega)))
            .add(p1.multiply((Math.sin(omega * t) / (Math.sin(omega)))));
    }

    public static void RenderMicroMaterialSphere(Vector3 position, int pass, MicroMaterialRegistry.IMicroMaterial mat,
        World world) {
        double ox = position.x + 0.5;
        double oy = position.y + 0.5;
        double oz = position.z + 0.5;
        Tessellator tess = Tessellator.instance;
        if (mat instanceof GrassMicroMaterial gMat) {
            IIcon[] icons = gMat.icont().icons;
            int color = getBlockColor(gMat.block(), world, (int) position.x, (int) position.y, (int) position.z, pass);
            int brightness;
            if (pass != -1) {
                brightness = gMat.block()
                    .getMixedBrightnessForBlock(world, (int) position.x, (int) position.y, (int) position.z);
            } else {
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
        } else if (mat instanceof BlockMicroMaterial bMat) {
            IIcon[] icons = bMat.icont().icons;
            int color = getBlockColor(bMat.block(), world, (int) position.x, (int) position.y, (int) position.z, pass);
            int brightness;
            if (pass != -1) {
                brightness = bMat.block()
                    .getMixedBrightnessForBlock(world, (int) position.x, (int) position.y, (int) position.z);
            } else {
                brightness = 0xF000F0;
            }
            RenderSphere(tess, icons, ox, oy, oz, brightness, color);
        }
    }

    public static void RenderSphereTop(Tessellator tess, IIcon topIcon, double x, double y, double z, int brightness,
        int color) {
        double uWindow = topIcon.getMaxU() - topIcon.getMinU();
        double vWindow = topIcon.getMaxV() - topIcon.getMinV();
        double minU = topIcon.getMinU();
        double minV = topIcon.getMinV();

        int UVCounter = 0;
        int UVCounterMiddle = 1;
        for (int longitude = 6; longitude < 8; longitude++) {
            if (longitude == 7) {
                tess.draw(); // Empty tess buffer
                tess.startDrawing(GL11.GL_TRIANGLES); // STart with triangles.
            }
            for (int latitude = 0; latitude < 16; latitude++) {
                // using non axis aligned faces so we need special uv for each vertex.
                double u1, u2, u3, u4, v1, v2, v3, v4;

                // I know these look funny how they're assigned, but because of how
                // the UVs are ordered when they are generated. It works.
                if (longitude == 6) {
                    u1 = minU + UVs[UVCounter].first() * uWindow;
                    v1 = minV + UVs[UVCounter++].second() * vWindow;
                    u4 = minU + UVs[UVCounter].first() * uWindow;
                    v4 = minV + UVs[UVCounter++].second() * vWindow;
                    u2 = minU + UVs[UVCounter].first() * uWindow;
                    v2 = minV + UVs[UVCounter++].second() * vWindow;
                    u3 = minU + UVs[UVCounter].first() * uWindow;
                    v3 = minV + UVs[UVCounter++].second() * vWindow;
                    UVCounter -= 2;

                    int lon0 = longitude;
                    int lon1 = longitude + 1;
                    int lat0 = WrapNumber(0, 15, latitude + 2);
                    int lat1 = WrapNumber(0, 15, latitude + 3);

                    Vector3 vert0 = SphereRenderingHelper.verts[lon0][lat0].copy()
                        .add(x, y, z);
                    Vector3 vert1 = SphereRenderingHelper.verts[lon0][lat1].copy()
                        .add(x, y, z);
                    Vector3 vert2 = SphereRenderingHelper.verts[lon1][lat1].copy()
                        .add(x, y, z);
                    Vector3 vert3 = SphereRenderingHelper.verts[lon1][lat0].copy()
                        .add(x, y, z);

                    renderSphericFaceVanilla(
                        tess,
                        vert0,
                        vert1,
                        vert2,
                        vert3,
                        u1,
                        v1,
                        u2,
                        v2,
                        u3,
                        v3,
                        u4,
                        v4,
                        brightness,
                        color);
                } else {

                    u1 = minU + UVs[UVCounterMiddle].first() * uWindow;
                    v1 = minV + UVs[UVCounterMiddle].second() * vWindow;
                    UVCounterMiddle += 2;
                    u2 = minU + UVs[UVCounterMiddle].first() * uWindow;
                    v2 = minV + UVs[UVCounterMiddle].second() * vWindow;

                    u3 = minU + (0.5 * uWindow);
                    v3 = minV + (0.5 * vWindow);

                    int lon0 = longitude;
                    int lon1 = longitude + 1;
                    int lat0 = WrapNumber(0, 15, latitude + 2);
                    int lat1 = WrapNumber(0, 15, latitude + 3);

                    Vector3 vert0 = SphereRenderingHelper.verts[lon0][lat0].copy()
                        .add(x, y, z);
                    Vector3 vert1 = SphereRenderingHelper.verts[lon0][lat1].copy()
                        .add(x, y, z);
                    Vector3 vert2 = SphereRenderingHelper.verts[lon1][lat0].copy()
                        .add(x, y, z);

                    renderSphericFaceVanilla(tess, vert0, vert1, vert2, u1, v1, u2, v2, u3, v3, brightness, color);
                }
            }
        }
        tess.draw(); // Draw our triangles
        tess.startDrawingQuads(); // go back to drawing quads once done.
    }

    public static void RenderSphereBottom(Tessellator tess, IIcon bottomIcon, double x, double y, double z,
        int brightness, int color) {
        double uWindow = bottomIcon.getMaxU() - bottomIcon.getMinU();
        double vWindow = bottomIcon.getMaxV() - bottomIcon.getMinV();
        double minU = bottomIcon.getMinU();
        double minV = bottomIcon.getMinV();

        int UVCounter = 0;
        int UVCounterMiddle = 1;
        for (int longitude = 1; longitude > -1; longitude--) {
            if (longitude == 0) {
                tess.draw(); // Empty tess buffer
                tess.startDrawing(GL11.GL_TRIANGLES); // STart with triangles.
            }
            for (int latitude = 0; latitude < 16; latitude++) {
                // using non axis aligned faces so we need special uv for each vertex.
                double u1, u2, u3, u4, v1, v2, v3, v4;

                // I know these look funny how they're assigned, but because of how
                // the UVs are ordered when they are generated. It works.
                if (longitude == 1) {
                    u1 = minU + UVs[UVCounter].first() * uWindow;
                    v1 = minV + UVs[UVCounter++].second() * vWindow;
                    u4 = minU + UVs[UVCounter].first() * uWindow;
                    v4 = minV + UVs[UVCounter++].second() * vWindow;
                    u2 = minU + UVs[UVCounter].first() * uWindow;
                    v2 = minV + UVs[UVCounter++].second() * vWindow;
                    u3 = minU + UVs[UVCounter].first() * uWindow;
                    v3 = minV + UVs[UVCounter++].second() * vWindow;
                    UVCounter -= 2;

                    int lon0 = longitude + 1;
                    int lon1 = longitude;
                    int lat0 = WrapNumber(0, 15, latitude + 2);
                    int lat1 = WrapNumber(0, 15, latitude + 3);

                    Vector3 vert0 = SphereRenderingHelper.verts[lon0][lat0].copy()
                        .add(x, y, z);
                    Vector3 vert1 = SphereRenderingHelper.verts[lon0][lat1].copy()
                        .add(x, y, z);
                    Vector3 vert2 = SphereRenderingHelper.verts[lon1][lat1].copy()
                        .add(x, y, z);
                    Vector3 vert3 = SphereRenderingHelper.verts[lon1][lat0].copy()
                        .add(x, y, z);

                    renderSphericFaceVanilla(
                        tess,
                        vert0,
                        vert3,
                        vert2,
                        vert1,
                        u1,
                        v1,
                        u4,
                        v4,
                        u3,
                        v3,
                        u2,
                        v2,
                        brightness,
                        color);
                } else {
                    u1 = minU + UVs[UVCounterMiddle].first() * uWindow;
                    v1 = minV + UVs[UVCounterMiddle].second() * vWindow;
                    UVCounterMiddle += 2;
                    u2 = minU + UVs[UVCounterMiddle].first() * uWindow;
                    v2 = minV + UVs[UVCounterMiddle].second() * vWindow;

                    u3 = minU + (0.5 * uWindow);
                    v3 = minV + (0.5 * vWindow);

                    int lon0 = longitude + 1;
                    int lon1 = longitude;
                    int lat0 = WrapNumber(0, 15, latitude + 2);
                    int lat1 = WrapNumber(0, 15, latitude + 3);

                    Vector3 vert0 = SphereRenderingHelper.verts[lon0][lat0].copy()
                        .add(x, y, z);
                    Vector3 vert1 = SphereRenderingHelper.verts[lon0][lat1].copy()
                        .add(x, y, z);
                    Vector3 vert2 = SphereRenderingHelper.verts[lon1][lat0].copy()
                        .add(x, y, z);

                    renderSphericFaceVanilla(tess, vert0, vert2, vert1, u1, v1, u3, v3, u2, v2, brightness, color);
                }
            }
        }
        tess.draw(); // Draw our triangles
        tess.startDrawingQuads(); // go back to drawing quads once done.
    }

    public static void RenderSphereSides(Tessellator tess, IIcon[] sideIcons, double x, double y, double z,
        int brightness, int color) {
        for (int latitude = 0; latitude < 16; latitude++) {
            for (int longitude = 2; longitude < 6; longitude++) {
                int side = latitude / 4;
                switch (side) {
                    case (0): {
                        side = ForgeDirection.SOUTH.ordinal() - 2;
                        break;
                    }
                    case (1): {
                        side = ForgeDirection.EAST.ordinal() - 2;
                        break;
                    }
                    case (2): {
                        side -= 2;
                        break; // Side is already at north ordinal
                    }
                    case (3): {
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

                Vector3 vert0 = SphereRenderingHelper.verts[lon0][lat0].copy()
                    .add(x, y, z);
                Vector3 vert1 = SphereRenderingHelper.verts[lon0][lat1].copy()
                    .add(x, y, z);
                Vector3 vert2 = SphereRenderingHelper.verts[lon1][lat1].copy()
                    .add(x, y, z);
                Vector3 vert3 = SphereRenderingHelper.verts[lon1][lat0].copy()
                    .add(x, y, z);

                renderSphericFaceVanilla(tess, vert0, vert1, vert2, vert3, u1, v1, u2, v2, brightness, color);
            }
        }
    }

    // Most blocks can just use this
    public static void RenderSphere(Tessellator tess, IIcon[] icons, double x, double y, double z, int brightness,
        int color) {
        RenderSphereTop(tess, icons[1], x, y, z, brightness, color);
        RenderSphereSides(tess, Arrays.copyOfRange(icons, 2, icons.length), x, y, z, brightness, color);
        RenderSphereBottom(tess, icons[0], x, y, z, brightness, color);
    }

    public static int getBlockColor(Block block, World world, int x, int y, int z, int pass) {
        if (pass == -1) {
            return block.getBlockColor();
        } else {
            return block.colorMultiplier(world, x, y, z);
        }
    }

    public static void renderSphericFaceVanilla(Tessellator tess, Vector3 vert0, Vector3 vert1, Vector3 vert2,
        Vector3 vert3, double u1, double v1, double u2, double v2, int brightness, int color) {
        tess.setBrightness(brightness);

        Vector3 n = vert0.copy()
            .normalize();
        float shade = (float) (0.6 + 0.4 * Math.max(0, n.y));

        float cr = ((color >> 16) & 0xFF) / 255f;
        float cg = ((color >> 8) & 0xFF) / 255f;
        float cb = (color & 0xFF) / 255f;

        tess.setColorOpaque_F(cr * shade, cg * shade, cb * shade);
        tess.setNormal((float) n.x, (float) n.y, (float) n.z);

        tess.addVertexWithUV(vert0.x, vert0.y, vert0.z, u1, v2);
        tess.addVertexWithUV(vert1.x, vert1.y, vert1.z, u2, v2);
        tess.addVertexWithUV(vert2.x, vert2.y, vert2.z, u2, v1);
        tess.addVertexWithUV(vert3.x, vert3.y, vert3.z, u1, v1);
    }

    public static void renderSphericFaceVanilla(Tessellator tess, Vector3 vert0, Vector3 vert1, Vector3 vert2,
        Vector3 vert3, double u1, double v1, double u2, double v2, double u3, double v3, double u4, double v4,
        int brightness, int color) {
        tess.setBrightness(brightness);

        Vector3 n = vert0.copy()
            .normalize();
        float shade = (float) (0.6 + 0.4 * Math.max(0, n.y));

        float cr = ((color >> 16) & 0xFF) / 255f;
        float cg = ((color >> 8) & 0xFF) / 255f;
        float cb = (color & 0xFF) / 255f;

        tess.setColorOpaque_F(cr * shade, cg * shade, cb * shade);
        tess.setNormal((float) n.x, (float) n.y, (float) n.z);

        tess.addVertexWithUV(vert0.x, vert0.y, vert0.z, u1, v1);
        tess.addVertexWithUV(vert1.x, vert1.y, vert1.z, u2, v2);
        tess.addVertexWithUV(vert2.x, vert2.y, vert2.z, u3, v3);
        tess.addVertexWithUV(vert3.x, vert3.y, vert3.z, u4, v4);
    }

    public static void renderSphericFaceVanilla(Tessellator tess, Vector3 vert0, Vector3 vert1, Vector3 vert2,
        double u1, double v1, double u2, double v2, double u3, double v3, int brightness, int color) {
        tess.setBrightness(brightness);

        Vector3 n = vert0.copy()
            .normalize();
        float shade = (float) (0.6 + 0.4 * Math.max(0, n.y));

        float cr = ((color >> 16) & 0xFF) / 255f;
        float cg = ((color >> 8) & 0xFF) / 255f;
        float cb = (color & 0xFF) / 255f;

        tess.setColorOpaque_F(cr * shade, cg * shade, cb * shade);
        tess.setNormal((float) n.x, (float) n.y, (float) n.z);

        tess.addVertexWithUV(vert0.x, vert0.y, vert0.z, u1, v1);
        tess.addVertexWithUV(vert1.x, vert1.y, vert1.z, u2, v2);
        tess.addVertexWithUV(vert2.x, vert2.y, vert2.z, u3, v3);
    }

    public static int WrapNumber(int min, int max, int in) {
        if (in > max) {
            return in - max + min - 1;
        } else if (in < min) {
            return in - min + max + 1;
        }
        return in;
    }
}
