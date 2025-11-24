// package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;
//
// import net.minecraft.client.entity.EntityClientPlayerMP;
//
// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.injection.At;
// import org.spongepowered.asm.mixin.injection.Inject;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
// import com.fouristhenumber.utilitiesinexcess.common.renderers.HeavenlyRingRenderer;
//
//
// This class is for modifying client side variables at runtime using chat messages
// The locations and sizes of the glove and heavenly ring renders for example
// @Mixin(EntityClientPlayerMP.class)
// public class MixinEntityClientPlayerMP_Testing {
//
// @Inject(method = "sendChatMessage", at = @At("HEAD"))
// private void uie$captureMessage(String p_71165_1_, CallbackInfo ci) {
// HeavenlyRingRenderer.dostuff(p_71165_1_);
// }
// }
