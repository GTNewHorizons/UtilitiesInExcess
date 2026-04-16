package com.fouristhenumber.utilitiesinexcess.common.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemBedrockiumIngot extends Item {

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int p_77663_4_, boolean p_77663_5_) {
        if (entityIn instanceof EntityLivingBase entity) {
            entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 0, 3, true));
        }
    }
}
