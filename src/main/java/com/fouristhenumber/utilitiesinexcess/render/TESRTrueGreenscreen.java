package com.fouristhenumber.utilitiesinexcess.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
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

    public static ArrayList<Vec3> positions = new ArrayList<>();

    private static final ShaderProgram shader = new ShaderProgram(
        UtilitiesInExcess.MODID,
        "shaders/TrueGreenscreen.vert.glsl",
        "shaders/TrueGreenscreen.frag.glsl");

    public static boolean inFrame = false;

    public static List<double[]> pos = new ArrayList<>();

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
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        inFrame = false;
    }

    public static void onPreRenderEntities() {

        if (MinecraftForgeClient.getRenderPass() != 0 || positions.isEmpty()) {
            return;
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureUtil.missingTexture.getGlTextureId());

        GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD, GL14.GL_FUNC_ADD);
        GL11.glDisable(GL11.GL_BLEND);
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
        GL11.glEnable(GL11.GL_BLEND);
        GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD, GL14.GL_MAX);

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);

        positions.clear();
    }

}
