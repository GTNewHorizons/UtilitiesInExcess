package com.fouristhenumber.utilitiesinexcess.compat.nei;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import codechicken.nei.api.API;

public class NEIUtils {

    public static void hideItem(Item item) {
        API.hideItem(new ItemStack(item));
    }
}
