package com.fouristhenumber.utilitiesinexcess.common.renderers.transfer;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.energyNodeRenderID;
import static com.fouristhenumber.utilitiesinexcess.utils.RenderUtils.renderInventoryCube;

@ThreadSafeISBRH(perThread = false)
public class EnergyNodeRenderer implements ISimpleBlockRenderingHandler
{
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        // Base 8x8x8 cube
        renderer.setRenderBounds(
            4 / 16.0, 4 / 16.0, 4 / 16.0,
            12 / 16.0, 12 / 16.0, 12 / 16.0
        );

        renderInventoryCube(renderer, block, metadata);

        // 6x6x1 face plates
        double min = 5 / 16.0;
        double max = 11 / 16.0;

        // Bottom
        renderer.setRenderBounds(min, 3 / 16.0, min, max, 4 / 16.0, max);
        renderInventoryCube(renderer, block, metadata);

        // Top
        renderer.setRenderBounds(min, 12 / 16.0, min, max, 13 / 16.0, max);
        renderInventoryCube(renderer, block, metadata);

        // North (-Z)
        renderer.setRenderBounds(min, min, 3 / 16.0, max, max, 4 / 16.0);
        renderInventoryCube(renderer, block, metadata);

        // South (+Z)
        renderer.setRenderBounds(min, min, 12 / 16.0, max, max, 13 / 16.0);
        renderInventoryCube(renderer, block, metadata);

        // West (-X)
        renderer.setRenderBounds(3 / 16.0, min, min, 4 / 16.0, max, max);
        renderInventoryCube(renderer, block, metadata);

        // East (+X)
        renderer.setRenderBounds(12 / 16.0, min, min, 13 / 16.0, max, max);
        renderInventoryCube(renderer, block, metadata);

        tessellator.draw();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        // Base 8x8x8 cube
        renderer.setRenderBounds(
            4 / 16.0, 4 / 16.0, 4 / 16.0,
            12 / 16.0, 12 / 16.0, 12 / 16.0
        );

        renderer.renderStandardBlock(block, x, y, z);

        // 6x6x1 face plates
        double min = 5 / 16.0;
        double max = 11 / 16.0;

        // Bottom
        renderer.setRenderBounds(min, 3 / 16.0, min, max, 4 / 16.0, max);
        renderer.renderStandardBlock(block, x, y, z);

        // Top
        renderer.setRenderBounds(min, 12 / 16.0, min, max, 13 / 16.0, max);
        renderer.renderStandardBlock(block, x, y, z);

        // North (-Z)
        renderer.setRenderBounds(min, min, 3 / 16.0, max, max, 4 / 16.0);
        renderer.renderStandardBlock(block, x, y, z);

        // South (+Z)
        renderer.setRenderBounds(min, min, 12 / 16.0, max, max, 13 / 16.0);
        renderer.renderStandardBlock(block, x, y, z);

        // West (-X)
        renderer.setRenderBounds(3 / 16.0, min, min, 4 / 16.0, max, max);
        renderer.renderStandardBlock(block, x, y, z);

        // East (+X)
        renderer.setRenderBounds(12 / 16.0, min, min, 13 / 16.0, max, max);
        renderer.renderStandardBlock(block, x, y, z);

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId)
    {
        return true;
    }

    @Override
    public int getRenderId() {
        return energyNodeRenderID;
    }
}
