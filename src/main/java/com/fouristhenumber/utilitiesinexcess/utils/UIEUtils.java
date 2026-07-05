package com.fouristhenumber.utilitiesinexcess.utils;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import codechicken.nei.api.API;
import it.unimi.dsi.fastutil.Hash;

public class UIEUtils {

    public static final Random uieRandom = new Random();

    private static final DecimalFormat COMMA_FORMAT = new DecimalFormat("#,###.#");

    public static final String UIE_NBT_TAG = "Utilities_In_Excess";

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

    public static ItemStack getBauble(EntityPlayer player, Class<?> clazz) {
        if (!Mods.Baubles.isLoaded()) return null;

        InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
        for (int i = 0; i < baubles.getSizeInventory(); i++) {
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack != null && stack.getItem() != null && clazz.isInstance(stack.getItem())) {
                return stack;
            }
        }

        return null;
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

    public static float lerp(float cur, float target, float speed) {
        return cur + (target - cur) * speed;
    }

    public static NBTTagCompound getUIETag(EntityPlayer player) {
        NBTTagCompound playerNBT = player.getEntityData();
        NBTTagCompound uieTag;

        if (playerNBT.hasKey(UIE_NBT_TAG)) {
            uieTag = playerNBT.getCompoundTag(UIE_NBT_TAG);
        } else {
            uieTag = new NBTTagCompound();
            playerNBT.setTag(UIE_NBT_TAG, uieTag);
        }
        return uieTag;
    }

    public static void dropItemsFromIInventory(int newSize, IInventory chest, World world, int x, int y, int z) {
        for (int l = newSize; l < chest.getSizeInventory(); l++) {
            ItemStack itemstack = chest.getStackInSlot(l);
            if (itemstack == null) {
                continue;
            }
            float f = uieRandom.nextFloat() * 0.8F + 0.1F;
            float f1 = uieRandom.nextFloat() * 0.8F + 0.1F;
            float f2 = uieRandom.nextFloat() * 0.8F + 0.1F;
            while (itemstack.stackSize > 0) {
                int i1 = uieRandom.nextInt(21) + 10;
                if (i1 > itemstack.stackSize) {
                    i1 = itemstack.stackSize;
                }
                itemstack.stackSize -= i1;
                EntityItem entityitem = new EntityItem(
                    world,
                    (float) x + f,
                    (float) y + (newSize > 0 ? 1 : 0) + f1,
                    (float) z + f2,
                    new ItemStack(itemstack.getItem(), i1, itemstack.getItemDamage()));
                float f3 = 0.05F;
                entityitem.motionX = (float) uieRandom.nextGaussian() * f3;
                entityitem.motionY = (float) uieRandom.nextGaussian() * f3 + 0.2F;
                entityitem.motionZ = (float) uieRandom.nextGaussian() * f3;
                if (itemstack.hasTagCompound()) {
                    entityitem.getEntityItem()
                        .setTagCompound(
                            (NBTTagCompound) itemstack.getTagCompound()
                                .copy());
                }
                world.spawnEntityInWorld(entityitem);
            }
        }
    }

    public static class ItemStackHashStrategy implements Hash.Strategy<ItemStack> {

        public static final ItemStackHashStrategy instance = new ItemStackHashStrategy();

        @Override
        public int hashCode(ItemStack itemStack) {
            if (itemStack == null || itemStack.getItem() == null) return 0;
            return Objects.hash(itemStack.getItem(), itemStack.getItemDamage(), itemStack.getTagCompound());
        }

        @Override
        public boolean equals(ItemStack a, ItemStack b) {
            if (a == null && b == null) return true;
            if (a == null || b == null) return false;
            return a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage()
                && Objects.equals(a.getTagCompound(), b.getTagCompound());
        }
    }

    public static void hideInNei(Block block) {
        hideInNei(Item.getItemFromBlock(block));
    }

    public static void hideInNei(Item item) {
        if (!Mods.NEI.isLoaded()) return;

        API.hideItem(new ItemStack(item));
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
