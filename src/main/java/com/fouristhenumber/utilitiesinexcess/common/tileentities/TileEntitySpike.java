package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockSpike;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;

public class TileEntitySpike extends TileEntity {

    private ItemStack fakeWeapon = null;
    private BlockSpike.SpikeType spikeType;

    public TileEntitySpike() {}

    public TileEntitySpike(BlockSpike.SpikeType spikeType) {
        this.spikeType = spikeType;
    }

    public void setFakeWeapon(ItemStack stack) {
        this.fakeWeapon = stack;
        markDirty();
    }

    public ItemStack getFakeWeapon() {
        return fakeWeapon;
    }

    public void damageEntity(Entity entity) {
        if (worldObj.isRemote || entity == null || fakeWeapon == null) return;

        FakePlayer fakePlayer = FakePlayerFactory.get(
            (WorldServer) worldObj,
            new GameProfile(UUID.nameUUIDFromBytes("UIE_Spike".getBytes()), "[UIE Spike]"));

        fakePlayer.setCurrentItemOrArmor(0, fakeWeapon.copy());

        Multimap<String, AttributeModifier> modifiers = fakeWeapon.getAttributeModifiers();
        fakePlayer.getAttributeMap()
            .applyAttributeModifiers(modifiers);

        double prevMotionX = entity.motionX;
        double prevMotionY = entity.motionY;
        double prevMotionZ = entity.motionZ;

        fakePlayer.attackTargetEntityWithCurrentItem(entity);

        entity.motionX = prevMotionX;
        entity.motionY = prevMotionY;
        entity.motionZ = prevMotionZ;

        fakePlayer.setCurrentItemOrArmor(0, null);
    }

    public BlockSpike.SpikeType getSpikeType() {
        return spikeType;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("FakeWeapon")) {
            fakeWeapon = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("FakeWeapon"));
        }

        if (tag.hasKey("SpikeType")) {
            try {
                spikeType = BlockSpike.SpikeType.valueOf(tag.getString("SpikeType"));
            } catch (IllegalArgumentException e) {
                spikeType = BlockSpike.SpikeType.WOOD;
            }
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
        tag.setString("SpikeType", spikeType.name());
    }
}
