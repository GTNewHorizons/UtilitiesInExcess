package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors;

import net.minecraft.item.ItemTool;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemTool.class)
public interface AccessorItemTool {

    @Accessor(value = "damageVsEntity")
    public float getDamageVsEntity_uie();

    @Accessor(value = "damageVsEntity")
    public void setDamageVsEntity_uie(float value);
}
