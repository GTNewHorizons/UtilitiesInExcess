package com.fouristhenumber.utilitiesinexcess.common.recipe;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.config.items.ItemConfig;

import cpw.mods.fml.common.registry.GameRegistry;

public class DisableableItemStack {

    public static boolean addShapedRecipe(Object outputObject, Object... params) {
        if (!isEnabled(outputObject)) return false;
        ItemStack output = getItem(outputObject);
        for (Object o : params) if (!isEnabled(o)) return false;
        GameRegistry.addShapedRecipe(output, parseParams(params));
        return true;
    }

    public static boolean addShapelessRecipe(Object outputObject, Object... params) {
        if (!isEnabled(outputObject)) return false;
        ItemStack output = getItem(outputObject);
        for (Object o : params) if (!isEnabled(o)) return false;
        GameRegistry.addShapelessRecipe(output, parseParams(params));
        return true;
    }

    public static boolean isEnabled(Object disableable) {
        if (disableable instanceof ModItems mi) return mi.isEnabled() || ItemConfig.registerDisabledItems;
        if (disableable instanceof ModBlocks mb) return mb.isEnabled();
        if (disableable instanceof DisableableItemStack dstack) return isEnabled(dstack.disableable);
        return true;
    }

    public static ItemStack getItem(Object disableable) {
        if (disableable instanceof ModItems mi) return new ItemStack(mi.get());
        if (disableable instanceof ModBlocks mb) return new ItemStack(mb.getItem());
        if (disableable instanceof Item i) return new ItemStack(i);
        if (disableable instanceof ItemStack stack) return stack;
        if (disableable instanceof DisableableItemStack dstack) return dstack.theStack;
        return null;
    }

    public static Object[] parseParams(Object... params) {
        ArrayList<Object> parsedParams = new ArrayList<>();
        for (Object o : params) {
            ItemStack parsedItem = getItem(o);
            if (parsedItem != null) parsedParams.add(parsedItem);
            else parsedParams.add(o);
        }
        return parsedParams.toArray();
    }

    public final ItemStack theStack;
    public final Object disableable;

    public DisableableItemStack(ItemStack theStack, Object disableable) {
        this.theStack = theStack;
        this.disableable = disableable;
    }

    public DisableableItemStack(ModItems mi) {
        this.theStack = new ItemStack(mi.get());
        this.disableable = mi;
    }

    public DisableableItemStack(ModItems mi, int amount) {
        this.theStack = new ItemStack(mi.get(), amount);
        this.disableable = mi;

    }

    public DisableableItemStack(ModItems mi, int amount, int meta) {
        this.theStack = new ItemStack(mi.get(), amount, meta);
        this.disableable = mi;

    }

    public DisableableItemStack(ModBlocks mi) {
        this.theStack = new ItemStack(mi.get());
        this.disableable = mi;

    }

    public DisableableItemStack(ModBlocks mi, int amount) {
        this.theStack = new ItemStack(mi.get(), amount);
        this.disableable = mi;

    }

    public DisableableItemStack(ModBlocks mi, int amount, int meta) {
        this.theStack = new ItemStack(mi.get(), amount, meta);
        this.disableable = mi;

    }
}
