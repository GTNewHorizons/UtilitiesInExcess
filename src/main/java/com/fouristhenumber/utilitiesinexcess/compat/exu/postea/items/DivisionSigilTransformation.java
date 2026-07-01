package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemInversionSigilActive;
import com.gtnewhorizons.postea.api.IDExtenderCompat;
import com.gtnewhorizons.postea.api.IItemStackTransformationHandler;

public class DivisionSigilTransformation implements IItemStackTransformationHandler {

    @Override
    public boolean apply(String originalId, NBTTagCompound stack) {
        NBTTagCompound tag = stack.getCompoundTag("tag");
        int stable = tag.getByte("stable");
        if (stable == 1) {
            IDExtenderCompat.setItemStackID(stack, Item.getIdFromItem(ModItems.PSEUDO_DIVISION_SIGIL.get()));
            stack.removeTag("tag");
        } else {
            int dmg = tag.getInteger("damage");
            tag.removeTag("damage");
            if (dmg > 1) {
                tag.setInteger(ItemInversionSigilActive.DURABILITY_NBT_KEY, dmg);
                IDExtenderCompat.setItemStackID(stack, Item.getIdFromItem(ModItems.INVERSION_SIGIL_ACTIVE.get()));
            } else {
                IDExtenderCompat.setItemStackID(stack, Item.getIdFromItem(ModItems.INVERSION_SIGIL_INACTIVE.get()));
            }
        }
        stack.setTag("tag", tag);
        return true;
    }
}
