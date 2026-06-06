package com.fouristhenumber.utilitiesinexcess.common.renderers;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig;
import com.gtnewhorizon.gtnhlib.util.ItemRenderUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InvertedIngotRenderer implements IItemRenderer {

    @Override
    public boolean handleRenderType(final ItemStack item, final ItemRenderType type) {
        return item.getItemDamage() == 0;
    }

    @Override
    public boolean shouldUseRenderHelper(final ItemRenderType type, final ItemStack item,
        final ItemRendererHelper helper) {
        return type == ItemRenderType.ENTITY && helper == ItemRendererHelper.ENTITY_BOBBING
            || (helper == ItemRendererHelper.ENTITY_ROTATION && Minecraft.getMinecraft().gameSettings.fancyGraphics);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (stack == null) return;

        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glEnable(GL11.GL_BLEND);

        IIcon icon = stack.getItem()
            .getIconFromDamageForRenderPass(stack.getItemDamage(), 0);

        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey("ImplosionTimer")) {

            World world = Minecraft.getMinecraft().theWorld;
            if (world == null) return;

            int remaining = tag.getInteger("ImplosionTimer");

            float progress = MathHelper
                .clamp_float((float) remaining / InversionConfig.invertedIngotImplosionTimer, 0f, 1f);

            float r = 1F;
            float g = MathHelper.clamp_float(progress, 0f, 1f);
            float b = MathHelper.clamp_float(progress, 0f, 1f);

            if (remaining < 60) {
                int blink = remaining / 10;
                if (blink == 1 || blink == 3 || blink == 5) {
                    g = 0.9f;
                    b = 0f;
                }
            }

            GL11.glColor3f(r, g, b);
        }

        ItemRenderUtil.applyStandardItemTransform(type);
        ItemRenderUtil.renderItem(type, icon);

        GL11.glPopAttrib();
    }
}
