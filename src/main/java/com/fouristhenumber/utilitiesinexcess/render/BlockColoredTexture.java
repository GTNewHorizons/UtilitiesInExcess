package com.fouristhenumber.utilitiesinexcess.render;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorBlock;

public class BlockColoredTexture extends TextureAtlasSprite {

    private final Block base;
    private final float colorMultiplier;

    public BlockColoredTexture(String name, Block base, float colorMultiplier) {
        super(name);
        this.base = base;
        this.colorMultiplier = colorMultiplier;
    }

    @Override
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true;
    }

    @Override
    public boolean load(IResourceManager manager, ResourceLocation location) {
        try {
            String textureName = ((AccessorBlock) base).uie$getTextureName();
            textureName = textureName.equals("planks") ? textureName + "_oak" : textureName;
            textureName = textureName.equals("quartz_block") ? textureName + "_top" : textureName;
            textureName = textureName.equals("redstone_lamp_off") ? "redstone_lamp_on" : textureName;

            BufferedImage img = ImageIO.read(
                Minecraft.getMinecraft()
                    .getResourceManager()
                    .getResource(new ResourceLocation("textures/blocks/" + textureName + ".png"))
                    .getInputStream());
            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    int rgba = img.getRGB(x, y);
                    Color c = new Color(rgba, true);

                    // Apparently a simple average is wrong
                    // Something about the receptor concentration in our eyes ¯\_(ツ)_/¯
                    // int grey = (int) ((c.getGreen() + c.getRed() + c.getBlue()) * 0.333f);
                    int grey = (int) (c.getGreen() * 0.587 + c.getRed() * 0.299 + c.getBlue() * 0.114);
                    grey = (int) Math.min(grey * colorMultiplier, 255);

                    Color newColor = new Color(grey, grey, grey, c.getAlpha());
                    img.setRGB(x, y, newColor.getRGB());
                }
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
        } catch (IOException e) {
            return true;
        }
    }
}
