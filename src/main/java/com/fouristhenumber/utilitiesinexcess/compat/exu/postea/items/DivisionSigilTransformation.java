package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemInversionSigilActive;
import com.gtnewhorizons.postea.api.IDExtenderCompat;

public class DivisionSigilTransformation extends AbstractItemTransformation {

    public DivisionSigilTransformation() {
        super();
        setDummyName("dummy_inversion_sigil");
        setOldName("ExtraUtilities:divisionSigil");
    }

    public NBTTagCompound doTransformation(NBTTagCompound tag) {
        NBTTagCompound tagtag = tag.getCompoundTag("tag");
        int stable = tagtag.getByte("stable");
        if (stable == 1) {
            IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModItems.PSEUDO_INVERSION_SIGIL.get()));
            tag.removeTag("tag");
        } else {
            int dmg = tagtag.getInteger("damage");
            tagtag.removeTag("damage");
            if (dmg > 1) {
                tagtag.setInteger(ItemInversionSigilActive.DURABILITY_NBT_KEY, dmg);
                IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModItems.INVERSION_SIGIL_ACTIVE.get()));
            } else {
                IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModItems.INVERSION_SIGIL_INACTIVE.get()));
            }
        }

        return tag;
    }
}
