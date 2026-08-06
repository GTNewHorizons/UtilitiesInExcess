package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fouristhenumber.utilitiesinexcess.render.TESRTrueGreenscreen;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal_TrueGreenscreen {

    // There is a bug that causes the player to see through tile entities when translucent entities (like dropped
    // stained glass items) are in front of them. So this is required to draw the green screen blocks before other
    // entities. And also to work around a different bug detailed in TESRTrueGreenscreen.
    @Inject(
        method = "renderEntities(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/culling/ICamera;F)V",
        at = @At(value = "HEAD"))
    private static void uie$preRenderEntitiesHook(EntityLivingBase p_147589_1_, ICamera p_147589_2_, float partialTicks,
        CallbackInfo ci) {
        TESRTrueGreenscreen.onPreRenderEntities(partialTicks);
    }
}
