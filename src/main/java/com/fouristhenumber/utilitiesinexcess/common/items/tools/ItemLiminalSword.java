package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.fouristhenumber.utilitiesinexcess.config.items.invertedtools.LiminalSwordConfig;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorEntityLivingBase;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorItemSword;
import com.google.common.collect.Multimap;
import com.gtnewhorizon.gtnhlib.api.ITranslucentItem;

public class ItemLiminalSword extends ItemSword implements ITranslucentItem {

    private static final UUID MAGIC_DMG_UUID = UUID.randomUUID();

    public ItemLiminalSword() {
        super(ToolMaterial.EMERALD);
        setTextureName("utilitiesinexcess:liminal_sword");
        setUnlocalizedName("liminal_sword");
        if (LiminalSwordConfig.INSTANCE.unbreakable) setMaxDamage(0);
        ((AccessorItemSword) this).setDamageVsEntity(LiminalSwordConfig.INSTANCE.normalDamage);
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
        float magicDamage = LiminalSwordConfig.INSTANCE.magicDamage;
        if (isCrit) magicDamage *= 1.5f;
        AccessorEntityLivingBase acTarget = (AccessorEntityLivingBase) target;
        float cd = acTarget.getLastDamage();
        // Set the last damage to 0 because we don't want our normal dmg to our interrupt magic dmg
        acTarget.setLastDamage(0);
        target.attackEntityFrom(
            DamageSource.causePlayerDamage(player)
                .setDamageBypassesArmor()
                .setMagicDamage(),
            magicDamage);
        // Should magic damage impair next hit? Probably yeah
        acTarget.setLastDamage(cd + magicDamage);
        return succeed;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        if (LiminalSwordConfig.INSTANCE.unbreakable) {
            tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("uie.desc.item.unbreakable"));
        }
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(stack);
        multimap.put(
            "utilitiesinexcess.magic_damage",
            new AttributeModifier(
                MAGIC_DMG_UUID,
                "utilitiesinexcess.magic_damage",
                LiminalSwordConfig.INSTANCE.magicDamage,
                0));
        return multimap;
    }

    // Unbreakable
    @Override
    public boolean isDamageable() {
        if (LiminalSwordConfig.INSTANCE.unbreakable) return false;
        return super.isDamageable();
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
        if (LiminalSwordConfig.INSTANCE.unbreakable) return false;
        return super.getIsRepairable(stack, repairMaterial);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (LiminalSwordConfig.INSTANCE.unbreakable) return false;
        return super.showDurabilityBar(stack);
    }
    //
}
