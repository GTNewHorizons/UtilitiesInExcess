package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors;

import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityLivingBase.class)
public interface AccessorEntityLivingBase {

    @Accessor(value = "lastDamage")
    float uie$getLastDamage();

    @Accessor(value = "lastDamage")
    void uie$setLastDamage(float value);
}
