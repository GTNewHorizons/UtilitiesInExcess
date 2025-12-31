package com.fouristhenumber.utilitiesinexcess.common.renderers;

import static org.lwjgl.opengl.GL11.GL_BLEND;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import com.fouristhenumber.utilitiesinexcess.config.items.ItemConfig;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
@EventBusSubscriber(phase = Phase.INIT, side = Side.CLIENT)
public class XRayRenderer {

    private final static ObjectOpenHashSet<BlockPos> candidatePositions = new ObjectOpenHashSet<>();

    public static void addCandidatePosition(BlockPos pos) {
        candidatePositions.add(pos);
    }

    public static void clearCandidatePositions() {
        if (!candidatePositions.isEmpty()) candidatePositions.clear();
    }

    @EventBusSubscriber.Condition
    public static boolean shouldSubscribe() {
        return ItemConfig.enableXRayGlasses;
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        if (candidatePositions.isEmpty()) return;

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LINE_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL_BLEND);
        GL11.glLineWidth(2.0F);
        GL11.glColor4f(1.0F, 0.3F, 0.3F, 0.5F);

        double posX = RenderManager.renderPosX;
        double posY = RenderManager.renderPosY;
        double posZ = RenderManager.renderPosZ;

        Tessellator tess = Tessellator.instance;
        tess.startDrawing(GL11.GL_LINES);

        for (BlockPos pos : candidatePositions) {
            int x = pos.x;
            int y = pos.y;
            int z = pos.z;

            boolean enclosedPosX = (candidatePositions.contains(new BlockPos(x + 1, y, z)));
            boolean enclosedNegX = (candidatePositions.contains(new BlockPos(x - 1, y, z)));
            boolean enclosedPosY = (candidatePositions.contains(new BlockPos(x, y + 1, z)));
            boolean enclosedNegY = (candidatePositions.contains(new BlockPos(x, y - 1, z)));
            boolean enclosedPosZ = (candidatePositions.contains(new BlockPos(x, y, z + 1)));
            boolean enclosedNegZ = (candidatePositions.contains(new BlockPos(x, y, z - 1)));

            // Top
            if (!enclosedPosY && !enclosedNegX) {
                tess.addVertex(x - posX, y - posY + 1, z - posZ);
                tess.addVertex(x - posX, y - posY + 1, z - posZ + 1);
            }
            if (!enclosedPosY && !enclosedPosX) {
                tess.addVertex(x - posX + 1, y - posY + 1, z - posZ);
                tess.addVertex(x - posX + 1, y - posY + 1, z - posZ + 1);
            }
            if (!enclosedPosY && !enclosedNegZ) {
                tess.addVertex(x - posX, y - posY + 1, z - posZ);
                tess.addVertex(x - posX + 1, y - posY + 1, z - posZ);
            }
            if (!enclosedPosY && !enclosedPosZ) {
                tess.addVertex(x - posX, y - posY + 1, z - posZ + 1);
                tess.addVertex(x - posX + 1, y - posY + 1, z - posZ + 1);
            }

            // Side edges
            if (!enclosedNegZ && !enclosedNegX) {
                tess.addVertex(x - posX, y - posY, z - posZ);
                tess.addVertex(x - posX, y - posY + 1, z - posZ);
            }
            if (!enclosedPosZ && !enclosedPosX) {
                tess.addVertex(x - posX + 1, y - posY, z - posZ + 1);
                tess.addVertex(x - posX + 1, y - posY + 1, z - posZ + 1);
            }
            if (!enclosedNegZ && !enclosedPosX) {
                tess.addVertex(x - posX + 1, y - posY, z - posZ);
                tess.addVertex(x - posX + 1, y - posY + 1, z - posZ);
            }
            if (!enclosedPosZ && !enclosedNegX) {
                tess.addVertex(x - posX, y - posY, z - posZ + 1);
                tess.addVertex(x - posX, y - posY + 1, z - posZ + 1);
            }

            // Bottom
            if (!enclosedNegY && !enclosedNegX) {
                tess.addVertex(x - posX, y - posY, z - posZ);
                tess.addVertex(x - posX, y - posY, z - posZ + 1);
            }
            if (!enclosedNegY && !enclosedPosX) {
                tess.addVertex(x - posX + 1, y - posY, z - posZ);
                tess.addVertex(x - posX + 1, y - posY, z - posZ + 1);
            }
            if (!enclosedNegY && !enclosedNegZ) {
                tess.addVertex(x - posX, y - posY, z - posZ);
                tess.addVertex(x - posX + 1, y - posY, z - posZ);
            }
            if (!enclosedNegY && !enclosedPosZ) {
                tess.addVertex(x - posX, y - posY, z - posZ + 1);
                tess.addVertex(x - posX + 1, y - posY, z - posZ + 1);
            }
        }
        tess.draw();
        GL11.glPopAttrib();
    }
}
