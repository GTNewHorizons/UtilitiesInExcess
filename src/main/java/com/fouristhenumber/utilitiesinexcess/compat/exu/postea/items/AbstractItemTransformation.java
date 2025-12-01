package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items;

import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.IPosteaTransformation;
import com.gtnewhorizons.postea.api.ItemStackReplacementManager;

import cpw.mods.fml.common.registry.GameRegistry;

public abstract class AbstractItemTransformation implements IPosteaTransformation {

    private final Item dummyItem;

    private String dummyName;
    private String oldName;

    public AbstractItemTransformation() {
        dummyItem = new Item();
    }

    @Override
    public void registerDummies() {
        GameRegistry.registerItem(dummyItem, dummyName);
    }

    @Override
    public void addItemRemappings(Map<String, Item> remappings) {
        remappings.put(oldName, dummyItem);
    }

    @Override
    public void registerTransformations() {
        ItemStackReplacementManager
            .addItemReplacement(UtilitiesInExcess.MODID + ":" + dummyName, this::doTransformation);
    }

    public abstract NBTTagCompound doTransformation(NBTTagCompound tag);

    public void setDummyName(String dummyName) {
        this.dummyName = dummyName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }
}
