package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.nbt.NBTTagCompound;

public class NbtHelper {

    public static void writeEnumToNBT(NBTTagCompound tag, String key, Enum<?> value) {
        tag.setInteger(key, value.ordinal());
    }

    public static <T extends Enum<T>> T readEnumFromNBT(NBTTagCompound tag, String key, T[] values, T defaultValue) {
        if (!tag.hasKey(key)) return defaultValue;
        int ordinal = tag.getInteger(key);
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : defaultValue;
    }

}
