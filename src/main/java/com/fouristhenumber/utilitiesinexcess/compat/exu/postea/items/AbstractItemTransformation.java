package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items;

import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.IPosteaTransformation;
import com.gtnewhorizons.postea.api.ItemStackReplacementManager;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * A simple {@link IPosteaTransformation} that transforms a single item.
 * Extend and use setDummyName and setOldName then do your transformations in doTransformation
 * <p>
 * See {@link WateringCanTransformation} for an example.
 */
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

    /**
     * Set the name for this transformation's dummy item *without* the modid, e.g. "dummy_golden_bag".
     *
     * @param dummyName The registry name for this transformation's dummy item
     */
    public void setDummyName(String dummyName) {
        this.dummyName = dummyName;
    }

    /**
     * Set the *full registry name* (e.g. "modid:item") of the old item this transformation is replacing
     *
     * @param oldName The registry name for the item this transformation is replacing
     */
    public void setOldName(String oldName) {
        this.oldName = oldName;
    }
}
