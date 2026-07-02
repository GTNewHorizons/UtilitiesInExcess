package com.fouristhenumber.utilitiesinexcess.common.renderers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.utils.Color;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemPaintbrush;
import com.gtnewhorizon.gtnhlib.util.ItemRenderUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PaintbrushRenderer implements IItemRenderer {

    @Override
    public boolean handleRenderType(final ItemStack item, final ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(final ItemRenderType type, final ItemStack item,
        final ItemRendererHelper helper) {
        return switch (helper) {
            case ENTITY_ROTATION, ENTITY_BOBBING -> true;
            default -> false;
        };
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (!(stack.getItem() instanceof ItemPaintbrush paintbrush)) return;
        int color = ItemPaintbrush.getColorFromStack(stack);

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);

        ItemRenderUtil.applyStandardItemTransform(type);

        GL11.glColor3f(Color.getRedF(color), Color.getGreenF(color), Color.getBlueF(color));
        ItemRenderUtil.renderItem(type, paintbrush.featherIcon);
        GL11.glColor3f(1f, 1f, 1f);
        ItemRenderUtil.renderItem(type, paintbrush.handleIcon);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }
}
