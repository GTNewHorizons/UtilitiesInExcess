package com.fouristhenumber.utilitiesinexcess.common.items;

import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.formatNumber;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;

public class ItemCapacityUpgrade extends Item {

    public ItemCapacityUpgrade() {
        this.setUnlocalizedName("capacity_upgrade");
        this.setTextureName("utilitiesinexcess:capacity_upgrade");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(
            StatCollector.translateToLocalFormatted(
                "item.capacity_upgrade.desc.1",
                formatNumber(BlockConfig.filingCabinets.upgradeCapacity)));
        tooltip.add(
            StatCollector.translateToLocalFormatted(
                "item.capacity_upgrade.desc.2",
                formatNumber(BlockConfig.filingCabinets.upgradeCountMax)));
        super.addInformation(stack, player, tooltip, advanced);
    }
}
