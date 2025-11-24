package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors;

import net.minecraft.client.renderer.ItemRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderer.class)
public interface AccessorItemRenderer {

    @Accessor("equippedProgress")
    void uie$setEquippedProgress(float equippedProgress);

    @Accessor("prevEquippedProgress")
    void uie$setPrevEquippedProgress(float prevEquippedProgress);
}
