package com.fouristhenumber.utilitiesinexcess.common.renderers.transfer;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.transferPipeRenderID;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityTransferPipe;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class TransferPipeRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess worldAccess, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        TileEntityTransferPipe te = (TileEntityTransferPipe) worldAccess.getTileEntity(x, y, z);
        if (te == null) return false;
        int mask = te.getConnectionsMask();

        renderer.setRenderBounds(0.375, 0.375, 0.375, 0.625, 0.625, 0.625);
        renderer.renderStandardBlock(block, x, y, z);

        // +X
        if ((mask & (1)) != 0) {
            renderer.setRenderBounds(0.625, 0.375, 0.375, 1.0, 0.625, 0.625);
            renderer.renderStandardBlock(block, x, y, z);
        }
        // -X
        if ((mask & (1 << 1)) != 0) {
            renderer.setRenderBounds(0.0, 0.375, 0.375, 0.375, 0.625, 0.625);
            renderer.renderStandardBlock(block, x, y, z);
        }
        // +Y
        if ((mask & (1 << 2)) != 0) {
            renderer.setRenderBounds(0.375, 0.625, 0.375, 0.625, 1.0, 0.625);
            renderer.renderStandardBlock(block, x, y, z);
        }
        // -Y
        if ((mask & (1 << 3)) != 0) {
            renderer.setRenderBounds(0.375, 0.0, 0.375, 0.625, 0.375, 0.625);
            renderer.renderStandardBlock(block, x, y, z);
        }
        // +Z
        if ((mask & (1 << 4)) != 0) {
            renderer.setRenderBounds(0.375, 0.375, 0.625, 0.625, 0.625, 1.0);
            renderer.renderStandardBlock(block, x, y, z);
        }
        // -Z
        if ((mask & (1 << 5)) != 0) {
            renderer.setRenderBounds(0.375, 0.375, 0.0, 0.625, 0.625, 0.375);
            renderer.renderStandardBlock(block, x, y, z);
        }

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
