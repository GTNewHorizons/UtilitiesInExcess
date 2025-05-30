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

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LINE_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(2.0F);

        GL11.glColor3f(1.0F, 1.0F, 1.0F);

        Minecraft mc = Minecraft.getMinecraft();
        double partialTicks = event.partialTicks;
        double posx = mc.renderViewEntity.lastTickPosX
            + (mc.renderViewEntity.posX - mc.renderViewEntity.lastTickPosX) * partialTicks;
        double posy = mc.renderViewEntity.lastTickPosY
            + (mc.renderViewEntity.posY - mc.renderViewEntity.lastTickPosY) * partialTicks;
        double posz = mc.renderViewEntity.lastTickPosZ
            + (mc.renderViewEntity.posZ - mc.renderViewEntity.lastTickPosZ) * partialTicks;

        for (Vec3 pos : candidatePositions) {
            int x = (int) pos.xCoord;
            int y = (int) pos.yCoord;
            int z = (int) pos.zCoord;

            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);

            aabb.offset(-posx, -posy, -posz);

            RenderGlobal.drawOutlinedBoundingBox(aabb, -1);
        }

        GL11.glPopAttrib();
    }
}
