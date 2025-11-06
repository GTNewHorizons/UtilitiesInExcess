package com.fouristhenumber.utilitiesinexcess.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

/// This is just used to render the portal frame when in item form.
public class ISBRHUnderworldPortal implements ISimpleBlockRenderingHandler {

    public static final ISBRHUnderworldPortal INSTANCE = new ISBRHUnderworldPortal();

    public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();

    private static void bindTexture(ResourceLocation resource) {
        Minecraft.getMinecraft().renderEngine.bindTexture(resource);
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        bindTexture(new ResourceLocation(UtilitiesInExcess.MODID, "textures/blocks/bedrockium_block.png"));

        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0, -0.5F, 0);
        TESRUnderworldPortal.FRAME.renderAll();

        bindTexture(TextureMap.locationItemsTexture);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        return false;
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
