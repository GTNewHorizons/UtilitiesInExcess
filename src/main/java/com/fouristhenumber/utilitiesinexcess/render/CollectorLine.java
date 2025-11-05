package com.fouristhenumber.utilitiesinexcess.render;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityCollector;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CollectorLine extends TileEntitySpecialRenderer {

    private final Map<Vec3, Integer> fadingLines = new LinkedHashMap<>();

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileEntityCollector collector)) return;

        if (collector.itemPositions != null) {
            for (Vec3 pos : collector.itemPositions) {
                fadingLines.put(pos, 20);
            }
        }

        //WHATEVER GO MY OPENGL STATE FLAGS
        //how to write open gl a fundamental guide \/
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(6.0f);
        GL11.glTranslated(x, y, z);

        //force fullbright (fun fact this is what night vision does so i didn't realize that this needed to be set UNTIL AFTER IT WORE OFF)
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        GL11.glBegin(GL11.GL_LINES);
        Iterator<Map.Entry<Vec3, Integer>> it = fadingLines.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Vec3, Integer> entry = it.next();
            Vec3 target = entry.getKey();
            int life = entry.getValue();

            float alpha = Math.max(0f, life / 20.0f);
            GL11.glColor4f(1.0f, 0f, 0f, alpha);

            double dx = target.xCoord - (te.xCoord + 0.5);
            double dy = target.yCoord - (te.yCoord + 0.5);
            double dz = target.zCoord - (te.zCoord + 0.5);

            GL11.glVertex3d(0, 0, 0);
            GL11.glVertex3d(dx, dy, dz);

            entry.setValue(life - 1);
            if (life <= 0) it.remove();
        }
        GL11.glEnd();

        if (collector.showBorder) {
            int r = 4;
            GL11.glLineWidth(3.0f);
            GL11.glBegin(GL11.GL_LINES);

            double[][] edges = {
                {-r, -r, -r}, {r + 1, -r, -r},
                {-r, -r, -r}, {-r, r + 1, -r},
                {-r, -r, -r}, {-r, -r, r + 1},
                {r + 1, r + 1, r + 1}, {-r, r + 1, r + 1},
                {r + 1, r + 1, r + 1}, {r + 1, -r, r + 1},
                {r + 1, r + 1, r + 1}, {r + 1, r + 1, -r},
                {r + 1, -r, -r}, {r + 1, r + 1, -r},
                {r + 1, -r, -r}, {r + 1, -r, r + 1},
                {-r, r + 1, -r}, {-r, r + 1, r + 1},
                {-r, r + 1, -r}, {r + 1, r + 1, -r},
                {-r, -r, r + 1}, {r + 1, -r, r + 1},
                {-r, -r, r + 1}, {-r, r + 1, r + 1}
            };

            long time = System.currentTimeMillis();
            for (int i = 0; i < edges.length; i += 2) {
                //wainbow :3
                float hue = (float) ((time * 0.001 + i * 0.1) % 1.0);
                float[] rgb = java.awt.Color.getHSBColor(hue, 1.0f, 1.0f).getRGBColorComponents(null);
                GL11.glColor4f(rgb[0], rgb[1], rgb[2], 0.8f);

                GL11.glVertex3d(edges[i][0], edges[i][1], edges[i][2]);
                GL11.glVertex3d(edges[i + 1][0], edges[i + 1][1], edges[i + 1][2]);
            }

            GL11.glEnd();
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();

        // clear only new item positions
        if (collector.itemPositions != null)
            collector.itemPositions.clear();
    }
}
