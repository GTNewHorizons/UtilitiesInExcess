package com.fouristhenumber.utilitiesinexcess.common.renderers.Multipart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.BlockMicroMaterial;
import codechicken.microblock.MaterialRenderHelper;
import codechicken.microblock.MicroMaterialRegistry;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class SphereRenderingHelper
{

    // Verts are ordered as follows for easy UV mapping [latitude][longitude]
    static Vector3[][] verts;

    // Wil do a 2 part slerp. First part to generate the equitorial verts, then the second
    // part will generate the longitudinal verts. Have to do this because antipodal points break slerp.
    static
    {
        Vector3[] baseEquitorialVert = new Vector3[]
        {
            new Vector3(0, 0, 1),
            new Vector3(1, 0, 0),
            new Vector3(0, 0, -1),
            new Vector3(-1, 0, 0),
        };

        // Slerp along equitorial latitudes
        Vector3[] equitorialVerts = new Vector3[16];
        for (int j = 0; j < 4; j++)
        {
            Vector3 p0 = baseEquitorialVert[j];
            Vector3 p1;
            if (j != 3)
            {
                p1 = baseEquitorialVert[j + 1];
            }
            else
            {
                p1 = baseEquitorialVert[0];
            }

            int offsetIndex = j * 4;
            equitorialVerts[offsetIndex] = p0.copy();
            for (int i = 1; i < 4; i++)
            {
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
        for (int latPos = 0; latPos < longBands; latPos++)
        {
            verts[0][latPos] = polarVerts[0].copy().multiply(.375);
            verts[latBands - 1][latPos] = polarVerts[1].copy().multiply(.375);
        }

        // Bottom
        for (int latitude = 0; latitude < longBands; latitude++)
        {
            for (int longitude = 1; longitude < 4; longitude++)
            {
                double t = (double) longitude / 4;
                verts[longitude][latitude] = slerp(polarVerts[0].copy(), equitorialVerts[latitude].copy(),  t).multiply(.375);
            }
        }

        // Equator
        for (int i = 0; i < equitorialVerts.length; i++)
        {
            verts[4][i] = equitorialVerts[i].copy().multiply(.375);
        }

        // Top
        for (int latitude = 0; latitude < longBands; latitude++)
        {
            for (int longitude = 1; longitude < 4; longitude++)
            {
                double t = (double) longitude / 4;
                verts[longitude + 4][latitude] = slerp(equitorialVerts[latitude].copy(), polarVerts[1].copy(), t).multiply(.375);
            }
        }
    }

    public static Vector3 slerp(Vector3 p0, Vector3 p1, double t)
    {
        double omega = Math.acos(p0.dotProduct(p1));
        return p0.multiply((Math.sin((1 - t) * omega))/(Math.sin(omega)))
               .add(p1.multiply((Math.sin(omega * t)/(Math.sin(omega)))));
    }

    private static final ThreadLocal<SphereFace> face = ThreadLocal.withInitial(SphereFace::new);

    public static void RenderMicroMaterialSphere(Vector3 position, int pass, MicroMaterialRegistry.IMicroMaterial mat, World world)
    {
        Tessellator tess = Tessellator.instance;

        double ox = position.x + 0.5;
        double oy = position.y + 0.5;
        double oz = position.z + 0.5;

        if (mat instanceof BlockMicroMaterial bMat) {

            IIcon icon = bMat.icont().icons[0];
            int brightness;
            if (pass != -1)
            {
                brightness = bMat.block().getMixedBrightnessForBlock(world, (int)position.x, (int)position.y, (int)position.z);
            }
            else
            {
                brightness = 0xF000F0;
            }

            for (int latitude = 0; latitude < 16; latitude++) {
                for (int longitude = 0; longitude < 8; longitude++) {
                    if (longitude < 6 && longitude > 1) {

                        int lon0 = longitude;
                        int lon1 = longitude + 1;
                        int lat0 = latitude;
                        int lat1 = WrapNumber(0, 15, latitude + 1);

                        Vector3 v0 = SphereRenderingHelper.verts[lon0][lat0].copy().add(ox, oy, oz);
                        Vector3 v1 = SphereRenderingHelper.verts[lon0][lat1].copy().add(ox, oy, oz);
                        Vector3 v2 = SphereRenderingHelper.verts[lon1][lat1].copy().add(ox, oy, oz);
                        Vector3 v3 = SphereRenderingHelper.verts[lon1][lat0].copy().add(ox, oy, oz);

                        // Full side texture for now
                        double u1 = icon.getMinU();
                        double u2 = icon.getMaxU();
                        double v1u = icon.getMinV();
                        double v2u = icon.getMaxV();

                        renderSphericFaceVanilla(
                            tess,
                            v0, v1, v2, v3,
                            u1, v1u,
                            u2, v2u,
                            brightness
                        );
                    }
                }
            }
        }
    }

    public static void renderSphericFaceVanilla(
        Tessellator tess,
        Vector3 v0, Vector3 v1, Vector3 v2, Vector3 v3,
        double u1, double v1u,
        double u2, double v2u,
        int brightness)
    {
        tess.setBrightness(brightness);

        // It's super fucking bright if we don't shade a bit
        Vector3 n = v0.copy().normalize();
        float shade = (float)(0.6 + 0.4 * Math.max(0, n.y));
        tess.setColorOpaque_F(shade, shade, shade);

        tess.setNormal((float)n.x, (float)n.y, (float)n.z);

        tess.addVertexWithUV(v0.x, v0.y, v0.z, u1, v1u);
        tess.addVertexWithUV(v1.x, v1.y, v1.z, u1, v2u);
        tess.addVertexWithUV(v2.x, v2.y, v2.z, u2, v2u);
        tess.addVertexWithUV(v3.x, v3.y, v3.z, u2, v1u);
    }

    public static void RenderSphericFace(Vector3 position, int pass, MicroMaterialRegistry.IMicroMaterial mat)
    {
        if (mat instanceof BlockMicroMaterial bMat) {
            MaterialRenderHelper
                .start(position, pass, bMat.icont())
                .blockColour(bMat.getColour(pass))
                .lighting()
                .blockAndMeta(bMat.block(), bMat.meta())
                .render();
        }
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
