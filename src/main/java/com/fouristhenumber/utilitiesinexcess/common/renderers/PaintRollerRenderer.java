package com.fouristhenumber.utilitiesinexcess.common.renderers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.utils.Color;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemPaintRoller;
import com.gtnewhorizon.gtnhlib.util.ItemRenderUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PaintRollerRenderer implements IItemRenderer {

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
        if (!(stack.getItem() instanceof ItemPaintRoller paintRoller)) return;
        int color = ItemPaintRoller.getColorFromStack(stack);

        ItemRenderUtil.applyStandardItemTransform(type);

        GL11.glColor3f(Color.getRedF(color), Color.getGreenF(color), Color.getBlueF(color));
        ItemRenderUtil.renderItem(type, paintRoller.featherIcon);
        GL11.glColor3f(1f, 1f, 1f);
        ItemRenderUtil.renderItem(type, paintRoller.handleIcon);
    }
}
