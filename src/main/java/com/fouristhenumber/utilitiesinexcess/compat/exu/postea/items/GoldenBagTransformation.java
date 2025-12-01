package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.gtnewhorizons.postea.api.IDExtenderCompat;

public class GoldenBagTransformation extends AbstractItemTransformation {

    public GoldenBagTransformation() {
        super();
        setDummyName("dummy_golden_bag");
        setOldName("ExtraUtilities:golden_bag");
    }

    public NBTTagCompound doTransformation(NBTTagCompound tag) {
        IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModItems.GOLDEN_BAG.get()));

        NBTTagCompound tagtag = tag.getCompoundTag("tag");
        NBTTagList tagList = new NBTTagList();

        List<String> toBeRemoved = new ArrayList<>();
        for (String name : tagtag.func_150296_c()) {
            if (!name.startsWith("items_")) continue;

            NBTTagCompound item = tagtag.getCompoundTag(name);
            item.setByte("Slot", Byte.parseByte(name.split("_")[1]));
            tagList.appendTag(item);

            toBeRemoved.add(name);
        }

        for (String name : toBeRemoved) {
            tagtag.removeTag(name);
        }

        tagtag.setTag("Items", tagList);
        tag.setTag("tag", tagtag);
        return tag;
    }
}
