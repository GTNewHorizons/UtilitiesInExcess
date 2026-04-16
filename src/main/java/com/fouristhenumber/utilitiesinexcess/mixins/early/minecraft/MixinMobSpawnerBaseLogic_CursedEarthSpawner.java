package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.world.World;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockCursedEarth;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(MobSpawnerBaseLogic.class)
public abstract class MixinMobSpawnerBaseLogic_CursedEarthSpawner {

    @Shadow
    public abstract World getSpawnerWorld();

    @Shadow
    public abstract int getSpawnerX();

    @Shadow
    public abstract int getSpawnerY();

    @Shadow
    public abstract int getSpawnerZ();

    @Shadow
    public int spawnDelay;

    @Unique
    private boolean uie$isCursedEarth;

    // If the block below is cursed earth, run the
    // spawner instead of depending on player location.
    @ModifyReturnValue(method = "isActivated", at = @At("RETURN"))
    private boolean uie$onIsActivated(boolean original) {
        Block blockBelow = this.getSpawnerWorld()
            .getBlock(this.getSpawnerX(), this.getSpawnerY() - 1, this.getSpawnerZ());
        uie$isCursedEarth = blockBelow instanceof BlockCursedEarth;
        return uie$isCursedEarth || original;
    }

    // Dividing the spawn time by 4 if
    // the block is cursed earth.
    @Inject(
        method = "resetTimer",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/tileentity/MobSpawnerBaseLogic;spawnDelay:I",
            opcode = Opcodes.PUTFIELD,
            shift = At.Shift.AFTER))
    private void uie$ChangeTime(CallbackInfo ci) {
        if (uie$isCursedEarth) {
            this.spawnDelay = this.spawnDelay / 4;
        }
    }

    // If the block is cursed earth, make the
    // creatures persistent and add potion effects.
    @ModifyArg(
        method = "func_98265_a",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;spawnEntityInWorld(Lnet/minecraft/entity/Entity;)Z"))
    private Entity uie$onEntitySpawned(Entity original) {
        if (!uie$isCursedEarth) return original;
        if (original instanceof EntityLiving living) {
            // Make persistent
            living.func_110163_bv();
            if (living instanceof IMob) {
                living.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 72000, 0));
                living.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 72000, 0));
            } else {
                living.addPotionEffect(new PotionEffect(Potion.regeneration.id, 72000, 0));
            }
        }
        return original;
    }
}
