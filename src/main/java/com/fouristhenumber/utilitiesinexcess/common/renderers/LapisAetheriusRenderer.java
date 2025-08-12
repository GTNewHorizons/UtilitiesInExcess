package com.fouristhenumber.utilitiesinexcess.common.renderers;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class LapisAetheriusRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        Tessellator tess = Tessellator.instance;

        renderer.setRenderBoundsFromBlock(block);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        tess.startDrawingQuads();
        tess.setNormal(0, 1, 0);
        renderer.renderFaceYNeg(block, 0, 0, 0, block.getIcon(0, metadata));
        renderer.renderFaceYPos(block, 0, 0, 0, block.getIcon(1, metadata));
        renderer.renderFaceZNeg(block, 0, 0, 0, block.getIcon(2, metadata));
        renderer.renderFaceZPos(block, 0, 0, 0, block.getIcon(3, metadata));
        renderer.renderFaceXNeg(block, 0, 0, 0, block.getIcon(4, metadata));
        renderer.renderFaceXPos(block, 0, 0, 0, block.getIcon(5, metadata));
        tess.draw();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        Tessellator tess = Tessellator.instance;

        tess.setBrightness(240);
        tess.setColorOpaque_F(1F, 1F, 1F);

        int meta = world.getBlockMetadata(x, y, z);
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y - 1, z, 0)) {
            renderer.renderFaceYNeg(block, x, y, z, block.getIcon(0, meta));
        }
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y + 1, z, 1)) {
            renderer.renderFaceYPos(block, x, y, z, block.getIcon(1, meta));
        }
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z - 1, 2)) {
            renderer.renderFaceZNeg(block, x, y, z, block.getIcon(2, meta));
        }
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z + 1, 3)) {
            renderer.renderFaceZPos(block, x, y, z, block.getIcon(3, meta));
        }
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x - 1, y, z, 4)) {
            renderer.renderFaceXNeg(block, x, y, z, block.getIcon(4, meta));
        }
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x + 1, y, z, 5)) {
            renderer.renderFaceXPos(block, x, y, z, block.getIcon(5, meta));
        }

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
