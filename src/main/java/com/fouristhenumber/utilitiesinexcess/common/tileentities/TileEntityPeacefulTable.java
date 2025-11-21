package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;

public class TileEntityPeacefulTable extends TileEntity {

    @Override
    public void updateEntity() {
        if (worldObj.isRemote || !(worldObj.difficultySetting == EnumDifficulty.PEACEFUL)) return;
        if (worldObj.getTotalWorldTime() % 40 != 0) return;

        BiomeGenBase biome = worldObj.getBiomeGenForCoords(xCoord, zCoord);
        List<BiomeGenBase.SpawnListEntry> list = biome.getSpawnableList(EnumCreatureType.monster);

        if (list == null || list.isEmpty()) return;

        BiomeGenBase.SpawnListEntry entry = (BiomeGenBase.SpawnListEntry) WeightedRandom
            .getRandomItem(worldObj.rand, list);
        EntityLiving entity = null;
        try {
            entity = entry.entityClass.getConstructor(new Class[] { World.class })
                .newInstance(worldObj);
        } catch (Exception exception) {
            UtilitiesInExcess.LOG.error("Failed to construct EntityLiving from constructor");
        }
        if (entity == null) return;

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
        UtilitiesInExcess.LOG.info("Peaceful table captured {}", stack.getDisplayName());
    }

    private FakePlayer makeFakePlayer() {
        FakePlayer fakePlayer = FakePlayerFactory.get(
            (WorldServer) worldObj,
            new GameProfile(UUID.nameUUIDFromBytes("UIE_Peaceful".getBytes()), "[UIE Peaceful Table]"));

        ItemStack fakeWeapon = new ItemStack(Items.diamond_axe);

        fakePlayer.setCurrentItemOrArmor(0, fakeWeapon);

        Multimap<String, AttributeModifier> modifiers = fakeWeapon.getAttributeModifiers();
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
