package com.fouristhenumber.utilitiesinexcess.common.renderers;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.spikeRenderID;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockSpike;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySpike;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class SpikeInventoryRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        TileEntityRendererDispatcher.instance
            .renderTileEntityAt(new TileEntitySpike(((BlockSpike) block).getSpikeType()), 0.0D, 0.0D, 0.0D, 0.0F);
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return spikeRenderID;
    }
}
