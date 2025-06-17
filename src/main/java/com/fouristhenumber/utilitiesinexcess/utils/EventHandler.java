package com.fouristhenumber.utilitiesinexcess.utils;

import static com.fouristhenumber.utilitiesinexcess.common.blocks.BlockRainMuffler.NBT_RAIN_MUFFLED;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemGluttonsAxe;
import com.fouristhenumber.utilitiesinexcess.network.PacketRainMuffledSync;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class EventHandler {

    @SubscribeEvent
    public void onAttackEntity(net.minecraftforge.event.entity.player.AttackEntityEvent event) {
        ItemStack held = event.entityPlayer.getHeldItem();

        if (held != null && held.getItem() instanceof ItemGluttonsAxe) {
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

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP playerMP) {
            boolean rainMuffled = event.player.getEntityData()
                .getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG)
                .getBoolean(NBT_RAIN_MUFFLED);
            UtilitiesInExcess.networkWrapper.sendTo(new PacketRainMuffledSync(rainMuffled), playerMP);
        }
    }

}
