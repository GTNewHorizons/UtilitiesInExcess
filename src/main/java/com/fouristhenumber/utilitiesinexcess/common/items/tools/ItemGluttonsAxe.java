package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.GluttonsAxeConfig;

public class ItemGluttonsAxe extends ItemAxe {

    public ItemGluttonsAxe() {
        super(ToolMaterial.EMERALD);
        setTextureName("utilitiesinexcess:gluttons_axe");
        setUnlocalizedName("gluttons_axe");
        setMaxDamage(0);
        this.damageVsEntity = GluttonsAxeConfig.damageAgainstUndead;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.gluttons_axe.desc.1"));
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.gluttons_axe.desc.2"));
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.gluttons_axe.desc.3"));
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    public static void spawnParticles(Entity e) {
        if (!GluttonsAxeConfig.spawnParticles) return;
        int ci = Potion.potionTypes[Potion.heal.getId()].getLiquidColor();
        double d0 = (double) (ci >> 16 & 255) / 255.0D;
        double d1 = (double) (ci >> 8 & 255) / 255.0D;
        double d2 = (double) (ci >> 0 & 255) / 255.0D;
        for (int i = 0; i < 5; i++) {

            e.worldObj.spawnParticle(
                "mobSpell",
                e.posX + (e.rand.nextDouble() - 0.5D) * (double) e.width,
                e.posY + e.rand.nextDouble() * (double) e.height - (double) e.yOffset,
                e.posZ + (e.rand.nextDouble() - 0.5D) * (double) e.width,
                d0,
                d1,
                d2);
        }
    }

    // Restore hunger every 2 seconds
    @Override
    public void onUpdate(ItemStack s, World w, Entity e, int slot, boolean selected) {
        super.onUpdate(s, w, e, slot, selected);
        if (e instanceof EntityPlayer p && selected) {
            if (w.getTotalWorldTime() % (2 * 20) == 0) {
                FoodStats fs = p.getFoodStats();
                fs.addStats(GluttonsAxeConfig.foodGain, GluttonsAxeConfig.saturationGain);
            }
        }
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z,
        EntityLivingBase entity) {
        return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer attacker, Entity e) {
        if (!(e instanceof EntityLivingBase target)) return false;
        if (target instanceof EntityZombie z && z.isVillager()) {
            attacker.addExhaustion(3 * 4);
            spawnParticles(target);
            if (!attacker.worldObj.isRemote) z.convertToVillager();
            return true;
        }
        if (!target.isEntityUndead()) {

            float amountToHeal = Math.min(GluttonsAxeConfig.maxHeal, target.getMaxHealth() - target.getHealth());
            if (amountToHeal == 0) if (GluttonsAxeConfig.useHungerAlways) attacker.addExhaustion(3 * 4);
            else {
                if (GluttonsAxeConfig.drainHp) if (attacker.getHealth() >= amountToHeal + 1)
                    attacker.setHealth(attacker.getHealth() - amountToHeal);
                else return true;
                target.setHealth(target.getHealth() + (amountToHeal + 1));
                attacker.addExhaustion(amountToHeal * 4);
            }
            spawnParticles(target);
            return true;
        }
        attacker.addExhaustion(3 * 4);
        spawnParticles(target);
        return false;
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
