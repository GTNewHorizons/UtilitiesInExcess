package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;

import net.minecraft.client.gui.GuiIngame;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fouristhenumber.utilitiesinexcess.render.TESRTrueGreenscreen;

@Mixin(GuiIngame.class)
public class MixinGuiIngame_TrueGreenscreen {

    @Inject(
        method = "renderVignette(FII)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/OpenGlHelper;glBlendFunc(IIII)V",
            ordinal = 0))
    private static void uie$noAlphaVignette(float p_73829_1_, int p_73829_2_, int p_73829_3_, CallbackInfo ci) {
        if (TESRTrueGreenscreen.inFrame) {
            GL11.glColorMask(true, true, true, false);
        }
    }

    @Inject(
        method = "renderVignette(FII)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/OpenGlHelper;glBlendFunc(IIII)V",
            ordinal = 1))
    private static void uie$noAlphaVignette2(float p_73829_1_, int p_73829_2_, int p_73829_3_, CallbackInfo ci) {
        if (TESRTrueGreenscreen.inFrame) {
            GL11.glColorMask(true, true, true, true);
        }
    }
}
