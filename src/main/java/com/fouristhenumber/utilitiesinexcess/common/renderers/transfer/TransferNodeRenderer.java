package com.fouristhenumber.utilitiesinexcess.common.renderers.transfer;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.transferNodeRenderID;
import static com.fouristhenumber.utilitiesinexcess.common.renderers.transfer.TransferPipeRenderer.RenderPipes;
import static com.fouristhenumber.utilitiesinexcess.utils.RenderUtils.renderInventoryCube;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityTransferNode;
import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

@ThreadSafeISBRH(perThread = false)
public class TransferNodeRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        IIcon icon = block.getIcon(0, metadata);

        float t = 2f / 16f;
        float s12 = 12f / 16f;
        float s8 = 8f / 16f;
        float s12Half = (1f - s12) / 2f;
        float s8Half = (1f - s8) / 2f;

        Tessellator tess = Tessellator.instance;

        tess.startDrawingQuads();
        tess.setNormal(0, 1, 0);

        renderer.setRenderBounds(0, 0, 0, 1, t, 1);
        renderInventoryCube(renderer, block, metadata);
        renderer.setRenderBounds(s12Half, t, s12Half, s12Half + s12, 2f * t, s12Half + s12);
        renderInventoryCube(renderer, block, metadata);
        renderer.setRenderBounds(s8Half, 2f * t, s8Half, s8Half + s8, 3f * t, s8Half + s8);
        renderInventoryCube(renderer, block, metadata);

        tess.draw();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        if (modelId != getRenderId()) return false;

        int meta = world.getBlockMetadata(x, y, z);

        float t = 2f / 16f;
        float s12 = 12f / 16f;
        float s8 = 8f / 16f;
        float s12Half = (1f - s12) / 2f;
        float s8Half = (1f - s8) / 2f;

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

        switch (meta) {
            case 0:
                renderer.setRenderBounds(s12Half, t, s12Half, s12Half + s12, 2f * t, s12Half + s12);
                break;
            case 1:
                renderer.setRenderBounds(s12Half, 1f - 2f * t, s12Half, s12Half + s12, 1f - t, s12Half + s12);
                break;
            case 2:
                renderer.setRenderBounds(s12Half, s12Half, t, s12Half + s12, s12Half + s12, 2f * t);
                break;
            case 3:
                renderer.setRenderBounds(s12Half, s12Half, 1f - 2f * t, s12Half + s12, s12Half + s12, 1f - t);
                break;
            case 4:
                renderer.setRenderBounds(t, s12Half, s12Half, 2f * t, s12Half + s12, s12Half + s12);
                break;
            case 5:
                renderer.setRenderBounds(1f - 2f * t, s12Half, s12Half, 1f - t, s12Half + s12, s12Half + s12);
                break;
        }
        renderer.renderStandardBlock(block, x, y, z);

        switch (meta) {
            case 0:
                renderer.setRenderBounds(s8Half, 2f * t, s8Half, s8Half + s8, 3f * t, s8Half + s8);
                break;
            case 1:
                renderer.setRenderBounds(s8Half, 1f - 3f * t, s8Half, s8Half + s8, 1f - 2f * t, s8Half + s8);
                break;
            case 2:
                renderer.setRenderBounds(s8Half, s8Half, 2f * t, s8Half + s8, s8Half + s8, 3f * t);
                break;
            case 3:
                renderer.setRenderBounds(s8Half, s8Half, 1f - 3f * t, s8Half + s8, s8Half + s8, 1f - 2f * t);
                break;
            case 4:
                renderer.setRenderBounds(2f * t, s8Half, s8Half, 3f * t, s8Half + s8, s8Half + s8);
                break;
            case 5:
                renderer.setRenderBounds(1f - 3f * t, s8Half, s8Half, 1f - 2f * t, s8Half + s8, s8Half + s8);
                break;
        }
        renderer.renderStandardBlock(block, x, y, z);

        TileEntityTransferNode te = (TileEntityTransferNode) world.getTileEntity(x, y, z);
        if (te == null) return false;

        int mask = te.getConnectionsMask();

        RenderPipes(mask, x, y, z, renderer, mask != 0);

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return transferNodeRenderID;
    }
}
