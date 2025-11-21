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
import com.google.common.collect.Multimap;
import com.gtnewhorizon.gtnhlib.capability.item.ItemSink;
import com.gtnewhorizon.gtnhlib.item.InsertionItemStack;
import com.gtnewhorizon.gtnhlib.util.ItemUtil;
import com.mojang.authlib.GameProfile;

public class TileEntityPeacefulTable extends TileEntity {

    ItemStack currentWeapon;
    IInventory currentInventory;

    @Override
    public void updateEntity() {
        if (worldObj.isRemote || !(worldObj.difficultySetting == EnumDifficulty.PEACEFUL)) return;
        if (worldObj.getTotalWorldTime() % 5 != 0) return;

        if (currentWeapon == null && !findWeapon()) return;

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

        entity.onSpawnWithEgg(null);
        entity.setLocationAndAngles(xCoord + 0.5, yCoord + 1, zCoord + 0.5, worldObj.rand.nextFloat() * 360F, 0);

        worldObj.spawnEntityInWorld(entity);

        FakePlayer fakePlayer = makeFakePlayer();

        int hits = 0;
        // Hit repeatedly to correctly simulate damage and durability
        while (!entity.isDead) {
            // Just give up at this point, so it doesn't lock out
            if (hits > 500) return;
            entity.hurtResistantTime = 0;
            fakePlayer.attackTargetEntityWithCurrentItem(entity);
            entity.motionX = entity.motionY = entity.motionZ = 0;
            hits++;
        }
    }

    public void receiveItemStack(ItemStack stack) {
        if (currentInventory != null) {
            ItemSink sink = ItemUtil.getItemSink(currentInventory, ForgeDirection.UP);
            if (sink != null) sink.store(new InsertionItemStack(stack));
        }
    }

    private boolean findWeapon() {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (worldObj.getTileEntity(
                xCoord + dir.offsetX,
                yCoord + dir.offsetY,
                zCoord + dir.offsetZ) instanceof IInventory inv) {
                for (int i = 0; i < inv.getSizeInventory(); i++) {
                    ItemStack stack = inv.getStackInSlot(i);
                    if (stack != null && stack.getItem() instanceof ItemSword) {
                        currentWeapon = stack;
                        currentInventory = inv;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private FakePlayer makeFakePlayer() {
        FakePlayer fakePlayer = FakePlayerFactory.get(
            (WorldServer) worldObj,
            new GameProfile(UUID.nameUUIDFromBytes("UIE_Peaceful".getBytes()), "[UIE Peaceful Table]"));

        fakePlayer.setCurrentItemOrArmor(0, currentWeapon);

        Multimap<String, AttributeModifier> modifiers = currentWeapon.getAttributeModifiers();
        fakePlayer.getAttributeMap()
            .applyAttributeModifiers(modifiers);

        NBTTagCompound tag = fakePlayer.getEntityData();

        tag.setBoolean("isPeacefulTable", true);

        tag.setInteger("x", xCoord);
        tag.setInteger("y", yCoord);
        tag.setInteger("z", zCoord);

        return fakePlayer;
    }
}
