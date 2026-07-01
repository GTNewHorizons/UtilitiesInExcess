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
import net.minecraft.item.Item;
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
        ItemStack helmet = pickArmor(
            rand,
            Items.leather_helmet,
            Items.golden_helmet,
            Items.chainmail_helmet,
            Items.iron_helmet,
            Items.diamond_helmet);
        mob.setCurrentItemOrArmor(4, helmet);
        ItemStack chestplate = pickArmor(
            rand,
            Items.leather_chestplate,
            Items.golden_chestplate,
            Items.chainmail_chestplate,
            Items.iron_chestplate,
            Items.diamond_chestplate);
        mob.setCurrentItemOrArmor(3, chestplate);
        ItemStack leggings = pickArmor(
            rand,
            Items.leather_leggings,
            Items.golden_leggings,
            Items.chainmail_leggings,
            Items.iron_leggings,
            Items.diamond_leggings);
        mob.setCurrentItemOrArmor(2, leggings);
        ItemStack boots = pickArmor(
            rand,
            Items.leather_boots,
            Items.golden_boots,
            Items.chainmail_boots,
            Items.iron_boots,
            Items.diamond_boots);
        mob.setCurrentItemOrArmor(1, boots);
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

    private static ItemStack pickArmor(Random rand, Item leather, Item gold, Item chain, Item iron, Item diamond) {
        ItemStack armor;
        int rng = rand.nextInt(100);
        if (rng < 20) return null;
        else if (rng < 30) armor = new ItemStack(leather);
        else if (rng < 40) armor = new ItemStack(gold);
        else if (rng < 65) armor = new ItemStack(chain);
        else if (rng < 90) armor = new ItemStack(iron);
        else armor = new ItemStack(diamond);

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
