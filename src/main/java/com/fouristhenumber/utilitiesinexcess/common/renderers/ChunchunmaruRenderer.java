package com.fouristhenumber.utilitiesinexcess.common.renderers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

public class ChunchunmaruRenderer implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return switch (helper) {
            case ENTITY_ROTATION, ENTITY_BOBBING, INVENTORY_BLOCK -> true;
            default -> false;
        };
    }

    public static final ResourceLocation chunchunmaruTexture = new ResourceLocation(
        "utilitiesinexcess",
        "textures/items/chunchunmaru.png");
    public static final IModelCustom MODEL = AdvancedModelLoader
        .loadModel(new ResourceLocation(UtilitiesInExcess.MODID, "models/chunchunmaru/chunchunmaru.obj"));

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glPushMatrix();
        mc.renderEngine.bindTexture(chunchunmaruTexture);

        if (type == ItemRenderType.INVENTORY) {
            GL11.glTranslatef(0, -1f, 0);
            GL11.glScalef(1.5F, 1.5F, 1.5F);
        } else if (type == ItemRenderType.EQUIPPED) {
            GL11.glTranslatef(0.9f, 0, 0);
            GL11.glScalef(1.5F, 1.5F, 1.5F);
            GL11.glRotatef(90, 0, 1, 0);
            GL11.glRotatef(-50, 1, 0, 0);
        } else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glTranslatef(0.7f, 0, 0);
            GL11.glRotatef(90, 0, 1, 0);
            GL11.glRotatef(-30, 1, 0, 0);
        }

        MODEL.renderAll();

        mc.getTextureManager()
            .bindTexture(TextureMap.locationItemsTexture);
        GL11.glPopMatrix();
    }
}
