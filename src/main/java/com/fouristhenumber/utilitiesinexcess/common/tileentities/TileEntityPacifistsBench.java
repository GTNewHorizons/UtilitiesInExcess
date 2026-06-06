package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.UUID;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;
import com.google.common.collect.Multimap;
import com.gtnewhorizon.gtnhlib.capability.item.ItemSink;
import com.gtnewhorizon.gtnhlib.item.InsertionItemStack;
import com.gtnewhorizon.gtnhlib.util.ItemUtil;
import com.mojang.authlib.GameProfile;

public class TileEntityPacifistsBench extends TileEntity {

    int weaponLocation = -1;
    IInventory currentInventory;

    @Override
    public void updateEntity() {
        if (worldObj.isRemote || worldObj.getTotalWorldTime() % BlockConfig.pacifistsBenchCooldownInTicks != 0) return;
        if (!(worldObj.difficultySetting == EnumDifficulty.PEACEFUL) && !BlockConfig.pacifistsBenchInNonPeaceful)
            return;

        ItemStack weapon = findWeapon();
        if (weapon == null) return;

        BiomeGenBase.SpawnListEntry entry = ((WorldServer) worldObj)
            .spawnRandomCreature(EnumCreatureType.monster, xCoord, yCoord, zCoord);

        EntityLiving entity;
        try {
            entity = entry.entityClass.getConstructor(World.class)
                .newInstance(worldObj);
        } catch (Exception exception) {
            UtilitiesInExcess.LOG.error("Failed to construct EntityLiving from constructor");
            return;
        }

        // Basically just makes wither skeletons spawn properly
        entity.onSpawnWithEgg(null);
        entity.setLocationAndAngles(xCoord + 0.5, yCoord + 1, zCoord + 0.5, worldObj.rand.nextFloat() * 360F, 0);

        worldObj.spawnEntityInWorld(entity);

        FakePlayer fakePlayer = makeFakePlayer(weapon);

        int hits = 0;
        // Hit repeatedly to correctly simulate damage and durability
        while (entity.getHealth() > 0.0) {
            // Just give up at this point, so it doesn't lock out if there's some insane modded interaction
            if (hits > 500) break;
            entity.hurtResistantTime = 0;
            fakePlayer.attackTargetEntityWithCurrentItem(entity);
            entity.motionX = entity.motionY = entity.motionZ = 0;
            hits++;
        }

        currentInventory.setInventorySlotContents(weaponLocation, fakePlayer.getCurrentEquippedItem());
    }

    public void receiveItemStack(ItemStack stack) {
        if (currentInventory != null) {
            ItemSink sink = ItemUtil.getItemSink(currentInventory, ForgeDirection.UP);
            if (sink != null) sink.store(new InsertionItemStack(stack));
        }
    }

    private ItemStack findWeapon() {
        if (currentInventory != null) {
            ItemStack weaponStack = currentInventory.getStackInSlot(weaponLocation);
            if (weaponStack != null && weaponStack.getItem() instanceof ItemSword) return weaponStack;
        }

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (worldObj.getTileEntity(
                xCoord + dir.offsetX,
                yCoord + dir.offsetY,
                zCoord + dir.offsetZ) instanceof IInventory inv) {
                for (int i = 0; i < inv.getSizeInventory(); i++) {
                    ItemStack stack = inv.getStackInSlot(i);
                    if (stack != null && stack.getItem() instanceof ItemSword) {
                        weaponLocation = i;
                        currentInventory = inv;
                        return stack;
                    }
                }
            }
        }
        return null;
    }

    private FakePlayer makeFakePlayer(ItemStack weapon) {
        FakePlayer fakePlayer = FakePlayerFactory.get(
            (WorldServer) worldObj,
            new GameProfile(UUID.nameUUIDFromBytes("UIE_Pacifist".getBytes()), "[UIE Pacifist's Bench]"));

        fakePlayer.setCurrentItemOrArmor(0, weapon);

        Multimap<String, AttributeModifier> modifiers = weapon.getAttributeModifiers();
        fakePlayer.getAttributeMap()
            .applyAttributeModifiers(modifiers);

        NBTTagCompound tag = fakePlayer.getEntityData();

        tag.setBoolean("isPacifistsBench", true);

        tag.setInteger("x", xCoord);
        tag.setInteger("y", yCoord);
        tag.setInteger("z", zCoord);

        return fakePlayer;
    }
}
