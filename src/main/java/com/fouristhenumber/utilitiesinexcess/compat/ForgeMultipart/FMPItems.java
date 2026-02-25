package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemDisabled;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.UEMultipartItem;
import com.fouristhenumber.utilitiesinexcess.config.items.ItemConfig;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum FMPItems
{
    UE_MULTI_PART(true, new UEMultipartItem(), "multi_part_item");

    public static final FMPItems[] VALUES = values();

    public static void init() {
        for (FMPItems item : VALUES) {
            if (item.isEnabled()) {
                item.theItem.setCreativeTab(UtilitiesInExcess.uieTab);
                GameRegistry.registerItem(item.get(), item.name);
            } else if (ItemConfig.registerDisabledItems) GameRegistry.registerItem(item.disabledVersion, item.name);
        }
    }

    private final boolean isEnabled;
    private final Item theItem;
    private final String name;
    private final ItemDisabled disabledVersion;

    FMPItems(boolean enabled, Item item, String name) {
        this.isEnabled = enabled;
        theItem = item;
        this.name = name;
        if (ItemConfig.registerDisabledItems) disabledVersion = new ItemDisabled(theItem);
        else disabledVersion = null;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public Item get() {
        return theItem;
    }

    public ItemStack newItemStack() {
        return newItemStack(1);
    }

    public ItemStack newItemStack(int count) {
        return newItemStack(count, 0);
    }

    public ItemStack newItemStack(int count, int meta) {
        return new ItemStack(this.get(), count, meta);
    }
}
