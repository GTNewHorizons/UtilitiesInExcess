package com.fouristhenumber.utilitiesinexcess.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

// TODO: maybe change the icon?
// TODO: hide this from nei!
// Handle disabled items, so they won't be just deleted
public class ItemDisabled extends Item {

    private final Item enabledVersion;

    public ItemDisabled(Item enabledVersion) {
        super();
        setUnlocalizedName("disabled_item");
        setTextureName("utilitiesinexcess:disabled_item");
        this.enabledVersion = enabledVersion;
    }

    @Override
    public String getItemStackDisplayName(ItemStack p_77653_1_) {
        String localizedEnabledItem = StatCollector
            .translateToLocal(enabledVersion.getUnlocalizedNameInefficiently(p_77653_1_) + ".name");
        return StatCollector.translateToLocalFormatted(
            this.getUnlocalizedNameInefficiently(p_77653_1_) + ".name",
            localizedEnabledItem);
    }
}
