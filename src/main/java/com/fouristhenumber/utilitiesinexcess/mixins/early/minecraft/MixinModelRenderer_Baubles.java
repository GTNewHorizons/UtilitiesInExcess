package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fouristhenumber.utilitiesinexcess.common.items.ItemGlove;
import com.fouristhenumber.utilitiesinexcess.common.renderers.GloveRenderer;
import com.fouristhenumber.utilitiesinexcess.utils.ModelPartRenderHelper;
import com.fouristhenumber.utilitiesinexcess.utils.UIEUtils;

@Mixin(RenderPlayer.class)
public class MixinModelRenderer_Baubles {

    @Inject(
        method = "renderFirstPersonArm",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/ModelRenderer;render(F)V",
            shift = At.Shift.AFTER),
        remap = false)
    private void uie$renderFirstPersonArm(EntityPlayer player, CallbackInfo ci) {
        RenderPlayer thisObject = (RenderPlayer) (Object) this;

        if (player == null) return;

        ItemStack stack = player.getHeldItem();
        if (stack == null || !(stack.getItem() instanceof ItemGlove))
            stack = UIEUtils.getBauble(player, ItemGlove.class);

        if (stack != null) {
            ModelPartRenderHelper.renderBipedPart(
                0.0625F,
                thisObject.modelBipedMain.bipedRightArm,
                GloveRenderer::renderGloveAsBauble,
                stack.getItemDamage());
        }
    }
}
