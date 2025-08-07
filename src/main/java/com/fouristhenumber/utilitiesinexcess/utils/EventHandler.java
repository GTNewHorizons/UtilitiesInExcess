package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;

import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemAntiParticulateShovel;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemDestructionPickaxe;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemGluttonsAxe;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.GluttonsAxeConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public void onBlockBroken(BlockEvent.HarvestDropsEvent event) {
        if (event.harvester == null) return;
        ItemStack heldItem = event.harvester.getHeldItem();
        if (heldItem == null) return;
        Item heldItemType = heldItem.getItem();
        if (heldItemType instanceof ItemDestructionPickaxe || heldItemType instanceof ItemAntiParticulateShovel
            || (heldItemType instanceof ItemGluttonsAxe && GluttonsAxeConfig.voidMinedBlock)) {
            event.drops.clear();
        }
    }
}
