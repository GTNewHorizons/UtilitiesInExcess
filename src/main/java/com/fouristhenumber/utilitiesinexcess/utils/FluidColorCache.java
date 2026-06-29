package com.fouristhenumber.utilitiesinexcess.utils;

import java.nio.IntBuffer;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FluidColorCache {

    // Mapping of average colors for each fluid in registry. Used for the color bands on drums.
    private static final HashMap<Fluid, Integer> colorMap = new HashMap<>();

    public static void buildCache() {
        TextureMap blockAtlas = Minecraft.getMinecraft()
            .getTextureMapBlocks();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, blockAtlas.getGlTextureId());

        int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        IntBuffer buf = BufferUtils.createIntBuffer(width * height);
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buf);

        for (Fluid fluid : FluidRegistry.getRegisteredFluids()
            .values()) {
            // Some fluid implementations provide a useful value for getColor(), but they often don't.
            int fluidColor = fluid.getColor();
            if (fluidColor != 0xFFFFFF) {
                colorMap.put(fluid, fluidColor);
                continue;
            }
            IIcon icon = fluid.getStillIcon();
            if (icon == null) icon = fluid.getFlowingIcon();
            if (!(icon instanceof TextureAtlasSprite sprite)) continue;

            int ox = sprite.getOriginX();
            int oy = sprite.getOriginY();
            int iw = sprite.getIconWidth();
            int ih = sprite.getIconHeight();

            if (ox < 0 || oy < 0 || iw <= 0 || ih <= 0 || ox + iw > width || oy + ih > height) continue;

            int count = 0, r = 0, g = 0, b = 0;
            for (int row = 0; row < ih; row++) {
                int rowOff = (oy + row) * width + ox;
                for (int col = 0; col < iw; col++) {
                    int argb = buf.get(rowOff + col);
                    int alpha = (argb >> 24) & 0xFF;
                    if (alpha == 0) continue;
                    count++;
                    r += (argb >> 16) & 0xFF;
                    g += (argb >> 8) & 0xFF;
                    b += argb & 0xFF;
                }
            }

            if (count == 0) continue;
            int color = ((r / count) << 16) | ((g / count) << 8) | (b / count);
            colorMap.put(fluid, color);
        }
        // Hardcode vanilla fluids because they are dumb, as always.
        colorMap.put(FluidRegistry.WATER, 0x3F76E4);
        colorMap.put(FluidRegistry.LAVA, 0xE85B1A);
    }

    public static int getColor(Fluid fluid) {
        return colorMap.getOrDefault(fluid, 0xFFFFFF);
    }

    @SuppressWarnings("unused")
    @EventBusSubscriber(side = Side.CLIENT)
    public static class Events {

        @EventBusSubscriber.Condition
        public static boolean shouldSubscribe() {
            return BlockConfig.enableDrum;
        }

        @SubscribeEvent
        public static void postTextureStitch(TextureStitchEvent.Post event) {
            if (event.map != Minecraft.getMinecraft()
                .getTextureMapBlocks()) return;
            FluidColorCache.buildCache();
        }
    }
}
