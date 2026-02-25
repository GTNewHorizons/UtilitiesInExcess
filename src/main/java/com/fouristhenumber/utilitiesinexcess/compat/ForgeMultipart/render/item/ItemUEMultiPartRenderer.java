package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.item;

import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Content.partNames;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Content;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.UEMultipart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.TextureUtils;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.MicroMaterialRegistry;

public class ItemUEMultiPartRenderer implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (!item.hasTagCompound()) {
            return;
        }

        GL11.glPushMatrix();
        if (type == ItemRenderType.ENTITY) {
            GL11.glScaled(0.5, 0.5, 0.5);
        }

        if (type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY) {
            GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        }

        TextureUtils.bindAtlas(0);
        CCRenderState state = CCRenderState.instance();
        state.resetInstance();
        state.useNormals = true;
        state.pullLightmapInstance();
        state.startDrawingInstance();

        int materialId = MicroMaterialRegistry.materialID(
            item.getTagCompound()
                .getString("mat"));
        UEMultipart part = new Content().createUEMultiPart(true, materialId, 0, partNames[item.getItemDamage()]);
        part.render(new Vector3(0, 0, 0), -1);

        state.drawInstance();
        GL11.glPopMatrix();
    }
}
