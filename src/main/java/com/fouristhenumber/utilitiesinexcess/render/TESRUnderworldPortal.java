package com.fouristhenumber.utilitiesinexcess.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;
import com.gtnewhorizon.gtnhlib.client.renderer.vbo.VertexBuffer;
import com.gtnewhorizon.gtnhlib.client.renderer.vertex.DefaultVertexFormat;

public class TESRUnderworldPortal extends TileEntitySpecialRenderer {

    public static final IModelCustom FRAME = AdvancedModelLoader
        .loadModel(new ResourceLocation(UtilitiesInExcess.MODID, "models/underworld_portal/frame.obj"));

    private VertexBuffer core;

    private void initCoreVBO() {
        TessellatorManager.startCapturing();

        Tessellator tessellator = TessellatorManager.get();

        tessellator.startDrawingQuads();

        tessellator.addVertex(-0.5, 0.5, -0.5);
        tessellator.addVertex(-0.5, 0.5, 0.5);
        tessellator.addVertex(0.5, 0.5, 0.5);
        tessellator.addVertex(0.5, 0.5, -0.5);

        tessellator.addVertex(-0.5, -0.5, -0.5);
        tessellator.addVertex(0.5, -0.5, -0.5);
        tessellator.addVertex(0.5, -0.5, 0.5);
        tessellator.addVertex(-0.5, -0.5, 0.5);

        tessellator.addVertex(0.5, -0.5, -0.5);
        tessellator.addVertex(-0.5, -0.5, -0.5);
        tessellator.addVertex(-0.5, 0.5, -0.5);
        tessellator.addVertex(0.5, 0.5, -0.5);

        tessellator.addVertex(0.5, -0.5, 0.5);
        tessellator.addVertex(0.5, 0.5, 0.5);
        tessellator.addVertex(-0.5, 0.5, 0.5);
        tessellator.addVertex(-0.5, -0.5, 0.5);

        tessellator.addVertex(-0.5, -0.5, 0.5);
        tessellator.addVertex(-0.5, 0.5, 0.5);
        tessellator.addVertex(-0.5, 0.5, -0.5);
        tessellator.addVertex(-0.5, -0.5, -0.5);

        tessellator.addVertex(0.5, -0.5, 0.5);
        tessellator.addVertex(0.5, -0.5, -0.5);
        tessellator.addVertex(0.5, 0.5, -0.5);
        tessellator.addVertex(0.5, 0.5, 0.5);

        tessellator.draw();

        core = TessellatorManager.stopCapturingToVBO(DefaultVertexFormat.POSITION);
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (core == null) {
            initCoreVBO();
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glTranslated(x + 0.5, y, z + 0.5);

        float light = tile.getWorldObj()
            .getLightBrightness(tile.xCoord, tile.yCoord, tile.zCoord);

        Tessellator.instance.setBrightness((int) (light * 16));

        bindTexture(new ResourceLocation(UtilitiesInExcess.MODID, "textures/blocks/bedrockium_block.png"));
        FRAME.renderAll();

        GL11.glPopMatrix();

        GL11.glPushMatrix();

        int counter = (int) (System.currentTimeMillis() % 1_000_000);

        GL11.glTranslated(x + 0.5, y + 0.6 + Math.sin(counter / 4000d * Math.PI * 2) * 0.05, z + 0.5);
        GL11.glScaled(0.3, 0.3, 0.3);

        GL11.glRotated(counter / 500d * Math.PI * 2, 1, 0, 0);
        GL11.glRotated(counter / 100d * Math.PI * 2, 0, 1, 0);
        GL11.glRotated(counter / 400d * Math.PI * 2, 0, 0, 1);
        GL11.glRotated(counter / 200d * Math.PI * 2, 0, 1, 1);

        UnderworldPortalShader.INSTANCE.use();
        UnderworldPortalShader.setLightFromLocation(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);

        bindTexture(TextureMap.locationBlocksTexture);

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        core.render();
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        UnderworldPortalShader.clear();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }
}
