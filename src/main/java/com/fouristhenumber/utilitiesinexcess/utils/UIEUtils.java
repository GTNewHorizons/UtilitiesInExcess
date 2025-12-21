package com.fouristhenumber.utilitiesinexcess.utils;

import it.unimi.dsi.fastutil.Hash;
import net.minecraft.item.ItemStack;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Random;

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
