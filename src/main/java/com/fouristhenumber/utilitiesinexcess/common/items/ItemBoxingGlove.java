package com.fouristhenumber.utilitiesinexcess.common.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

public class ItemBoxingGlove extends Item {

    public ItemBoxingGlove() {
        super();
        this.setTextureName("utilitiesinexcess:boxing_glove");
        this.setUnlocalizedName("boxing_glove");
        this.setMaxStackSize(1);
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        Vec3 motion = Vec3
            .createVectorHelper(player.posX - entity.posX, player.posY - entity.posY, player.posZ - entity.posZ)
            .normalize();
        entity.motionX -= motion.xCoord * 0.75;
        entity.motionY -= motion.yCoord * 0.75;
        entity.motionZ -= motion.zCoord * 0.75;

        return true;
    }
}
