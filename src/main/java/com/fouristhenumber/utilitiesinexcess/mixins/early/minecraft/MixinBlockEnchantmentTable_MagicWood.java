package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(BlockEnchantmentTable.class)
public class MixinBlockEnchantmentTable_MagicWood {

    // Wraps World.getBlock() to avoid a mixin into an if-statement
    @WrapOperation(
        method = "Lnet/minecraft/block/BlockEnchantmentTable;randomDisplayTick(Lnet/minecraft/world/World;IIILjava/util/Random;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block uie$addMagicWoodParticles(World world, int x, int y, int z, Operation<Block> original) {
        Block block = original.call(world, x, y, z);
        if (block == ModBlocks.MAGIC_WOOD.get()) return Blocks.bookshelf;
        return block;
    }
}
