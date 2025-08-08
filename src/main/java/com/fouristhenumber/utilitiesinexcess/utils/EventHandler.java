package com.fouristhenumber.utilitiesinexcess.utils;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.event.world.BlockEvent;

import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemAntiParticulateShovel;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemDestructionPickaxe;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemGluttonsAxe;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemPrecisionShears;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.AntiParticulateShovelConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.DestructionPickaxeConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.GluttonsAxeConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public void onBlockBroken(BlockEvent.HarvestDropsEvent event) {
        if (event.harvester == null) return;
        EntityPlayer player = event.harvester;
        ItemStack heldItem = event.harvester.getHeldItem();
        if (heldItem == null) return;
        Item heldItemType = heldItem.getItem();
        if ((heldItemType instanceof ItemDestructionPickaxe && DestructionPickaxeConfig.voidMinedBlock)
            || (heldItemType instanceof ItemAntiParticulateShovel && AntiParticulateShovelConfig.voidMinedBlocks)
            || (heldItemType instanceof ItemGluttonsAxe && GluttonsAxeConfig.voidMinedBlock)) {
            event.drops.clear();
        }
        if (heldItemType instanceof ItemPrecisionShears) {
            AxisAlignedBB dropSearchArea = AxisAlignedBB
                .getBoundingBox(event.x - 1, event.y - 1, event.z - 1, event.x + 1, event.y + 1, event.z + 1);
            List<EntityItem> foundItems = event.world.getEntitiesWithinAABBExcludingEntity(player, dropSearchArea)
                .stream()
                .filter(EntityItem.class::isInstance)
                .map(EntityItem.class::cast)
                .filter(entityItem -> entityItem.age == 0)
                .collect(Collectors.toList());
            for (EntityItem item : foundItems) {
                if (!event.world.isRemote) item.setPosition(player.posX, player.posY, player.posZ);
            }

            for (ItemStack drop : event.drops) {
                if (!player.inventory.addItemStackToInventory(drop)) {
                    // Not player.entityDropItem(drop, 0.0f); cause i don't want the pickup delay:P
                    EntityItem entityitem = new EntityItem(
                        player.worldObj,
                        player.posX,
                        player.posY,
                        player.posZ,
                        drop);
                    player.worldObj.spawnEntityInWorld(entityitem);
                }
            }
            player.inventoryContainer.detectAndSendChanges();
            event.drops.clear();
        }
    }
}
