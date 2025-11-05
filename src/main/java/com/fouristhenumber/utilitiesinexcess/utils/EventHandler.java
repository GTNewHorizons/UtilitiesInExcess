package com.fouristhenumber.utilitiesinexcess.utils;

import static com.fouristhenumber.utilitiesinexcess.common.blocks.BlockSpike.SpikeType.DIAMOND;
import static com.fouristhenumber.utilitiesinexcess.common.blocks.BlockSpike.SpikeType.GOLD;
import static com.fouristhenumber.utilitiesinexcess.common.blocks.BlockSpike.SpikeType.WOOD;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BlockEvent;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockSpike;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemAntiParticulateShovel;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemDestructionPickaxe;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemGluttonsAxe;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemPrecisionShears;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.AntiParticulateShovelConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.DestructionPickaxeConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.GluttonsAxeConfig;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorEntityLivingBase;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.source.getEntity() instanceof EntityPlayer player)) return;
        if (!(event.entityLiving instanceof EntityLiving entity)) return;

        ItemStack weapon = player.getHeldItem();
        if (weapon == null || !(weapon.getItem() instanceof BlockSpike.ItemSpike spike)) return;

        BlockSpike.SpikeType type = ((BlockSpike) spike.field_150939_a).getSpikeType();

        if (type != DIAMOND) {
            entity.recentlyHit = 0;
        }
        if (type == GOLD) {
            int xp = ((AccessorEntityLivingBase) entity).accessGetExperiencePoints(player);

            while (xp > 0) {
                int j = EntityXPOrb.getXPSplit(xp);
                xp -= j;
                entity.worldObj
                    .spawnEntityInWorld(new EntityXPOrb(entity.worldObj, entity.posX, entity.posY, entity.posZ, j));
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (!(event.source.getEntity() instanceof EntityPlayer player)) return;

        ItemStack weapon = player.getHeldItem();
        if (weapon == null || !(weapon.getItem() instanceof BlockSpike.ItemSpike spike)) return;

        if (((BlockSpike) spike.field_150939_a).getSpikeType() != WOOD) return;

        EntityLivingBase target = event.entityLiving;
        float base = (float) player.getEntityAttribute(SharedMonsterAttributes.attackDamage)
            .getAttributeValue();
        float ench = EnchantmentHelper.getEnchantmentModifierLiving(player, target);
        float total = base + ench;

        // If attack would kill, cancel
        if (target.getHealth() - total <= 0.01F) {
            event.setCanceled(true);
        }
    }

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
