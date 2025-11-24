package com.fouristhenumber.utilitiesinexcess.common.renderers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import com.fouristhenumber.utilitiesinexcess.common.items.ItemHeavenlyRing;
import com.fouristhenumber.utilitiesinexcess.utils.UIEUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HeavenlyRingRenderer {

    private static final float WING_MIN = 10F;
    private static final float WING_MAX = 25F;

    public static float getNextAngle(float curAngle, boolean isFlying) {
        float speed;
        float max;
        if (isFlying) {
            speed = 3F;
            max = WING_MAX;
        } else {
            speed = 1.5F;
            max = WING_MAX / 2;
        }
        long time = Minecraft.getSystemTime();
        return UIEUtils.lerp(curAngle, (float) ((Math.sin(time * 0.001 * speed) + 1) * max), 0.1F);
    }

    public static void render(int meta, float angle) {
        Tessellator t = Tessellator.instance;
        int boundTexIndex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TextureMap.locationItemsTexture);
        IIcon icon = ItemHeavenlyRing.wingIcons[meta];
        GL11.glPushMatrix();

        // 1.5 3 0.1 20 25 0.001
        // Left wing
        GL11.glTranslatef(0.0625F, -0.3125F, 0.125F);
        GL11.glRotatef(-(WING_MIN + angle), 0, 1, 0);
        t.startDrawingQuads();
        t.addVertexWithUV(0, 1, 0, icon.getMinU(), icon.getMaxV());
        t.addVertexWithUV(1, 1, 0, icon.getMaxU(), icon.getMaxV());
        t.addVertexWithUV(1, 0, 0, icon.getMaxU(), icon.getMinV());
        t.addVertexWithUV(0, 0, 0, icon.getMinU(), icon.getMinV());
        t.draw();

        GL11.glRotatef((WING_MIN + angle), 0, 1, 0);
        GL11.glTranslatef(-0.0625F, 0.3125F, -0.125F);

        GL11.glTranslatef(-0.0625F, -0.3125F, 0.125F);
        GL11.glRotatef((-180 + WING_MIN) + angle, 0, 1, 0);
        // Right wing
        t.startDrawingQuads();
        t.addVertexWithUV(0, 1, 0, icon.getMinU(), icon.getMaxV());
        t.addVertexWithUV(1, 1, 0, icon.getMaxU(), icon.getMaxV());
        t.addVertexWithUV(1, 0, 0, icon.getMaxU(), icon.getMinV());
        t.addVertexWithUV(0, 0, 0, icon.getMinU(), icon.getMinV());
        t.draw();

        GL11.glPopMatrix();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, boundTexIndex);
    }
}
