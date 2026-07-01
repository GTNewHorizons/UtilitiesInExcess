package com.fouristhenumber.utilitiesinexcess.utils;

import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.config.items.invertedtools.EthericSwordConfig;

public class SiegeMobCreator {

    public static EntityLiving getSiegeMob(World world) {
        EntityLiving mob;
        int rng = world.rand.nextInt(100);
        if (rng < 25) {
            mob = new EntityZombie(world);
            addArmor(mob, world.rand);
            addMelee(mob, world.rand);
        } else if (rng < 50) {
            mob = new EntitySkeleton(world);
            addArmor(mob, world.rand);
            addBow(mob, world.rand);
        } else if (rng < 70) {
            mob = new EntitySpider(world);
        } else if (rng < 85) {
            mob = new EntityCreeper(world);
        } else if (rng < 93) {
            mob = new EntityBlaze(world);
        } else if (rng < 98) {
            mob = new EntityGhast(world);
        } else {
            mob = new EntityGiantZombie(world);
        }
        applyPotions(mob, world.rand);
        return mob;
    }

    private static void addArmor(EntityLiving mob, Random rand) {
        for (int armorSlot = 1; armorSlot <= 4; armorSlot++) {
            ItemStack armorStack = pickArmor(rand, armorSlot);
            mob.setCurrentItemOrArmor(armorSlot, armorStack);
        }
    }

    private static void applyPotions(EntityLiving mob, Random rand) {
        int rng = rand.nextInt(100);
        if (rng < 30) {
            mob.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, Integer.MAX_VALUE, 2, false));
        } else if (rng < 50) {
            mob.addPotionEffect(new PotionEffect(Potion.damageBoost.id, Integer.MAX_VALUE, 2, false));
        } else if (rng < 60) {
            mob.addPotionEffect(new PotionEffect(Potion.resistance.id, Integer.MAX_VALUE, 2, false));
        }
    }

    private static ItemStack pickArmor(Random rand, int armorSlot) {
        ItemStack armor;
        int rng = rand.nextInt(100);
        int armorTier;
        if (rng < 20) return null;
        else if (rng < 30) armorTier = 0;
        else if (rng < 40) armorTier = 1;
        else if (rng < 65) armorTier = 2;
        else if (rng < 90) armorTier = 3;
        else armorTier = 4;

        armor = new ItemStack(EntityLiving.getArmorItemForSlot(armorSlot, armorTier));

        applyArmorEnchants(armor, rand);
        return armor;
    }

    private static void applyArmorEnchants(ItemStack item, Random rand) {
        int level = rand.nextInt(4) + 1;
        if (rand.nextInt(100) < 20) item.addEnchantment(Enchantment.protection, level);
        else if (rand.nextInt(100) < 40) item.addEnchantment(Enchantment.projectileProtection, level);
        else if (rand.nextInt(100) < 60) item.addEnchantment(Enchantment.fireProtection, level);
        else if (rand.nextInt(100) < 80) item.addEnchantment(Enchantment.blastProtection, level);
    }

    private static void addBow(EntityLiving mob, Random rand) {
        ItemStack bow = new ItemStack(Items.bow);

        if (rand.nextInt(100) < 30) bow.addEnchantment(Enchantment.power, rand.nextInt(5) + 1);
        if (rand.nextInt(100) < 10) bow.addEnchantment(Enchantment.flame, 1);

        mob.setCurrentItemOrArmor(0, bow);
    }

    private static void addMelee(EntityLiving mob, Random rand) {
        ItemStack weapon;
        int rng = rand.nextInt(100);
        if (rng < 20) weapon = new ItemStack(Items.iron_axe);
        else if (rng < 70) weapon = new ItemStack(Items.iron_sword);
        else if (rng < 85) weapon = new ItemStack(Items.diamond_axe);
        else if (rng < 95) weapon = new ItemStack(Items.diamond_sword);
        else weapon = EthericSwordConfig.INSTANCE.enable ? ModItems.ETHERIC_SWORD.newItemStack()
            : new ItemStack(Items.diamond_sword);

        if (rand.nextInt(100) < 40) weapon.addEnchantment(Enchantment.sharpness, rand.nextInt(5) + 1);
        if (rand.nextInt(100) < 10) weapon.addEnchantment(Enchantment.fireAspect, 2);

        mob.setCurrentItemOrArmor(0, weapon);
    }
}
