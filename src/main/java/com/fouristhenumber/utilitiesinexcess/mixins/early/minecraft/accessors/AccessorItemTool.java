package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors;

import net.minecraft.item.ItemTool;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemTool.class)
public interface AccessorItemTool {

    @Accessor(value = "damageVsEntity")
    float getDamageVsEntity();

    @Accessor(value = "damageVsEntity")
    void setDamageVsEntity(float value);
}
