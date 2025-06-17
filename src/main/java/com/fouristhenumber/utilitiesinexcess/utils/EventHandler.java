package com.fouristhenumber.utilitiesinexcess.utils;

import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemEthericSword;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.world.BlockEvent;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemGluttonsAxe;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public void onAttackEntity(net.minecraftforge.event.entity.player.AttackEntityEvent event) {
        ItemStack held = event.entityPlayer.getHeldItem();

        if (held == null) return;

        if (held.getItem() instanceof ItemGluttonsAxe) {
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
        else if (held.getItem() instanceof ItemEthericSword) {
            if (event.target instanceof EntityLivingBase entityLivingBase) {
                // TODO: Find a way to confirm that this damage is actually armor-piercing
                // TODO: Add critical hits
                entityLivingBase.attackEntityFrom(DamageSource.causePlayerDamage(event.entityPlayer).setDamageBypassesArmor().setMagicDamage(), 6.0f);
                entityLivingBase.attackEntityFrom(DamageSource.causePlayerDamage(event.entityPlayer), 8.0f);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onBlockBroken(BlockEvent.HarvestDropsEvent event) {
        if (event.harvester == null) return;
        ItemStack heldItem = event.harvester.getHeldItem();
        if (heldItem == null) return;

        if (heldItem.getItem() == ModItems.DESTRUCTION_PICKAXE.get()
            || heldItem.getItem() == ModItems.ANTI_PARTICULATE_SHOVEL.get()) {
            event.drops.clear();
        }
    }
}
