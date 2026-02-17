package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.gtnewhorizons.postea.api.IDExtenderCompat;

public class UnstableIngotTransformation extends AbstractItemTransformation {

    public UnstableIngotTransformation() {
        super();
        setDummyName("dummy_inverted_ingot");
        setOldName("ExtraUtilities:unstableIngot");
    }

    public NBTTagCompound doTransformation(NBTTagCompound tag) {
        int dmg = tag.getInteger("Damage");
        switch (dmg) {
            case 0, 2:
                IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModItems.INVERTED_INGOT.get()));
                break;
            case 1:
                IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModItems.INVERTED_NUGGET.get()));
                break;
        }
        return tag;
    }
}
