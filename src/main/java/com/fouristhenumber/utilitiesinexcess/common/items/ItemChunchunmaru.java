package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import com.fouristhenumber.utilitiesinexcess.config.items.ChunchunmaruConfig;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorItemSword;
import com.gtnewhorizon.gtnhlib.api.ITranslucentItem;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ItemChunchunmaru extends ItemSword implements ITranslucentItem {

    public ItemChunchunmaru() {
        super(ToolMaterial.EMERALD);
        setUnlocalizedName("chunchunmaru");
        if (ChunchunmaruConfig.INSTANCE.unbreakable) {
            setMaxDamage(0);
        } else {
            setMaxDamage(ChunchunmaruConfig.INSTANCE.durability);
        }
        ((AccessorItemSword) this).setDamageVsEntity(ChunchunmaruConfig.INSTANCE.normalDamage);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.chunchunmaru.desc"));
        if (ChunchunmaruConfig.INSTANCE.unbreakable) {
            tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocal("item.unbreakable.desc"));
        }
    }

    // Unbreakable
    @Override
    public boolean isDamageable() {
        if (ChunchunmaruConfig.INSTANCE.unbreakable) return false;
        return super.isDamageable();
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
        if (ChunchunmaruConfig.INSTANCE.unbreakable) return false;
        return super.getIsRepairable(stack, repairMaterial);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (ChunchunmaruConfig.INSTANCE.unbreakable) return false;
        return super.showDurabilityBar(stack);
    }

    @SuppressWarnings("unused")
    @EventBusSubscriber
    public static class Events {

        @EventBusSubscriber.Condition
        public static boolean shouldSubscribe() {
            return ChunchunmaruConfig.INSTANCE.enable && ChunchunmaruConfig.INSTANCE.damageCreativePlayers;
        }

        @SubscribeEvent
        public static void onAttackEntity(AttackEntityEvent event) {
            if (event.entity instanceof EntityPlayer player && player.capabilities.isCreativeMode
                && player.getHeldItem()
                    .getItem() instanceof ItemChunchunmaru) {
                boolean isCrit = player.fallDistance > 0.0F && !player.onGround
                    && !player.isOnLadder()
                    && !player.isInWater()
                    && !player.isPotionActive(Potion.blindness)
                    && player.ridingEntity == null;
                event.target.attackEntityFrom(
                    DamageSource.causePlayerDamage(player)
                        .setDamageAllowedInCreativeMode(),
                    ChunchunmaruConfig.INSTANCE.creativeDamage * (isCrit ? 1.5f : 1f));
                event.setCanceled(true);
            }
        }
    }
}
