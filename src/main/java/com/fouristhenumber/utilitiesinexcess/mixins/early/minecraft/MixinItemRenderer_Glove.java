package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.fouristhenumber.utilitiesinexcess.common.items.ItemGlove;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorItemRenderer;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer_Glove {

    @Expression("? != null")
    @WrapOperation(method = "renderItemInFirstPerson", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 5))
    private boolean uie$renderFirstPersonArm(Object left, Object right, Operation<Boolean> original) {
        ItemRenderer thisObject = (ItemRenderer) (Object) this;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        AccessorItemRenderer air = (AccessorItemRenderer) thisObject;

        if (player == null) original.call(left, right);

        if ((player.getHeldItem() != null && player.getHeldItem()
            .getItem() instanceof ItemGlove)) {
            return false;
        }
        return original.call(left, right);
    }
}
