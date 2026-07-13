package com.fouristhenumber.utilitiesinexcess.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColored;

public class BlockColoredTexture extends TextureAtlasSprite {

    private final BlockColored block;
    private final float brightnessMultiplier;

    private String baseName;

    public BlockColoredTexture(String name, float brightnessMultiplier) {
        this(name, null, brightnessMultiplier);
    }

    public BlockColoredTexture(String name, String baseName, BlockColored block, float brightnessMultiplier) {
        this.baseName = baseName;
        this(name, block, brightnessMultiplier);
    }

    public BlockColoredTexture(String name, BlockColored block, float brightnessMultiplier) {
        super(name);
        this.block = block;
        this.brightnessMultiplier = brightnessMultiplier;
    }

    @Override
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true;
    }

    @Override
    public boolean load(IResourceManager manager, ResourceLocation location) {
        ResourceLocation baseLocation;
        if (baseName == null) {
            baseLocation = new ResourceLocation(getIconName().replace("/___UIE_COLORED___", ""));
        } else {
            ResourceLocation temp = new ResourceLocation(baseName);
            baseLocation = new ResourceLocation(
                temp.getResourceDomain(),
                "textures/blocks/" + temp.getResourcePath() + ".png");
        }

        BufferedImage img = generateGrayscaleImage(baseLocation, brightnessMultiplier);

        if (img == null) {
            return true;
        }

        this.height = img.getHeight();
        this.width = img.getWidth();

        int mipmapLevels = Minecraft.getMinecraft().gameSettings.mipmapLevels;
        int[][] imageData = new int[1 + mipmapLevels][];

        int[] rgbaData = new int[this.height * this.width];
        img.getRGB(0, 0, width, height, rgbaData, 0, width);
        imageData[0] = rgbaData;

        framesTextureData.add(imageData);
        return false;
    }

    public static BufferedImage generateGrayscaleImage(ResourceLocation location, float brightnessMultiplier) {
        try {
            BufferedImage img = ImageIO.read(
                Minecraft.getMinecraft()
                    .getResourceManager()
                    .getResource(location)
                    .getInputStream());
            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    int rgba = img.getRGB(x, y);
                    Color c = new Color(rgba, true);

                    // Apparently a simple average is wrong
                    // Something about the receptor concentration in our eyes ¯\_(ツ)_/¯
                    // int grey = (int) ((c.getGreen() + c.getRed() + c.getBlue()) * 0.333f);
                    int grey = (int) (c.getGreen() * 0.587 + c.getRed() * 0.299 + c.getBlue() * 0.114);
                    grey = (int) Math.min(grey * brightnessMultiplier, 255);

                    Color newColor = new Color(grey, grey, grey, c.getAlpha());
                    img.setRGB(x, y, newColor.getRGB());
                }
            }
            return img;
        } catch (IOException e) {
            return null;
        }
    }
}
