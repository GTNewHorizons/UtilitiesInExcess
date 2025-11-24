package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fouristhenumber.utilitiesinexcess.common.items.ItemGlove;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemHeavenlyRing;
import com.fouristhenumber.utilitiesinexcess.common.renderers.GloveRenderer;
import com.fouristhenumber.utilitiesinexcess.common.renderers.HeavenlyRingRenderer;
import com.fouristhenumber.utilitiesinexcess.utils.ModelPartRenderHelper;
import com.fouristhenumber.utilitiesinexcess.utils.UIEUtils;

@Mixin(ModelBiped.class)
public class MixinModelBiped_Baubles {
    // This mixin should allow to render anything attached to any player body part
    // Simply use ModelPartRenderHelper.renderBipedPart with the correct bodypart (instead of thisObject.bipedRightArm)
    // inside uie$doExtraRender and do your rendering inside the runnable.

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/ModelRenderer;render(F)V",
            shift = At.Shift.AFTER,
            ordinal = 6),
        remap = false)
    private void uie$render1(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_,
        float p_78088_6_, float p_78088_7_, CallbackInfo ci) {
        uie$doExtraRender(p_78088_1_, p_78088_7_);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/ModelRenderer;render(F)V",
            shift = At.Shift.AFTER,
            ordinal = 13),
        remap = false)
    private void uie$render2(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_,
        float p_78088_6_, float p_78088_7_, CallbackInfo ci) {
        uie$doExtraRender(p_78088_1_, p_78088_7_);
    }

    private float uie$heavenlyRingWing = 1;

    private void uie$doExtraRender(Entity p_78088_1_, float p_78088_7_) {
        ModelBiped thisObject = (ModelBiped) (Object) this;

        if (p_78088_1_ == null) return;
        if (!(p_78088_1_ instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) p_78088_1_;

        if ((player.getHeldItem() != null && player.getHeldItem()
            .getItem() instanceof ItemGlove) || UIEUtils.hasBauble(player, ItemGlove.class)) {
            ModelPartRenderHelper
                .renderBipedPart(p_78088_7_, thisObject.bipedRightArm, GloveRenderer::renderGloveAsBauble);
        }

        ItemStack ring = player.getHeldItem();
        if (ring == null || !(ring.getItem() instanceof ItemHeavenlyRing))
            ring = UIEUtils.getBauble(player, ItemHeavenlyRing.class);
        if (ring != null) {
            final ItemStack finalRing = ring;
            uie$heavenlyRingWing = HeavenlyRingRenderer
                .getNextAngle(uie$heavenlyRingWing, player.capabilities.isFlying);
            ModelPartRenderHelper.renderBipedPart(
                p_78088_7_,
                thisObject.bipedBody,
                () -> { HeavenlyRingRenderer.render(finalRing.getItemDamage(), uie$heavenlyRingWing); });
        }
    }
}
