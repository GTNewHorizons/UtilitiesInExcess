package com.fouristhenumber.utilitiesinexcess.compat.angelica.coloredblocks;

import java.awt.image.BufferedImage;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

import com.fouristhenumber.utilitiesinexcess.render.BlockColoredTexture;
import com.prupe.mcpatcher.MCLogger;
import com.prupe.mcpatcher.mal.tile.TileLoader;

import jss.notfine.config.MCPatcherForgeConfig;

public class TileLoaderColored extends TileLoader {

    private final float brightnessMultiplier;

    public TileLoaderColored(String mapName, MCLogger logger, float brightnessMultiplier) {
        super(mapName, logger);
        this.brightnessMultiplier = brightnessMultiplier;
    }

    @Override
    public BufferedImage loadResourceImage(ResourceLocation resource, boolean alternate) {
        BufferedImage image = null;
        if (!MCPatcherForgeConfig.ConnectedTextures.debugTextures) {
            image = BlockColoredTexture.generateGrayscaleImage(
                new ResourceLocation(
                    resource.getResourceDomain(),
                    resource.getResourcePath()
                        .replace("/___UIE_COLORED___", "")),
                brightnessMultiplier);
            if (image == null) {
                subLogger.warning("missing %s", resource);
            }
        }
        if (image == null) {
            image = generateDebugTexture(resource.getResourcePath(), 64, 64, alternate);
        }
        return image;
    }

    @Override
    protected TextureAtlasSprite registerIconToMap(TextureMap textureMap, ResourceLocation resource, String name) {
        TextureAtlasSprite icon = new BlockColoredTexture(name, brightnessMultiplier);
        textureMap.setTextureEntry(name, icon);
        return icon;
    }

    @Override
    public boolean ignoreMissingTextures() {
        return true;
    }

}
