package com.fouristhenumber.utilitiesinexcess.common.renderers;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.spikeRenderID;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.fouristhenumber.utilitiesinexcess.utils.RenderableCube;
import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

@ThreadSafeISBRH(perThread = false)
public class SpikeRenderer implements ISimpleBlockRenderingHandler {

    final static List<RenderableCube> cubes = new ArrayList<RenderableCube>();

    static final int TEXTURE_SIZE = 64;

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        Tessellator t = Tessellator.instance;
        IIcon icon = renderer.getBlockIcon(block);
        for (RenderableCube c : cubes) {
            c.draw(t, x, y, z, icon, TEXTURE_SIZE, true);
        }
        return true;
    }

    @Override
    public void renderInventoryBlock(Block block, int meta, int modelId, RenderBlocks renderer) {
        Tessellator t = Tessellator.instance;
        IIcon icon = renderer.getBlockIcon(block);
        t.startDrawingQuads();
        for (RenderableCube c : cubes) {
            c.draw(t, 0, 0, 0, icon, TEXTURE_SIZE, true);
        }
        t.draw();
    }

    static {
        // base
        cubes.add(
            new RenderableCube(
                0,
                0,
                0,
                1F,
                0.0625F,
                1F,
                new float[][] { { 16, 0, 31, 15 }, { 32, 0, 47, 15 }, { 0, 16, 15, 16 }, { 0, 16, 15, 16 },
                    { 0, 16, 15, 16 }, { 0, 16, 15, 16 } }));

        // layer 2 plates

        float[][] layer2uv = new float[][] { { 6, 17, 11, 22 }, { 12, 17, 17, 22 }, { 0, 23, 5, 23 }, { 6, 23, 11, 23 },
            { 12, 23, 17, 23 }, { 18, 23, 23, 23 } };

        cubes.add(new RenderableCube(0.0625F, 0.0625F, 0.0625F, 0.4375F, 0.125F, 0.4375F, layer2uv));

        cubes.add(new RenderableCube(0.5625F, 0.0625F, 0.0625F, 0.9375F, 0.125F, 0.4375F, layer2uv));

        cubes.add(new RenderableCube(0.0625F, 0.0625F, 0.5625F, 0.4375F, 0.125F, 0.9375F, layer2uv));

        cubes.add(new RenderableCube(0.5625F, 0.0625F, 0.5625F, 0.9375F, 0.125F, 0.9375F, layer2uv));

        // layer 3 plates

        float[][] layer3uv = new float[][] { { 28, 18, 31, 21 }, { 32, 18, 35, 21 }, { 24, 22, 27, 23 },
            { 28, 22, 31, 23 }, { 32, 22, 35, 23 }, { 36, 22, 39, 23 } };

        cubes.add(new RenderableCube(0.125F, 0.125F, 0.125F, 0.375F, 0.25F, 0.375F, layer3uv));

        cubes.add(new RenderableCube(0.625F, 0.125F, 0.125F, 0.875F, 0.25F, 0.375F, layer3uv));

        cubes.add(new RenderableCube(0.125F, 0.125F, 0.625F, 0.375F, 0.25F, 0.875F, layer3uv));

        cubes.add(new RenderableCube(0.625F, 0.125F, 0.625F, 0.875F, 0.25F, 0.875F, layer3uv));

        // layer 4 plates

        float[][] layer4uv = new float[][] { { 2, 0, 3, 1 }, { 4, 0, 5, 1 }, { 0, 2, 1, 4 }, { 2, 2, 3, 4 },
            { 4, 2, 5, 4 }, { 6, 2, 7, 4 } };

        cubes.add(new RenderableCube(0.1875F, 0.25F, 0.1875F, 0.3125F, 0.4375F, 0.3125F, layer4uv));

        cubes.add(new RenderableCube(0.6875F, 0.25F, 0.1875F, 0.8125F, 0.4375F, 0.3125F, layer4uv));

        cubes.add(new RenderableCube(0.1875F, 0.25F, 0.6875F, 0.3125F, 0.4375F, 0.8125F, layer4uv));

        cubes.add(new RenderableCube(0.6875F, 0.25F, 0.6875F, 0.8125F, 0.4375F, 0.8125F, layer4uv));

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
