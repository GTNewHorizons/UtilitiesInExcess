package com.fouristhenumber.utilitiesinexcess.common.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBlockRotatable extends ItemBlock {

    public ItemBlockRotatable(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, int metadata) {
        int direction = (int) ((((player.rotationYaw % 360) + 45f) / 90f + 4f) % 4f);

        return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, direction);
    }
}
