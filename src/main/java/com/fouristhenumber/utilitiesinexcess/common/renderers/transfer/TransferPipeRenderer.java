package com.fouristhenumber.utilitiesinexcess.common.renderers.transfer;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.transferPipeRenderID;
import static com.fouristhenumber.utilitiesinexcess.utils.RenderUtils.renderInventoryCube;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.pipe.BlockCrossoverPipe;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityNetworkComponentBase;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

@ThreadSafeISBRH(perThread = false)
public class TransferPipeRenderer implements ISimpleBlockRenderingHandler {

    public static void RenderPipes(int mask, int x, int y, int z, Block block, RenderBlocks renderer, boolean renderCenter) {

        if (renderCenter) {
            if (block instanceof BlockCrossoverPipe)
            {
                renderer.setRenderBounds(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);
                renderer.renderStandardBlock(block, x, y, z);
            }
            else
            {
                renderer.setRenderBounds(0.375, 0.375, 0.375, 0.625, 0.625, 0.625);
                renderer.renderStandardBlock(block, x, y, z);
            }

        }

        // -Y (down)
        if ((mask & (1)) != 0) {
            renderer.setRenderBounds(0.375, 0.0, 0.375, 0.625, 0.375, 0.625);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // +Y (up)
        if ((mask & (1 << 1)) != 0) {
            renderer.setRenderBounds(0.375, 0.625, 0.375, 0.625, 1.0, 0.625);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // -Z (north)
        if ((mask & (1 << 2)) != 0) {
            renderer.setRenderBounds(0.375, 0.375, 0.0, 0.625, 0.625, 0.375);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // +Z (south)
        if ((mask & (1 << 3)) != 0) {
            renderer.setRenderBounds(0.375, 0.375, 0.625, 0.625, 0.625, 1.0);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // -X (west)
        if ((mask & (1 << 4)) != 0) {
            renderer.setRenderBounds(0.0, 0.375, 0.375, 0.375, 0.625, 0.625);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // +X (east)
        if ((mask & (1 << 5)) != 0) {
            renderer.setRenderBounds(0.625, 0.375, 0.375, 1.0, 0.625, 0.625);
            renderer.renderStandardBlock(block, x, y, z);
        }
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        Tessellator tess = Tessellator.instance;

        tess.startDrawingQuads();
        tess.setNormal(0, 1, 0);

        if (block instanceof BlockCrossoverPipe)
        {
            renderer.setRenderBounds(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);
            renderInventoryCube(renderer, block, metadata);
        }
        else
        {
            renderer.setRenderBounds(0.625, 0.375, 0.375, 1.0, 0.625, 0.625);
            renderInventoryCube(renderer, block, metadata);
        }
        renderer.setRenderBounds(0.0, 0.375, 0.375, 0.375, 0.625, 0.625);
        renderInventoryCube(renderer, block, metadata);
        renderer.setRenderBounds(0.375, 0.625, 0.375, 0.625, 1.0, 0.625);
        renderInventoryCube(renderer, block, metadata);
        renderer.setRenderBounds(0.375, 0.0, 0.375, 0.625, 0.375, 0.625);
        renderInventoryCube(renderer, block, metadata);
        renderer.setRenderBounds(0.375, 0.375, 0.625, 0.625, 0.625, 1.0);
        renderInventoryCube(renderer, block, metadata);
        renderer.setRenderBounds(0.375, 0.375, 0.0, 0.625, 0.625, 0.375);
        renderInventoryCube(renderer, block, metadata);

        tess.draw();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess worldAccess, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        TileEntityNetworkComponentBase<?> te = (TileEntityNetworkComponentBase<?>) worldAccess.getTileEntity(x, y, z);
        if (te == null) return false;

        RenderPipes(te.getRawConnectionMask(), x, y, z, block, renderer, true);

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return transferPipeRenderID;
    }
}
