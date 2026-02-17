package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.gtnewhorizons.postea.api.IDExtenderCompat;

public class GoldenLassoTransformation extends AbstractItemTransformation {

    public GoldenLassoTransformation() {
        super();
        setDummyName("dummy_mob_jar");
        setOldName("ExtraUtilities:golden_lasso");
    }

    public NBTTagCompound doTransformation(NBTTagCompound tag) {
        IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModItems.MOB_JAR.get()));

        if (tag.hasKey("tag")) {
            NBTTagCompound tagtag = tag.getCompoundTag("tag");
            NBTTagCompound newTagtag = new NBTTagCompound();
            newTagtag.setTag("MobData", tagtag);

            tag.setTag("tag", newTagtag);
        }

        return tag;
    }
}
