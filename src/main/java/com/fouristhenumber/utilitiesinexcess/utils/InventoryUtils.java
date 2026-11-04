package com.fouristhenumber.utilitiesinexcess.utils;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class InventoryUtils {

    public static final Random rand = new Random();

    public static IInventory getInventory(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);

        if (block instanceof BlockChest) {
            return ((BlockChest) block).func_149951_m(world, x, y, z);
        }

        TileEntity te = world.getTileEntity(x, y, z);
        return te instanceof IInventory ? (IInventory) te : null;
    }

    public static void dropStack(World world, int x, int y, int z, ItemStack stack) {
        while (stack.stackSize > 0) {
            int amount = Math.min(stack.stackSize, rand.nextInt(21) + 10);

            ItemStack dropped = stack.copy();
            dropped.stackSize = amount;

            float offsetX = rand.nextFloat() * 0.8F + 0.1F;
            float offsetY = rand.nextFloat() * 0.8F + 0.1F;
            float offsetZ = rand.nextFloat() * 0.8F + 0.1F;

            EntityItem entity = new EntityItem(world, x + offsetX, y + offsetY, z + offsetZ, dropped);

            float velocity = 0.05F;
            entity.motionX = rand.nextGaussian() * velocity;
            entity.motionY = rand.nextGaussian() * velocity + 0.2F;
            entity.motionZ = rand.nextGaussian() * velocity;

            world.spawnEntityInWorld(entity);

            stack.stackSize -= amount;
        }
    }
}
