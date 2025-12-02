package com.fouristhenumber.utilitiesinexcess.common.renderers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig;

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
        IIcon icon = stack.getItem()
            .getIconFromDamageForRenderPass(stack.getItemDamage(), 0);

        Tessellator tess = Tessellator.instance;
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float minU = icon.getMinU();
        float maxV = icon.getMaxV();

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

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor3f(r, g, b);
        }

        switch (type) {
            case ENTITY -> {
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

                if (RenderItem.renderInFrame) {
                    GL11.glScalef(1.025641F, 1.025641F, 1.025641F);
                    GL11.glTranslatef(0.0F, -0.05F, 0.0F);
                }

                if (Minecraft.getMinecraft().gameSettings.fancyGraphics) {
                    if (RenderItem.renderInFrame) {
                        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                    }
                    GL11.glTranslatef(-0.5F, -0.25F, 0.0421875F);

                    ItemRenderer.renderItemIn2D(
                        tess,
                        maxU,
                        minV,
                        minU,
                        maxV,
                        icon.getIconWidth(),
                        icon.getIconHeight(),
                        0.0625F);
                } else {
                    tess.startDrawingQuads();
                    tess.setNormal(0.0F, 1.0F, 0.0F);
                    tess.addVertexWithUV(0.0F - 0.5F, 0.0F - 0.25F, 0.0D, minU, maxV);
                    tess.addVertexWithUV(1.0F - 0.5F, 0.0F - 0.25F, 0.0D, maxU, maxV);
                    tess.addVertexWithUV(1.0F - 0.5F, 1.0F - 0.25F, 0.0D, maxU, minV);
                    tess.addVertexWithUV(0.0F - 0.5F, 1.0F - 0.25F, 0.0D, minU, minV);
                    tess.draw();
                }
            }
            case EQUIPPED, EQUIPPED_FIRST_PERSON -> {
                ItemRenderer
                    .renderItemIn2D(tess, maxU, minV, minU, maxV, icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
            }
            case INVENTORY -> {
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                renderItemIcon(icon, 0.0D, 0.0D, 16.0D, 16.0D, 0.001, 0.0F, 0.0F, -1.0F);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
            }
            default -> {}
        }

        GL11.glPopAttrib();
    }

    public static void renderItemIcon(IIcon icon, double xStart, double yStart, double xEnd, double yEnd, double z,
        float nx, float ny, float nz) {
        if (icon == null) {
            return;
        }
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glEnable(GL11.GL_BLEND);
        final Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.setNormal(nx, ny, nz);
        if (nz > 0.0F) {
            tess.addVertexWithUV(xStart, yStart, z, icon.getMinU(), icon.getMinV());
            tess.addVertexWithUV(xEnd, yStart, z, icon.getMaxU(), icon.getMinV());
            tess.addVertexWithUV(xEnd, yEnd, z, icon.getMaxU(), icon.getMaxV());
            tess.addVertexWithUV(xStart, yEnd, z, icon.getMinU(), icon.getMaxV());
        } else {
            tess.addVertexWithUV(xStart, yEnd, z, icon.getMinU(), icon.getMaxV());
            tess.addVertexWithUV(xEnd, yEnd, z, icon.getMaxU(), icon.getMaxV());
            tess.addVertexWithUV(xEnd, yStart, z, icon.getMaxU(), icon.getMinV());
            tess.addVertexWithUV(xStart, yStart, z, icon.getMinU(), icon.getMinV());
        }
        tess.draw();
    }
}
