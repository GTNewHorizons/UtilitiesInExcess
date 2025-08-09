package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors;

import net.minecraft.village.MerchantRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MerchantRecipe.class)
public interface AccessorMerchantRecipe {
    @Accessor(value = "toolUses")
    int getCurrentUses();
    @Accessor(value = "maxTradeUses")
    int getMaxUses();
}
