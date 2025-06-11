package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockCursedEarth;

@Mixin(MobSpawnerBaseLogic.class)
public class CursedEarthSpawner {

    @Shadow
    private int minSpawnDelay;
    @Shadow
    private int maxSpawnDelay;
    @Shadow
    private List<WeightedRandom.Item> potentialEntitySpawns;

    // If the block below is cursed earth,
    // always run the spawner instead of depending
    // on player location.
    @Inject(method = "isActivated", at = @At("HEAD"), cancellable = true)
    private void onIsActivated(CallbackInfoReturnable<Boolean> cir) {
        MobSpawnerBaseLogic self = (MobSpawnerBaseLogic) (Object) this;
        World world = self.getSpawnerWorld();

        if (world == null) return;

        Block blockBelow = world.getBlock(self.getSpawnerX(), self.getSpawnerY() - 1, self.getSpawnerZ());

        if (blockBelow instanceof BlockCursedEarth) {
            cir.setReturnValue(true);
        }
    }

    // Copied from base class except
    // dividing the spawn time by 4 if the block
    // is cursed earth.
    @Inject(method = "resetTimer", at = @At("HEAD"), cancellable = true)
    private void onResetTimer(CallbackInfo ci) {
        MobSpawnerBaseLogic self = (MobSpawnerBaseLogic) (Object) this;
        World world = self.getSpawnerWorld();

        if (world == null) return;

        Block blockBelow = world.getBlock(self.getSpawnerX(), self.getSpawnerY() - 1, self.getSpawnerZ());

        if (!(blockBelow instanceof BlockCursedEarth)) return;

        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            self.spawnDelay = this.minSpawnDelay / 4;
        } else {
            int range = this.maxSpawnDelay - this.minSpawnDelay;
            self.spawnDelay = (this.minSpawnDelay + world.rand.nextInt(range)) / 4;
        }

        if (this.potentialEntitySpawns != null && this.potentialEntitySpawns.size() > 0) {
            self.setRandomEntity(
                (MobSpawnerBaseLogic.WeightedRandomMinecart) WeightedRandom
                    .getRandomItem(world.rand, this.potentialEntitySpawns));
        }

        self.func_98267_a(1);
        ci.cancel();
    }

    // If the block is cursed earth, make the creatures persistent
    // and add potion effects.
    @Inject(method = "func_98265_a", at = @At("RETURN"))
    private void onEntitySpawned(Entity entity, CallbackInfoReturnable<Entity> cir) {
        MobSpawnerBaseLogic self = (MobSpawnerBaseLogic) (Object) this;
        World world = self.getSpawnerWorld();

        if (world == null || world.isRemote) return;

        Block blockBelow = world.getBlock(self.getSpawnerX(), self.getSpawnerY() - 1, self.getSpawnerZ());

        if (!(blockBelow instanceof BlockCursedEarth && entity instanceof EntityLiving living)) return;

        // Make persistent
        living.func_110163_bv();

        if (living instanceof IMob) {
            living.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 72000, 0));
            living.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 72000, 0));
        } else {
            living.addPotionEffect(new PotionEffect(Potion.regeneration.id, 72000, 0));
        }
    }
}
