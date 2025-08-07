package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.EthericSwordConfig;

public class ItemEthericSword extends ItemSword {

    public ItemEthericSword() {
        super(ToolMaterial.EMERALD);
        setTextureName("utilitiesinexcess:etheric_sword");
        setUnlocalizedName("etheric_sword");
        setMaxDamage(0);
        this.field_150934_a = EthericSwordConfig.normalDamage;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase p) {
        // Shouldn't ever fail?
        EntityPlayer player = (EntityPlayer) p;
        boolean succeed = super.hitEntity(stack, target, player);
        // Apparently this code doesn't exist standalone, so i just stole it:P
        boolean isCrit = player.fallDistance > 0.0F && !player.onGround
            && !player.isOnLadder()
            && !player.isInWater()
            && !player.isPotionActive(Potion.blindness)
            && player.ridingEntity == null
            && target instanceof EntityLivingBase;
        float magicDamage = EthericSwordConfig.magicDamage;
        if (isCrit) magicDamage *= 1.5f;
        float cd = target.lastDamage;
        // Set the last damage to 0 because we don't want our normal dmg to our interrupt magic dmg
        target.lastDamage = 0;
        target.attackEntityFrom(
            DamageSource.causePlayerDamage(player)
                .setDamageBypassesArmor()
                .setMagicDamage(),
            magicDamage);
        // Should magic damage impair next hit?
        target.lastDamage = cd + magicDamage;
        return succeed;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.etheric_sword.desc.1"));
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.etheric_sword.desc.2"));
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    // Unbreakable
    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_) {
        return false;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }
    //
}
