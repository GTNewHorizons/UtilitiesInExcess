package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.FMPItems;
import com.gtnewhorizons.postea.api.IDExtenderCompat;
import com.gtnewhorizons.postea.api.IItemStackTransformationHandler;

import codechicken.microblock.MicroMaterialRegistry;

public class ForgeMicroblockItemTransformer implements IItemStackTransformationHandler {

    @Override
    public boolean apply(String originalItemId, NBTTagCompound stack) {
        // UiE's microblock metas are offset 1. The item/meta is always modified when this transformer runs. Therefore,
        // we always return true.
        IDExtenderCompat.setItemStackID(stack, Item.getIdFromItem(FMPItems.UE_MULTI_PART.get()));
        stack.setInteger("Damage", stack.getInteger("Damage") - 1);

        if (!stack.hasKey("tag")) return true;
        NBTTagCompound tag = stack.getCompoundTag("tag");

        if (!tag.hasKey("mat")) return true;
        String matName = tag.getString("mat");

        // Goes through the remap layer, which is private
        int remappedId = MicroMaterialRegistry.materialID(matName);
        String remappedName = MicroMaterialRegistry.materialName(remappedId);

        if (remappedName.equals(matName)) return true;

        tag.setString("mat", remappedName);
        stack.setTag("tag", tag);
        return true;
    }
}
