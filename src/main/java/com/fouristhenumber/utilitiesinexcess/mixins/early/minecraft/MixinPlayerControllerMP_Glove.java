package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemGlove;
import com.fouristhenumber.utilitiesinexcess.utils.UIEUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP_Glove {

    @WrapOperation(
        method = "onPlayerRightClick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldSettings$GameType;isCreative()Z"))
    private boolean uie$renderFirstPersonArm(WorldSettings.GameType instance, Operation<Boolean> original,
        @Local(argsOnly = true) EntityPlayer player) {
        if (UIEUtils.hasBauble(player, ItemGlove.class) && UtilitiesInExcess.proxy.GLOVE_KEYBIND.isKeyDown(player)) {
            return true;
        }
        return original.call(instance);
    }
}
