package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;

import java.awt.image.BufferedImage;

import net.minecraft.util.ScreenShotHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.fouristhenumber.utilitiesinexcess.render.TESRTrueGreenscreen;

@Mixin(ScreenShotHelper.class)
public class MixinScreenShotHelper_TrueGreenscreen {

    @ModifyArg(
        method = "saveScreenshot(Ljava/io/File;Ljava/lang/String;IILnet/minecraft/client/shader/Framebuffer;)Lnet/minecraft/util/IChatComponent;",
        at = @At(value = "INVOKE", target = "Ljava/awt/image/BufferedImage;<init>(III)V"),
        index = 2)
    private static int uie$replaceImageType(int in) {
        if (TESRTrueGreenscreen.inFrame) {
            return BufferedImage.TYPE_INT_ARGB;
        }
        return in;
    }
}
