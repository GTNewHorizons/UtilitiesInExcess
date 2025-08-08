package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import java.util.List;
import java.util.Random;

import com.fouristhenumber.utilitiesinexcess.config.items.ItemConfig;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
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
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorEntityZombie;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorItemTool;

public class ItemGluttonsAxe extends ItemAxe {

    public ItemGluttonsAxe() {
        super(ToolMaterial.EMERALD);
        setTextureName("utilitiesinexcess:gluttons_axe");
        setUnlocalizedName("gluttons_axe");
        if (GluttonsAxeConfig.unbreakable) setMaxDamage(0);
        ((AccessorItemTool) this).setDamageVsEntity(GluttonsAxeConfig.damageAgainstUndead);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        if (!ItemConfig.shiftForDescription || GuiScreen.isShiftKeyDown()) {
            tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.gluttons_axe.desc.1"));
            tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.gluttons_axe.desc.2"));
            tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.gluttons_axe.desc.3"));
        } else tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("shift_for_description"));

        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    private static final Random particleRandom = new Random();;

    public static void spawnParticles(Entity e) {
        if (!GluttonsAxeConfig.spawnParticles) return;
        int ci = Potion.potionTypes[Potion.heal.getId()].getLiquidColor();
        double d0 = (double) (ci >> 16 & 255) / 255.0D;
        double d1 = (double) (ci >> 8 & 255) / 255.0D;
        double d2 = (double) (ci >> 0 & 255) / 255.0D;
        for (int i = 0; i < 5; i++) {

            e.worldObj.spawnParticle(
                "mobSpell",
                e.posX + (particleRandom.nextDouble() - 0.5D) * (double) e.width,
                e.posY + particleRandom.nextDouble() * (double) e.height - (double) e.yOffset,
                e.posZ + (particleRandom.nextDouble() - 0.5D) * (double) e.width,
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
            if (!attacker.worldObj.isRemote) ((AccessorEntityZombie) z).convertToVillager();
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
        if (GluttonsAxeConfig.unbreakable) return false;
        return super.isDamageable();
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
        if (GluttonsAxeConfig.unbreakable) return false;
        return super.getIsRepairable(stack, repairMaterial);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (GluttonsAxeConfig.unbreakable) return false;
        return super.showDurabilityBar(stack);
    }
    //
}
