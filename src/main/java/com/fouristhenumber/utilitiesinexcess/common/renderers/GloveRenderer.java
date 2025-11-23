package com.fouristhenumber.utilitiesinexcess.common.renderers;

import static com.fouristhenumber.utilitiesinexcess.utils.RenderUtils.renderItemIcon;

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
        return type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.EQUIPPED
            || type == ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelper(final ItemRenderType type, final ItemStack item,
        final ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (type != ItemRenderType.INVENTORY) return;
        if (!(stack.getItem() instanceof ItemGlove glove)) return;
        int meta = stack.getItemDamage();

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);

        float[] rgb = woolMetaToRGB(meta / 16);
        GL11.glColor3f(rgb[0], rgb[1], rgb[2]);
        renderItemIcon(glove.topIcon, 0.0D, 0.0D, 16.0D, 16.0D, 0.001, 0.0F, 0.0F, -1.0F);
        rgb = woolMetaToRGB(meta % 16);
        GL11.glColor3f(rgb[0], rgb[1], rgb[2]);
        renderItemIcon(glove.bottomIcon, 0.0D, 0.0D, 16.0D, 16.0D, 0.001, 0.0F, 0.0F, -1.0F);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

    public static float[] woolMetaToRGB(int dyeMeta) {

        return switch (dyeMeta) {
            case 0 -> new float[] { 0.917F, 0.929F, 0.929F };
            case 1 -> new float[] { 0.945F, 0.466F, 0.086F };
            case 2 -> new float[] { 0.745F, 0.274F, 0.709F };
            case 3 -> new float[] { 0.274F, 0.690F, 0.854F };
            case 4 -> new float[] { 0.976F, 0.776F, 0.160F };
            case 5 -> new float[] { 0.443F, 0.729F, 0.101F };
            case 6 -> new float[] { 0.933F, 0.564F, 0.678F };
            case 7 -> new float[] { 0.247F, 0.270F, 0.282F };
            case 8 -> new float[] { 0.556F, 0.560F, 0.529F };
            case 9 -> new float[] { 0.082F, 0.541F, 0.568F };
            case 10 -> new float[] { 0.482F, 0.168F, 0.678F };
            case 11 -> new float[] { 0.207F, 0.227F, 0.619F };
            case 12 -> new float[] { 0.450F, 0.282F, 0.160F };
            case 13 -> new float[] { 0.333F, 0.431F, 0.109F };
            case 14 -> new float[] { 0.631F, 0.156F, 0.137F };
            case 15 -> new float[] { 0.086F, 0.086F, 0.105F };
            default -> new float[] { 0, 0, 0 };
        };
    }

    public static final RenderableCube topCube = new RenderableCube(
        0,
        0,
        0,
        1,
        1,
        1,
        new float[][] { { 8, 8, 16, 16 }, { 16, 8, 24, 16 }, { 0, 8, 8, 16 }, { 8, 0, 16, 8 }, { 0, 0, 8, 8 },
            { 16, 0, 24, 8 } });

    public static final RenderableCube bottomCube = new RenderableCube(
        0,
        0,
        0,
        1,
        0.25,
        1,
        new float[][] { { 8, 8, 16, 16 }, { 8, 8, 16, 16 }, { 0, 16, 8, 18 }, { 0, 16, 8, 18 }, { 0, 16, 8, 18 },
            { 0, 16, 8, 18 } });

    public static final ResourceLocation gloveTexture = new ResourceLocation(
        "utilitiesinexcess",
        "textures/items/glove_model.png");

    public static void renderGloveAsBauble(int meta) {
        int previousTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

        // 0.27 -0.73 1.43 -0.5 0 0 0 1
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glScalef(0.27F, 0.27F, 0.27F);
        GL11.glTranslatef(-0.73F, 1.43F, -0.5F);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        mc.renderEngine.bindTexture(gloveTexture);
        Tessellator t = Tessellator.instance;
        float[] rgb = woolMetaToRGB(meta / 16);
        t.startDrawingQuads();
        t.setColorOpaque_F(rgb[0], rgb[1], rgb[2]);
        topCube.draw(t, 0, 0, 0, 24, false);
        t.draw();

        GL11.glTranslatef(0, -0.25F, 0F);
        rgb = woolMetaToRGB(meta % 16);
        t.startDrawingQuads();
        t.setColorOpaque_F(rgb[0], rgb[1], rgb[2]);
        bottomCube.draw(t, 0, 0, 0, 24, false);
        t.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
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
