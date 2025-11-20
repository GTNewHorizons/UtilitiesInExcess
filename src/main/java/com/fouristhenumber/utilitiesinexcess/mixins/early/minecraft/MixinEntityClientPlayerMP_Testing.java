package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;

import net.minecraft.client.entity.EntityClientPlayerMP;

import org.spongepowered.asm.mixin.Mixin;

// Added for easy testing, can be used to change values client-side at runtime instead of hotswap
@Mixin(EntityClientPlayerMP.class)
public class MixinEntityClientPlayerMP_Testing {

    // @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true, remap = false)
    // private void uie$chatMessage(String message, CallbackInfo ci) {}
}
