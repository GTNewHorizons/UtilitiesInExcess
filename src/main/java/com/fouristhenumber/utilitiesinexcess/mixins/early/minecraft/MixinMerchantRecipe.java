package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.IFavoritable;

@Mixin(MerchantRecipe.class)
public class MixinMerchantRecipe implements IFavoritable {

    @Unique
    public boolean uie$favorite;

    @Override
    public boolean uie$getFavorite() {
        return uie$favorite;
    }

    @Override
    public void uie$setFavorite(boolean value) {
        uie$favorite = value;
    }

    @Inject(method = "readFromTags", at = @At(value = "TAIL"))
    public void uie$readFromTags(NBTTagCompound compound, CallbackInfo ci) {
        uie$setFavorite(compound.getBoolean("favorite"));
    }

    @Inject(method = "writeToTags", at = @At(value = "TAIL"))
    public void uie$writeToTags(CallbackInfoReturnable<NBTTagCompound> cir) {
        NBTTagCompound compound = cir.getReturnValue();
        compound.setBoolean("favorite", uie$getFavorite());
    }
}
