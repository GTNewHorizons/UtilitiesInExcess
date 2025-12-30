package com.fouristhenumber.utilitiesinexcess.common.renderers;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.render.HelperAO;
import com.fouristhenumber.utilitiesinexcess.render.IRotatableTile;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// todo change it to use GTNHLib models instead
public class RotatableBlockRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        Tessellator tess = Tessellator.instance;
        renderer.setRenderBoundsFromBlock(block);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        tess.startDrawingQuads();
        tess.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, metadata));

        tess.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, metadata));

        tess.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, metadata));

        tess.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, metadata));

        tess.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, metadata));

        tess.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, metadata));
        tess.draw();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        IRotatableTile tile = (IRotatableTile) world.getTileEntity(x, y, z);
        if (tile == null) return false;

        int meta = world.getBlockMetadata(x, y, z);
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            IIcon icon = tile.getIconForSide(block, side, meta);
            ForgeDirection rotation = tile.getRotationForSide(side, meta);
            renderFace(renderer, block, x, y, z, side, icon, rotation);
        }

        return true;
    }

    private void renderFace(RenderBlocks renderer, Block block, int x, int y, int z, ForgeDirection side, IIcon icon,
        ForgeDirection rotation) {
        renderer.setOverrideBlockTexture(icon);
        float brightness = 1.0f;
        switch (side) {
            case DOWN:
                HelperAO.RenderYNeg(renderer, block, x, y, z, brightness, brightness, brightness, icon, rotation);
                break;
            case UP:
                HelperAO.RenderYPos(renderer, block, x, y, z, brightness, brightness, brightness, icon, rotation);
                break;
            case NORTH:
                HelperAO.RenderZNeg(renderer, block, x, y, z, brightness, brightness, brightness, icon);
                break;
            case SOUTH:
                HelperAO.RenderZPos(renderer, block, x, y, z, brightness, brightness, brightness, icon);
                break;
            case WEST:
                HelperAO.RenderXNeg(renderer, block, x, y, z, brightness, brightness, brightness, icon);
                break;
            case EAST:
                HelperAO.RenderXPos(renderer, block, x, y, z, brightness, brightness, brightness, icon);
                break;
        }

        renderer.clearOverrideBlockTexture();
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return UtilitiesInExcess.rotatableblockRenderID;
    }
}
