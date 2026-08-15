package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class InventoryUtils
{
    public static IInventory getInventory(World world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);

        if (block instanceof BlockChest)
        {
            return ((BlockChest) block).func_149951_m(world, x, y, z);
        }

        TileEntity te = world.getTileEntity(x, y, z);
        return te instanceof IInventory ? (IInventory) te : null;
    }
}
