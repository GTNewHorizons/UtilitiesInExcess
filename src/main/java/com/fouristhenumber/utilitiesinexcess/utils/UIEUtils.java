package com.fouristhenumber.utilitiesinexcess.utils;

import java.text.DecimalFormat;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.compat.nei.NEIUtils;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;

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

    public static void hideInNei(Block block) {
        hideInNei(Item.getItemFromBlock(block));
    }

    public static void hideInNei(Item item) {
        if (!Mods.NEI.isLoaded()) return;

        NEIUtils.hideItem(item);
    }
}
