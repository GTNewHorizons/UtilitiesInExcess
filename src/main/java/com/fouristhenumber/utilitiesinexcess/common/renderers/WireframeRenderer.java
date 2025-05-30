package com.fouristhenumber.utilitiesinexcess.common.renderers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WireframeRenderer {

    public WireframeRenderer() {
        candidatePositions = new ArrayList<>();
        candidatePositions.add(Vec3.createVectorHelper(10, 10, 10));
    }

    // This should be set when the item is targeting a block.
    // For example, from your item class after calculating candidate positions.
    private static List<Vec3> candidatePositions;

    // Setter so your item can update the candidate positions
    public static void setCandidatePositions(List<Vec3> newCandidatePositions) {
        candidatePositions = newCandidatePositions;
    }

    public static void clearCandidatePositions() {
        if (!candidatePositions.isEmpty()) candidatePositions = new ArrayList<>();
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (candidatePositions == null || candidatePositions.isEmpty()) {
            return;
        }

        // Configure OpenGL for drawing a wireframe.
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LINE_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(2.0F);

        // Set your color here (white in this case).
        GL11.glColor3f(1.0F, 1.0F, 1.0F);

        Minecraft mc = Minecraft.getMinecraft();
        double posx = mc.renderViewEntity.posX;
        double posy = mc.renderViewEntity.posY;
        double posz = mc.renderViewEntity.posZ;

        // Loop through each candidate position.
        for (Vec3 pos : candidatePositions) {
            int x = (int) pos.xCoord;
            int y = (int) pos.yCoord;
            int z = (int) pos.zCoord;

            // Create the bounding box for one block. Adjust the bounds as needed.
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);

            aabb.offset(-posx, -posy, -posz);

            // Draw the outlined bounding box.
            RenderGlobal.drawOutlinedBoundingBox(aabb, -1);
        }

        // Restore OpenGL states.
        GL11.glPopAttrib();
    }
}
