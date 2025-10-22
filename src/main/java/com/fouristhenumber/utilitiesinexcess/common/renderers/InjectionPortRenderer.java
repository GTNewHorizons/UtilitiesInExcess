package com.fouristhenumber.utilitiesinexcess.common.renderers;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class InjectionPortRenderer implements ISimpleBlockRenderingHandler {

    public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        if (modelId != getRenderId()) return;

        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        IIcon icon = block.getIcon(0, metadata);
        renderer.setRenderBounds(0, 0, 0, 1, 2f / 16f, 1);
        renderer.renderFaceYNeg(block, 0, 0, 0, icon);
        renderer.renderFaceYPos(block, 0, 0, 0, icon);
        renderer.renderFaceZNeg(block, 0, 0, 0, icon);
        renderer.renderFaceZPos(block, 0, 0, 0, icon);
        renderer.renderFaceXNeg(block, 0, 0, 0, icon);
        renderer.renderFaceXPos(block, 0, 0, 0, icon);

        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        if (modelId != getRenderId()) return false;

        int meta = world.getBlockMetadata(x, y, z);
        IIcon icon = block.getIcon(meta, meta);

        float t = 2f / 16f;

        switch (meta) {
            case 0:
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, t, 1.0F);
                break;
            case 1:
                renderer.setRenderBounds(0.0F, 1.0F - t, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            case 2:
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, t);
                break;
            case 3:
                renderer.setRenderBounds(0.0F, 0.0F, 1.0F - t, 1.0F, 1.0F, 1.0F);
                break;
            case 4:
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, t, 1.0F, 1.0F);
                break;
            case 5:
                renderer.setRenderBounds(1.0F - t, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
        }

        renderer.renderStandardBlock(block, x, y, z);
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return RENDER_ID;
    }
}
