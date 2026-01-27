package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.gtnewhorizons.postea.api.IDExtenderCompat;

public class WateringCanTransformation extends AbstractItemTransformation {

    public WateringCanTransformation() {
        super();
        setDummyName("dummy_watering_can");
        setOldName("ExtraUtilities:watering_can");
    }

    public NBTTagCompound doTransformation(NBTTagCompound tag) {
        int dmg = tag.getInteger("Damage");
        switch (dmg) {
            case 0, 1:
                IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModItems.WATERING_CAN_BASIC.get()));
                break;
            case 2:
                IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModItems.WATERING_CAN_ADVANCED.get()));
                break;
            case 3:
                IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModItems.WATERING_CAN_ELITE.get()));
                break;
        }
        return tag;
    }
}
