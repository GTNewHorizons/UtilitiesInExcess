package com.fouristhenumber.utilitiesinexcess.common.renderers.transfer;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.energyNodeRenderID;

@ThreadSafeISBRH(perThread = false)
public class EnergyNodeRenderer implements ISimpleBlockRenderingHandler
{
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {
        Tessellator tessellator = Tessellator.instance;

        GL11.glPushMatrix();

        // Inventory blocks are rendered around the origin
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        tessellator.startDrawingQuads();

        // Base 8x8x8 cube
        renderer.setRenderBounds(
            4 / 16.0, 4 / 16.0, 4 / 16.0,
            12 / 16.0, 12 / 16.0, 12 / 16.0
        );

        renderer.renderStandardBlock(block, 0, 0, 0);

        // 6x6x1 face plates
        double min = 5 / 16.0;
        double max = 11 / 16.0;

        // Bottom
        renderer.setRenderBounds(min, 3 / 16.0, min, max, 4 / 16.0, max);
        renderer.renderStandardBlock(block, 0, 0, 0);

        // Top
        renderer.setRenderBounds(min, 12 / 16.0, min, max, 13 / 16.0, max);
        renderer.renderStandardBlock(block, 0, 0, 0);

        // North (-Z)
        renderer.setRenderBounds(min, min, 3 / 16.0, max, max, 4 / 16.0);
        renderer.renderStandardBlock(block, 0, 0, 0);

        // South (+Z)
        renderer.setRenderBounds(min, min, 12 / 16.0, max, max, 13 / 16.0);
        renderer.renderStandardBlock(block, 0, 0, 0);

        // West (-X)
        renderer.setRenderBounds(3 / 16.0, min, min, 4 / 16.0, max, max);
        renderer.renderStandardBlock(block, 0, 0, 0);

        // East (+X)
        renderer.setRenderBounds(12 / 16.0, min, min, 13 / 16.0, max, max);
        renderer.renderStandardBlock(block, 0, 0, 0);

        tessellator.draw();

        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        return false;
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
