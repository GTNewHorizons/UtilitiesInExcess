package com.fouristhenumber.utilitiesinexcess.common.renderers;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.AxisAlignedBB;
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
public class WireframeRenderer {

    private final static ObjectOpenHashSet<BlockPos> candidatePositions = new ObjectOpenHashSet<>();

    // Setter so your item can update the candidate positions
    public static void addCandidatePosition(BlockPos pos) {
        candidatePositions.add(pos);
    }

    public static void clearCandidatePositions() {
        if (!candidatePositions.isEmpty()) candidatePositions.clear();
    }

    @EventBusSubscriber.Condition
    public static boolean shouldSubscribe() {
        return ItemConfig.enableArchitectsWand;
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        if (candidatePositions.isEmpty()) {
            return;
        }

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LINE_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(2.0F);

        GL11.glColor3f(1.0F, 1.0F, 1.0F);

        double posX = RenderManager.renderPosX;
        double posY = RenderManager.renderPosY;
        double posZ = RenderManager.renderPosZ;

        for (BlockPos pos : candidatePositions) {
            int x = pos.x;
            int y = pos.y;
            int z = pos.z;

            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);

            aabb.offset(-posX, -posY, -posZ);

            RenderGlobal.drawOutlinedBoundingBox(aabb, -1);
        }

        GL11.glPopAttrib();
    }
}
