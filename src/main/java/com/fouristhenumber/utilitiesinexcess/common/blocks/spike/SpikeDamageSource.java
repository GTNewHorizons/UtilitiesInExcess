package com.fouristhenumber.utilitiesinexcess.common.blocks.spike;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class SpikeDamageSource extends DamageSource {

    private final ItemStack fakeWeapon;
    private final spikeTypes type;

    public SpikeDamageSource(String name, ItemStack fakeWeapon, spikeTypes type) {
        super(name);
        this.fakeWeapon = fakeWeapon;
        this.type = type;
    }

    public ItemStack getFakeWeapon() {
        return fakeWeapon;
    }

    public spikeTypes getType() {
        return type;
    }

    public enum spikeTypes {
        WOOD,
        IRON,
        GOLD,
        DIAMOND;
    }
}
