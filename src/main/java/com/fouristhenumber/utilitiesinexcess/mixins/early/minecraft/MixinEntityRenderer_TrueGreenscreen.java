package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;

import net.minecraft.client.renderer.EntityRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.fouristhenumber.utilitiesinexcess.render.TESRTrueGreenscreen;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer_TrueGreenscreen {

    @ModifyArg(
        method = "updateFogColor(F)V",
        at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glClearColor(FFFF)V"),
        index = 3)
    private static float uie$clearColorAlpha(float alpha) {
        if (TESRTrueGreenscreen.inFrame) {
            return 1F;
        }
        return 0F;
    }

}
