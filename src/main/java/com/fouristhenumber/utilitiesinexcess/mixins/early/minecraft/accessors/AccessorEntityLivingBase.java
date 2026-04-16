package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityLivingBase.class)
public interface AccessorEntityLivingBase {

    @Accessor(value = "lastDamage")
    float getLastDamage();

    @Accessor(value = "lastDamage")
    void setLastDamage(float value);

    @Invoker("getExperiencePoints")
    int accessGetExperiencePoints(EntityPlayer player);
}
