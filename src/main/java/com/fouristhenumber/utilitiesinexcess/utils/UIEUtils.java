package com.fouristhenumber.utilitiesinexcess.utils;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.compat.Mods;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;

public class UIEUtils {

    public static final Random uieRandom = new Random();

    private static final DecimalFormat COMMA_FORMAT = new DecimalFormat("#,###.#");

    public static String formatNumber(int number) {
        return COMMA_FORMAT.format(number);
    }

    public static String formatNumber(long number) {
        return COMMA_FORMAT.format(number);
    }

    public static String formatNumber(double number) {
        return COMMA_FORMAT.format(number);
    }

    public static String formatNumber(float number) {
        return COMMA_FORMAT.format(number);
    }

    public static boolean hasBauble(EntityPlayer player, Class<?> clazz) {
        if (!Mods.Baubles.isLoaded()) return false;

        InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
        for (int i = 0; i < baubles.getSizeInventory(); i++) {
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack != null && stack.getItem() != null && clazz.isInstance(stack.getItem())) {
                return true;
            }
        }

        return false;
    }

    public static Set<BlockPos> scanForBlock(World world, int x, int y, int z, int radius, Block findBlock) {
        return scanForBlock(world, x, y, z, radius, findBlock, 0);
    }

    public static Set<BlockPos> scanForBlock(World world, int x, int y, int z, int radius, Block findBlock, int meta) {
        Set<BlockPos> positions = new HashSet<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int rem = radius - (Math.abs(dx) + Math.abs(dy));
                if (rem < 0) continue;

                for (int dz = -rem; dz <= rem; dz++) {
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) > radius) continue;

                    int nx = x + dx;
                    int ny = y + dy;
                    int nz = z + dz;

                    if (!world.blockExists(nx, ny, nz)) continue;

                    if (world.getBlock(nx, ny, nz) == findBlock && world.getBlockMetadata(nx, ny, nz) == meta)
                        positions.add(new BlockPos(nx, ny, nz));
                }
            }
        }
        return positions;
    }
}
