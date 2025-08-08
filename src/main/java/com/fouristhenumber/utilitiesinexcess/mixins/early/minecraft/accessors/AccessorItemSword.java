package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors;

import net.minecraft.item.ItemSword;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemSword.class)
public interface AccessorItemSword {

    @Accessor(value = "field_150934_a")
    public float getDamageVsEntity_uie();

    @Accessor(value = "field_150934_a")
    public void setDamageVsEntity_uie(float value);
}
