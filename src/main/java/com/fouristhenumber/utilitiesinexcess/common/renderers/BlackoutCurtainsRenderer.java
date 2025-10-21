package com.fouristhenumber.utilitiesinexcess.common.renderers;

import static net.minecraftforge.common.util.ForgeDirection.*;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.joml.Vector2d;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockBlackoutCurtains;
import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

@ThreadSafeISBRH(perThread = false)
public class BlackoutCurtainsRenderer implements ISimpleBlockRenderingHandler {

    private void chain4(Tessellator tess, IIcon tex, double y, Vector2d c0, Vector2d c1, Vector2d c2, Vector2d c3) {
        makeVerticalQuad(tess, tex, y, c0, c1);
        makeVerticalQuad(tess, tex, y, c1, c2);
        makeVerticalQuad(tess, tex, y, c2, c3);
        // makeVerticalQuad(tess, tex, y, c3, c0); save on vertices that should never be visible anyway
    }

    private void makeVerticalQuad(Tessellator tess, IIcon tex, double y1, Vector2d c1, Vector2d c2) {
        double x1 = c1.x;
        double z1 = c1.y;
        double x2 = c2.x;
        double z2 = c2.y;
        double y2 = y1 + 1;

        double d1, d2;
        if (x1 != x2) {
            d1 = x1;
            d2 = x2;
        } else {
            d1 = z1;
            d2 = z2;
        }

        double b1, b2;
        if (d1 > d2) {
            b1 = Math.ceil(d1) - d1;
            b2 = 1 - d2 + Math.floor(d2);
        } else {
            b1 = d1 - Math.floor(d1);
            b2 = 1 + d2 - Math.ceil(d2);
        }

        double u1 = tex.getInterpolatedU(16d * b1);
        double u2 = tex.getInterpolatedU(16d * b2);

        tess.addVertexWithUV(x1, y2, z1, u1, tex.getMinV());
        tess.addVertexWithUV(x1, y1, z1, u1, tex.getMaxV());
        tess.addVertexWithUV(x2, y1, z2, u2, tex.getMaxV());
        tess.addVertexWithUV(x2, y2, z2, u2, tex.getMinV());
    }

    private void drawFloorCeil(Tessellator tess, IIcon tex, double y, Vector2d c1, Vector2d c2, boolean floor,
        boolean ceil) {
        if (floor) {
            makeHorizontalQuad(tess, tex, y, c1.x, c2.x, c1.y, c2.y);
        }
        if (ceil) {
            makeHorizontalQuad(tess, tex, y + 1, c1.x, c2.x, c2.y, c1.y); // winding order swap
        }
    }

