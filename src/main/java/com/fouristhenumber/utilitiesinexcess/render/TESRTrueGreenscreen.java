package com.fouristhenumber.utilitiesinexcess.render;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.utils.RenderableCube;
import com.gtnewhorizon.gtnhlib.client.renderer.shader.ShaderProgram;

import cpw.mods.fml.common.gameevent.TickEvent;

public class TESRTrueGreenscreen extends TileEntitySpecialRenderer {

    public static final ArrayList<Vec3> positions = new ArrayList<>();

    private static final ShaderProgram shader = new ShaderProgram(
        UtilitiesInExcess.MODID,
        "shaders/TrueGreenscreen.vert.glsl",
        "shaders/TrueGreenscreen.frag.glsl");

    public static boolean inFrame = false;

    public static RenderableCube cube = new RenderableCube(
        0,
        0,
        0,
        1,
        1,
        1,
        new float[][] { { 0, 0, 1, 1 }, { 0, 0, 1, 1 }, { 0, 0, 1, 1 }, { 0, 0, 1, 1 }, { 0, 0, 1, 1 },
            { 0, 0, 1, 1 } });

    @Override
    public void renderTileEntityAt(TileEntity p_147500_1_, double x, double y, double z, float partialTicks) {
        inFrame = true;

        positions.add(Vec3.createVectorHelper(x, y, z));
    }

    public static void onPostClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            inFrame = false;
        }
    }

    // There is a bug that causes mipmaps of textures with transparency in them (like leaves) to wrongly change the
    // alpha values of the pixels they are drawn on. To work around this, we draw something right in front of the
    // player's face to cover the whole screen, but set the color mask to only allow writing to the alpha channel,
    // meaning that we are just setting the alpha values for every pixel to 1.
    public static void drawCameraLockedSquare(float distance, float size, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityLivingBase viewer = mc.renderViewEntity;
        if (viewer == null) viewer = mc.thePlayer;

        float yaw = viewer.prevRotationYaw + (viewer.rotationYaw - viewer.prevRotationYaw) * partialTicks;
        float pitch = viewer.prevRotationPitch + (viewer.rotationPitch - viewer.prevRotationPitch) * partialTicks;

        GL11.glPushMatrix();

        GL11.glRotatef(-(yaw + 180.0F), 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-pitch, 1.0F, 0.0F, 0.0F);

        GL11.glTranslatef(0.0F, 0.0F, -distance);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glColorMask(false, false, false, true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        float halfSize = size / 2.0F;
        tessellator.addVertex(-halfSize, -halfSize, 0.0D);
        tessellator.addVertex(halfSize, -halfSize, 0.0D);
        tessellator.addVertex(halfSize, halfSize, 0.0D);
        tessellator.addVertex(-halfSize, halfSize, 0.0D);

        tessellator.draw();

        GL11.glColorMask(true, true, true, true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    public static void onPreRenderEntities(float partialTicks) {

        if (MinecraftForgeClient.getRenderPass() != 0 || positions.isEmpty()) {
            return;
        }

        drawCameraLockedSquare(0.1F, 100, partialTicks);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureUtil.missingTexture.getGlTextureId());

        GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD, GL14.GL_FUNC_ADD);
        boolean blend = GL11.glGetBoolean(GL11.GL_BLEND);
        if (blend) {
            GL11.glDisable(GL11.GL_BLEND);
        }
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        shader.use();

        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        for (Vec3 pos : positions) {
            cube.draw(t, pos.xCoord, pos.yCoord, pos.zCoord, 1, false);
        }
        t.draw();

        ShaderProgram.clear();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        if (blend) {
            GL11.glEnable(GL11.GL_BLEND);
        }
        GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD, GL14.GL_MAX);

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);

        positions.clear();
    }

}
