package com.fouristhenumber.utilitiesinexcess.utils;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.compat.Mods;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
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
}
