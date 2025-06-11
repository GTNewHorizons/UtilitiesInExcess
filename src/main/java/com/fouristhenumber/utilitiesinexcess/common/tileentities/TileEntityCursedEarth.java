package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.lang.reflect.Field;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

public class TileEntityCursedEarth extends TileEntity {

    private MobSpawnerBaseLogic spawner;

    public TileEntityCursedEarth() {}

    public void setSpawner(@Nullable MobSpawnerBaseLogic spawner) {
        this.spawner = spawner;
    }

    // To handle being loaded in.
    private boolean initialized = false;

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) return;
        if (!initialized) {
            checkForSpawner(worldObj, xCoord, yCoord, zCoord);
            initialized = true;
        }
        spawnLogic();
    }

    public boolean SpawnMob(EntityLiving entity) {
        if (!canSpawnHere(entity)) return false;

        // Initialize with no spawn data
        entity.onSpawnWithEgg(null);

        // Checks if hostile mob
        if (entity instanceof IMob) {
            entity.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 3600, 0));
            entity.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 3600, 0));
        } else {
            entity.addPotionEffect(new PotionEffect(Potion.regeneration.id, 3600, 0));
        }

        // Prevent despawning
        entity.func_110163_bv();

        worldObj.spawnEntityInWorld(entity);
        return true;
    }

    public void spawnLogic() {
        if (spawner == null) return;
        if (spawner.spawnDelay > 0) {
            spawner.spawnDelay -= 100;
            return;
        }

        if (spawner.spawnDelay < 0) {
            spawner.spawnDelay = 0;
        }

        UtilitiesInExcess.LOG.info("Attempting spawns");

        int spawnCount = getSpawnerField(spawner, "spawnCount");
        int maxNearby = getSpawnerField(spawner, "maxNearbyEntities");
        int spawnRange = getSpawnerField(spawner, "spawnRange");

        boolean spawned = false;

        for (int i = 0; i < spawnCount; i++) {
            Entity entity = EntityList.createEntityByName(spawner.getEntityNameToSpawn(), worldObj);
            if (!(entity instanceof EntityLiving living)) continue;

            // Check nearby entity count
            int nearby = worldObj
                .getEntitiesWithinAABB(
                    living.getClass(),
                    AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                        .expand(spawnRange, 4, spawnRange))
                .size();

            if (nearby >= maxNearby) continue;

            // Random position within range
            double x = xCoord + (worldObj.rand.nextDouble() - 0.5D) * spawnRange * 2.0D;
            double y = yCoord + worldObj.rand.nextInt(3);
            double z = zCoord + (worldObj.rand.nextDouble() - 0.5D) * spawnRange * 2.0D;

            living.setLocationAndAngles(x, y, z, worldObj.rand.nextFloat() * 360.0F, 0.0F);

            if (SpawnMob(living)) {
                spawned = true;
            }
        }

        if (spawned) {
            UtilitiesInExcess.LOG.info("Mob spawned, resetting timer");
            int minSpawnDelay = getSpawnerField(spawner, "minSpawnDelay");
            int maxSpawnDelay = getSpawnerField(spawner, "maxSpawnDelay");
            if (maxSpawnDelay <= minSpawnDelay) {
                spawner.spawnDelay = minSpawnDelay;
            } else {
                int i = maxSpawnDelay - minSpawnDelay;
                spawner.spawnDelay = minSpawnDelay + worldObj.rand.nextInt(i);
            }
        }
    }

    private int getSpawnerField(MobSpawnerBaseLogic logic, String fieldName) {
        try {
            Field field = MobSpawnerBaseLogic.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getInt(logic);
        } catch (Exception e) {
            UtilitiesInExcess.LOG.throwing(e);
            switch (fieldName) {
                case "spawnCount":
                case "spawnRange":
                    return 4;
                case "maxNearbyEntities":
                    return 6;
                case "minSpawnDelay":
                    return 200;
                case "maxSpawnDelay":
                    return 800;
                default:
                    throw new RuntimeException(e.getMessage());
            }
        }
    }

    private boolean canSpawnHere(EntityLiving entity) {
        return worldObj.checkNoEntityCollision(entity.boundingBox)
            && worldObj.getCollidingBoundingBoxes(entity, entity.boundingBox)
                .isEmpty()
            && !worldObj.isAnyLiquid(entity.boundingBox);
    }

    public void checkForSpawner(World world, int x, int y, int z) {
        Block blockAbove = world.getBlock(x, y + 1, z);

        if (blockAbove instanceof BlockMobSpawner) {
            TileEntityMobSpawner spawner = (TileEntityMobSpawner) world.getTileEntity(x, y + 1, z);
            if (spawner != null) {
                // Gets MobSpawnerBaseLogic from the TE
                this.setSpawner(spawner.func_145881_a());
                UtilitiesInExcess.LOG.info("Spawner found");
                UtilitiesInExcess.LOG.info(spawner);
            }
        } else {
            this.setSpawner(null);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (spawner != null) {
            NBTTagCompound SpawnerNBT = new NBTTagCompound();
            spawner.writeToNBT(SpawnerNBT);
            nbt.setTag("spawner", SpawnerNBT);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (spawner != null && nbt.hasKey("spawner")) {
            spawner.readFromNBT(nbt);
        }
    }

}
