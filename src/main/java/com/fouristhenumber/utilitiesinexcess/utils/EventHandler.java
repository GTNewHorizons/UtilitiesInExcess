package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.world.BlockEvent;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemEthericSword;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public void onAttackEntity(net.minecraftforge.event.entity.player.AttackEntityEvent event) {
        ItemStack held = event.entityPlayer.getHeldItem();
        if (held == null) return;
        if (held.getItem() instanceof ItemEthericSword) {
            if (event.target instanceof EntityLivingBase entityLivingBase) {
                // TODO: Find a way to confirm that this damage is actually armor-piercing
                // TODO: Add critical hits
                entityLivingBase.attackEntityFrom(
                    DamageSource.causePlayerDamage(event.entityPlayer)
                        .setDamageBypassesArmor()
                        .setMagicDamage(),
                    6.0f);
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
