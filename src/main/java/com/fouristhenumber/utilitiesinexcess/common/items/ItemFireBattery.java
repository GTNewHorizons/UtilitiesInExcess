package com.fouristhenumber.utilitiesinexcess.common.items;

import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.formatNumber;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.fouristhenumber.utilitiesinexcess.config.items.FireBatteryConfig;

import cofh.api.energy.ItemEnergyContainer;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemFireBattery extends ItemEnergyContainer implements IFuelHandler {

    public ItemFireBattery() {
        super(
            FireBatteryConfig.fireBatteryRFStorage,
            FireBatteryConfig.fireBatteryRFCharge,
            FireBatteryConfig.fireBatteryRFUsage);
        setUnlocalizedName("fire_battery");
        setTextureName("utilitiesinexcess:fire_battery");
        setMaxStackSize(1);
        GameRegistry.registerFuelHandler(this);
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        ItemStack drained = itemStack.copy();
        drained.stackSize = 1;
        extractEnergy(drained, FireBatteryConfig.fireBatteryRFUsage, false);
        return drained;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public int getBurnTime(ItemStack fuel) {
        if (fuel == null) return 0;
        return extractEnergy(fuel, FireBatteryConfig.fireBatteryRFUsage, true) / FireBatteryConfig.fireBatteryBurnTime;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0 - (double) getEnergyStored(stack) / (double) getMaxEnergyStored(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getEnergyStored(stack) < getMaxEnergyStored(stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean b) {
        tooltip.add(
            StatCollector.translateToLocalFormatted(
                "item.fire_battery.desc",
                formatNumber(getEnergyStored(stack)),
                formatNumber(getMaxEnergyStored(stack))));
        super.addInformation(stack, player, tooltip, b);
    }
}
