package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;

import java.util.Map;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColored;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;

@Mixin(TextureMap.class)
public class MixinTextureMap_ColoredBlocks {

    @Shadow
    @Final
    private Map<String, TextureAtlasSprite> mapRegisteredSprites;

    @Shadow
    @Final
    private String basePath;

    @Inject(
        method = "loadTextureAtlas(Lnet/minecraft/client/resources/IResourceManager;)V",
        at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V", remap = false, shift = At.Shift.AFTER))
    private void uie$loadColoredBlockTextures(IResourceManager manager, CallbackInfo ci) {
        if (basePath.equals("textures/blocks") && Loader.instance()
            .getLoaderState() == LoaderState.AVAILABLE) {
            BlockColored.initColoredBlocks(mapRegisteredSprites);
        }
    }
}
