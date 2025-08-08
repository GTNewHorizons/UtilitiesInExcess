package com.fouristhenumber.utilitiesinexcess.common.renderers;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class LapisAetheriusRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        GL11.glDisable(GL11.GL_LIGHTING);
        Tessellator tess = Tessellator.instance;

        renderer.setRenderBoundsFromBlock(block);

        tess.startDrawingQuads();
        tess.setNormal(0, -1, 0);
        renderer.renderFaceYNeg(block, 0, 0, 0, block.getIcon(0, metadata));
        tess.draw();

        tess.startDrawingQuads();
        tess.setNormal(0, 1, 0);
        renderer.renderFaceYPos(block, 0, 0, 0, block.getIcon(1, metadata));
        tess.draw();

        tess.startDrawingQuads();
        tess.setNormal(0, 0, -1);
        renderer.renderFaceZNeg(block, 0, 0, 0, block.getIcon(2, metadata));
        tess.draw();

        tess.startDrawingQuads();
        tess.setNormal(0, 0, 1);
        renderer.renderFaceZPos(block, 0, 0, 0, block.getIcon(3, metadata));
        tess.draw();

        tess.startDrawingQuads();
        tess.setNormal(-1, 0, 0);
        renderer.renderFaceXNeg(block, 0, 0, 0, block.getIcon(4, metadata));
        tess.draw();

        tess.startDrawingQuads();
        tess.setNormal(1, 0, 0);
        renderer.renderFaceXPos(block, 0, 0, 0, block.getIcon(5, metadata));
        tess.draw();

        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
                                    Block block, int modelId, RenderBlocks renderer) {
        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT | GL11.GL_CURRENT_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);

        renderer.setRenderBoundsFromBlock(block);
        renderer.renderFaceYNeg(block, x, y, z, block.getIcon(0, 0));
        renderer.renderFaceYPos(block, x, y, z, block.getIcon(1, 0));
        renderer.renderFaceZNeg(block, x, y, z, block.getIcon(2, 0));
        renderer.renderFaceZPos(block, x, y, z, block.getIcon(3, 0));
        renderer.renderFaceXNeg(block, x, y, z, block.getIcon(4, 0));
        renderer.renderFaceXPos(block, x, y, z, block.getIcon(5, 0));

        GL11.glPopAttrib();
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return UtilitiesInExcess.lapisAetheriusRenderID;
    }
}
