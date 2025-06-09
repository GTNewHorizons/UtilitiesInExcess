package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;
import java.util.Random;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.config.BlockConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BlockCursedEarth extends Block {

    // TODO: Handle spreading? Look into how we wanna do that
    // if/when we handle
    public BlockCursedEarth() {
        super(Material.ground);
        setBlockName("cursedEarth");
        setBlockTextureName("utilitiesinexcess:cursed_earth");
        this.setTickRandomly(true);
    }

    @Override
    public void updateTick(World world, int x, int y, int z,
                           Random random) {
        super.updateTick(world, x, y, z, random);
        if (world.isRemote) return;
        if (!world.isAirBlock(x, y + 1, z)) return;
        if (world.getBlockLightValue(x, y + 1, z) >= 8) return;
        if (random.nextInt(100) >= BlockConfig.CursedEarth.cursedEarthSpawnRate) return;

        AxisAlignedBB spawnArea = AxisAlignedBB.getBoundingBox(
            x, y + 1, z,
            x + 1, y + 2, z + 1
        );
        List<EntityLiving> entitiesAbove = world.getEntitiesWithinAABB(
            EntityLiving.class, spawnArea
        );
        if (!entitiesAbove.isEmpty()) return;


        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        List<BiomeGenBase.SpawnListEntry> monsterList =
            biome.getSpawnableList(EnumCreatureType.monster);

        if (monsterList == null || monsterList.isEmpty()) return;

        BiomeGenBase.SpawnListEntry spawnEntry =
            monsterList.get(random.nextInt(monsterList.size()));

        EntityLiving mob;
        try {
            mob = spawnEntry.entityClass.getConstructor(World.class).newInstance(world);
        } catch (Exception e) {
            UtilitiesInExcess.LOG.error("Error while spawning mob {}", spawnEntry);
            UtilitiesInExcess.LOG.error(e.getStackTrace());
            return;
        }

        mob.setLocationAndAngles(
            x + 0.5D,
            y + 1D,
            z + 0.5D,
            MathHelper.wrapAngleTo180_float(random.nextFloat() * 360.0F),
            0.0F
        );

        world.spawnEntityInWorld(mob);
    }

    public static class ItemBlockCursedEarth extends ItemBlock {

        public ItemBlockCursedEarth(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
            tooltip.add(StatCollector.translateToLocal("tile.cursedEarth.desc"));
            super.addInformation(stack, player, tooltip, p_77624_4_);
        }
    }
}
