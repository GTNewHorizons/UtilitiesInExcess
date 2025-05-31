package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import com.fouristhenumber.utilitiesinexcess.common.blocks.spike.SpikeDamageSource;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemHungerAxe;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (!(event.source instanceof SpikeDamageSource source)) return;
        ItemStack fakeWeapon = source.getFakeWeapon();
        SpikeDamageSource.spikeTypes type = source.getType();

        // if (type != SpikeDamageSource.spikeTypes.GOLD)

        int looting = EnchantmentHelper.getEnchantmentLevel(Enchantment.looting.effectId, fakeWeapon);
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.source instanceof SpikeDamageSource source) {
            ItemStack fakeWeapon = source.getFakeWeapon();
            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, fakeWeapon) > 0) {
                event.entity.setFire(5);
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.source instanceof SpikeDamageSource source) {
            if (source.getType() == SpikeDamageSource.spikeTypes.WOOD && event.entityLiving.getHealth() < 2.0F) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onAttackEntity(net.minecraftforge.event.entity.player.AttackEntityEvent event) {
        ItemStack held = event.entityPlayer.getHeldItem();

        if (held != null && held.getItem() instanceof ItemHungerAxe) {
            // Convert zombie villager instantly
            if (event.target instanceof EntityZombie zombie && zombie.isVillager() && !zombie.worldObj.isRemote) {
                EntityVillager entityvillager = new EntityVillager(zombie.worldObj);
                entityvillager.copyLocationAndAnglesFrom(zombie);
                entityvillager.onSpawnWithEgg(null);
                entityvillager.setLookingForHome();

                if (zombie.isChild()) {
                    entityvillager.setGrowingAge(-24000);
                }

                zombie.worldObj.removeEntity(zombie);
                zombie.worldObj.spawnEntityInWorld(entityvillager);
                entityvillager.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 0));
                zombie.worldObj
                    .playAuxSFXAtEntity(null, 1017, (int) zombie.posX, (int) zombie.posY, (int) zombie.posZ, 0);
            } else if (event.target instanceof EntityLivingBase livingBase) {
                livingBase.addPotionEffect(new PotionEffect(Potion.heal.id, 1, 0));
            }
            FoodStats foodStats = event.entityPlayer.getFoodStats();

            if (foodStats.getSaturationLevel() > 0F) {
                event.entityPlayer.addExhaustion(8.0F);
            } else if (foodStats.getFoodLevel() > 0) {
                foodStats.setFoodLevel(Math.max(0, foodStats.getFoodLevel() - 2));
            }
            // Cancel the attack
            event.setCanceled(true);
        }
    }
}