    private void makeHorizontalQuad(Tessellator tess, IIcon tex, double y, double x1, double x2, double z1, double z2) {
        double u1 = tex.getInterpolatedU(16d * (x1 - Math.floor(x1)));
        double u2 = tex.getInterpolatedU(16d * (1 + x2 - Math.ceil(x2)));
        double v1 = tex.getInterpolatedV(16d * (z1 - Math.floor(z1)));
        double v2 = tex.getInterpolatedV(16d * (1 + z2 - Math.ceil(z2)));

        tess.addVertexWithUV(x1, y, z1, u1, v1);
        tess.addVertexWithUV(x1, y, z2, u1, v2);
        tess.addVertexWithUV(x2, y, z2, u2, v2);
        tess.addVertexWithUV(x2, y, z1, u2, v1);
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {}

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        Tessellator tess = Tessellator.instance;
        BlockBlackoutCurtains b = (BlockBlackoutCurtains) block;
        IIcon tex = b.getIcon(0, 0);

        tess.setBrightness((int) (1.25f * block.getMixedBrightnessForBlock(world, x, y, z)));
        tess.setColorOpaque_F(1, 1, 1);

        boolean connNorth = b.canConnectTo(world, x, y, z - 1, NORTH);
        boolean connSouth = b.canConnectTo(world, x, y, z + 1, SOUTH);
        boolean connWest = b.canConnectTo(world, x - 1, y, z, WEST);
        boolean connEast = b.canConnectTo(world, x + 1, y, z, EAST);
        boolean connDown = b.shouldSideBeRendered(world, x, y - 1, z, DOWN.ordinal());
        boolean connUp = b.shouldSideBeRendered(world, x, y + 1, z, UP.ordinal());

        double halfThickness = 1 / 16d;

        // spotless:off

        /*
         *              north (-z)
         *                0###1
         *                #   #
         *            A###B   2###3
         *  west (-x) #           # east (+x)
         *            9###8   5###4
         *                #   #
         *                7###6
         *              south (+z)
         */

        Vector2d floorCenter = new Vector2d(x + 0.5, z + 0.5);
        Vector2d z1 = new Vector2d(0, -halfThickness);
        Vector2d z2 = new Vector2d(0, -0.5);
        Vector2d x1 = new Vector2d(-halfThickness, 0);
        Vector2d x2 = new Vector2d(-0.5, 0);

        Vector2d c0 = new Vector2d(floorCenter).add(z2).add(x1);
        Vector2d c1 = new Vector2d(floorCenter).add(z2).sub(x1);
        Vector2d c2 = new Vector2d(floorCenter).add(z1).sub(x1);
        Vector2d c3 = new Vector2d(floorCenter).add(z1).sub(x2);
        Vector2d c4 = new Vector2d(floorCenter).sub(z1).sub(x2);
        Vector2d c5 = new Vector2d(floorCenter).sub(z1).sub(x1);
        Vector2d c6 = new Vector2d(floorCenter).sub(z2).sub(x1);
        Vector2d c7 = new Vector2d(floorCenter).sub(z2).add(x1);
        Vector2d c8 = new Vector2d(floorCenter).sub(z1).add(x1);
        Vector2d c9 = new Vector2d(floorCenter).sub(z1).add(x2);
        Vector2d cA = new Vector2d(floorCenter).add(z1).add(x2);
        Vector2d cB = new Vector2d(floorCenter).add(z1).add(x1);

        // spotless:on

        int totalConnections = (connNorth ? 1 : 0) + (connSouth ? 1 : 0) + (connWest ? 1 : 0) + (connEast ? 1 : 0);

        switch (totalConnections) {
            case 0:
                makeVerticalQuad(tess, tex, y, c7, c6);
                makeVerticalQuad(tess, tex, y, c1, c0);
                makeVerticalQuad(tess, tex, y, c4, c3);
                makeVerticalQuad(tess, tex, y, cA, c9);
                drawFloorCeil(tess, tex, y, c7, c1, connDown, connUp);
                drawFloorCeil(tess, tex, y, c9, cB, connDown, connUp);
                drawFloorCeil(tess, tex, y, c5, c3, connDown, connUp);
            case 4:
                makeVerticalQuad(tess, tex, y, c0, c7);
                makeVerticalQuad(tess, tex, y, c6, c1);
                makeVerticalQuad(tess, tex, y, c9, c4);
                makeVerticalQuad(tess, tex, y, c3, cA);
                break;
            case 1:
                if (connNorth) {
                    chain4(tess, tex, y, c0, c8, c5, c1);
                } else if (connWest) {
                    chain4(tess, tex, y, c9, c5, c2, cA);
                } else if (connSouth) {
                    chain4(tess, tex, y, c6, c2, cB, c7);
                } else {
                    chain4(tess, tex, y, c3, cB, c8, c4);
                }
                break;
            case 2:
                if (connNorth && connSouth) {
                    makeVerticalQuad(tess, tex, y, c0, c7);
                    makeVerticalQuad(tess, tex, y, c6, c1);
                } else if (connWest && connEast) {
                    makeVerticalQuad(tess, tex, y, c9, c4);
                    makeVerticalQuad(tess, tex, y, c3, cA);
                } else if (connNorth && connWest) {
                    makeVerticalQuad(tess, tex, y, c0, cB);
                    makeVerticalQuad(tess, tex, y, cB, cA);
                    makeVerticalQuad(tess, tex, y, c9, c5);
                    makeVerticalQuad(tess, tex, y, c5, c1);
                } else if (connWest && connSouth) {
                    makeVerticalQuad(tess, tex, y, c9, c8);
                    makeVerticalQuad(tess, tex, y, c8, c7);
                    makeVerticalQuad(tess, tex, y, c6, c2);
                    makeVerticalQuad(tess, tex, y, c2, cA);
                } else if (connSouth && connEast) {
                    makeVerticalQuad(tess, tex, y, c6, c5);
                    makeVerticalQuad(tess, tex, y, c5, c4);
                    makeVerticalQuad(tess, tex, y, c3, cB);
                    makeVerticalQuad(tess, tex, y, cB, c7);
                } else if (connEast && connNorth) {
                    makeVerticalQuad(tess, tex, y, c3, c2);
                    makeVerticalQuad(tess, tex, y, c2, c1);
                    makeVerticalQuad(tess, tex, y, c0, c8);
                    makeVerticalQuad(tess, tex, y, c8, c4);
                }
                break;
            case 3:
                if (!connNorth) {
                    makeVerticalQuad(tess, tex, y, c9, c4);
                    makeVerticalQuad(tess, tex, y, c3, cA);
                    makeVerticalQuad(tess, tex, y, c8, c7);
                    makeVerticalQuad(tess, tex, y, c6, c5);
                } else if (!connWest) {
                    makeVerticalQuad(tess, tex, y, c0, c7);
                    makeVerticalQuad(tess, tex, y, c6, c1);
                    makeVerticalQuad(tess, tex, y, c5, c4);
                    makeVerticalQuad(tess, tex, y, c3, c2);
                } else if (!connSouth) {
                    makeVerticalQuad(tess, tex, y, c9, c4);
                    makeVerticalQuad(tess, tex, y, c3, cA);
                    makeVerticalQuad(tess, tex, y, c2, c1);
                    makeVerticalQuad(tess, tex, y, c0, cB);
                } else {
                    makeVerticalQuad(tess, tex, y, c0, c7);
                    makeVerticalQuad(tess, tex, y, c6, c1);
                    makeVerticalQuad(tess, tex, y, cB, cA);
                    makeVerticalQuad(tess, tex, y, c9, c8);
                }
        }

        if (connNorth) {
            drawFloorCeil(tess, tex, y, cB, c1, connDown, connUp);
        }
        if (connWest) {
            drawFloorCeil(tess, tex, y, c9, cB, connDown, connUp);
        }
        if (connSouth) {
            drawFloorCeil(tess, tex, y, c7, c5, connDown, connUp);
        }
        if (connEast) {
            drawFloorCeil(tess, tex, y, c5, c3, connDown, connUp);
        }
        drawFloorCeil(tess, tex, y, c8, c2, connDown, connUp);

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return UtilitiesInExcess.blackoutCurtainsRenderID;
    }
}
