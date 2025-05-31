package com.fouristhenumber.utilitiesinexcess.common.blocks.spike;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;

public class TileEntitySpike extends TileEntity {

    private ItemStack fakeWeapon = null;
    private final SpikeDamageSource.spikeTypes spikeType;

    public TileEntitySpike(SpikeDamageSource.spikeTypes spikeType) {
        this.spikeType = spikeType;
    }

    public void setFakeWeapon(ItemStack stack) {
        this.fakeWeapon = stack;
    }

    public ItemStack getFakeWeapon() {
        return fakeWeapon;
    }

    public void damageEntity(Entity entity) {
        if (worldObj.isRemote || entity == null) return;

        DamageSource source = new SpikeDamageSource("ue_spike", fakeWeapon, spikeType);
        entity.attackEntityFrom(source, 1F);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("FakeWeapon")) {
            fakeWeapon = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("FakeWeapon"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (fakeWeapon != null) {
            NBTTagCompound weaponTag = new NBTTagCompound();
            fakeWeapon.writeToNBT(weaponTag);
            tag.setTag("FakeWeapon", weaponTag);
        }
    }
}
