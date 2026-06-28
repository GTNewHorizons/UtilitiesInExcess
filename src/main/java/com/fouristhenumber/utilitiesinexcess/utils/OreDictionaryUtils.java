package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryUtils
{
    public static boolean IsDictUnder(ItemStack stack, String dictionaryTerm) {
        if (stack == null) return false;

        for (int id : OreDictionary.getOreIDs(stack)) {
            if (OreDictionary.getOreName(id).startsWith(dictionaryTerm)) {
                return true;
            }
        }

        return false;
    }
}
