package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.gtnewhorizons.postea.api.IDExtenderCompat;
import com.gtnewhorizons.postea.api.IItemStackTransformationHandler;

public class GoldenLassoTransformation implements IItemStackTransformationHandler {

    @Override
    public boolean apply(String originalId, NBTTagCompound stack) {
        IDExtenderCompat.setItemStackID(stack, Item.getIdFromItem(ModItems.MOB_JAR.get()));

        if (stack.hasKey("tag")) {
            NBTTagCompound tagtag = stack.getCompoundTag("tag");
            NBTTagCompound newTagtag = new NBTTagCompound();
            newTagtag.setTag("MobData", tagtag);

            stack.setTag("tag", newTagtag);
        }
        return true;
    }
}
