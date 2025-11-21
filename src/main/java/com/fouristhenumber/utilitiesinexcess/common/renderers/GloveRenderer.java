package com.fouristhenumber.utilitiesinexcess.common.renderers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemGlove;
import com.fouristhenumber.utilitiesinexcess.utils.RenderableCube;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GloveRenderer implements IItemRenderer {

    @Override
    public boolean handleRenderType(final ItemStack item, final ItemRenderType type) {
        return type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.EQUIPPED;
    }

    @Override
    public boolean shouldUseRenderHelper(final ItemRenderType type, final ItemStack item,
        final ItemRendererHelper helper) {
        return type == ItemRenderType.INVENTORY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {

    }

    public static final RenderableCube cube = new RenderableCube(
        0,
        0,
        0,
        1,
        1,
        1,
        new float[][] { { 8, 8, 16, 16 }, { 16, 8, 24, 16 }, { 0, 8, 8, 16 }, { 8, 0, 16, 8 }, { 0, 0, 8, 8 },
            { 16, 0, 24, 8 } });
    public static final ResourceLocation gloveTexture = new ResourceLocation(
        "utilitiesinexcess",
        "textures/items/glove_model.png");

    public static void renderGloveAsBauble() {
        int previousTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

        // 0.27 -0.73 1.43 -0.5 0 0 0 1
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glScalef(0.27F, 0.27F, 0.27F);
        GL11.glTranslatef(-0.73F, 1.43F, -0.5F);

        mc.renderEngine.bindTexture(gloveTexture);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        cube.draw(t, 0, 0, 0, 24);
        t.draw();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTex);
    }

    private static IIcon gloveIcon;

    public static void renderGloveHudIcon() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (!ItemGlove.isUsingGlove(player)) return;

        if (gloveIcon == null) {
            gloveIcon = new ItemStack(ModItems.GLOVE.get()).getIconIndex();
        }

        Tessellator t = Tessellator.instance;
        ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        mc.getTextureManager()
            .bindTexture(TextureMap.locationItemsTexture);

        GL11.glPushMatrix();

        float x = (scaledResolution.getScaledWidth() / 2F) + 2, y = (scaledResolution.getScaledHeight() / 2F) + 2,
            z = 0;
        float width = 12;
        float height = 12;
        t.startDrawingQuads();
        t.addVertexWithUV(x, y + height, z, gloveIcon.getMinU(), gloveIcon.getMaxV());
        t.addVertexWithUV(x + width, y + height, z, gloveIcon.getMaxU(), gloveIcon.getMaxV());
        t.addVertexWithUV(x + width, y, z, gloveIcon.getMaxU(), gloveIcon.getMinV());
        t.addVertexWithUV(x, y, z, gloveIcon.getMinU(), gloveIcon.getMinV());
        t.draw();

        GL11.glPopMatrix();
    }
}
