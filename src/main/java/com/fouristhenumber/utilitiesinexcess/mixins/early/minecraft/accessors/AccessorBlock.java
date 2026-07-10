package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
public interface AccessorBlock {

    @Accessor("unlocalizedName")
    String uie$getUnlocalizedNameRaw();

    @Accessor("blockMaterial")
    void uie$setBlockMaterial(Material material);
}
