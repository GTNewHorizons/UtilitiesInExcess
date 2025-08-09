package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.config.blocks.CursedEarthConfig;

public class BlockCursedEarth extends Block {

    // TODO: Handle spreading? Look into how we wanna do that
    // if/when we handle the sigil etc

    // Most of the logic for cursed earth interactions with spawners is
    // implemented in mixins.early.minecraft.CursedEarthSpawner
    public BlockCursedEarth() {
        super(Material.ground);
        setBlockName("cursed_earth");
        setBlockTextureName("utilitiesinexcess:cursed_earth");
        this.setHardness(0.5F);
        this.setResistance(200.0F);
        this.setStepSound(soundTypeGravel);
        this.setTickRandomly(true);
    }

    @Override
    public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        ItemStack tool = player.getCurrentEquippedItem();
        return tool != null && tool.getItem() instanceof ItemSpade;
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        for (int i = 0; i < 4; i++) {
            double px = x + random.nextFloat();
            double py = y + random.nextFloat();
            double pz = z + random.nextFloat();
            double vx = (random.nextFloat() - 0.5) * 0.5;
            double vy = (random.nextFloat() - 0.5) * 0.5;
            double vz = (random.nextFloat() - 0.5) * 0.5;
            world.spawnParticle("portal", px, py, pz, vx, vy, vz);
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        // Change Items.diamond to any item you want it to drop
        drops.add(new ItemStack(Item.getItemFromBlock(Blocks.dirt), 1));
        return drops;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        super.updateTick(world, x, y, z, random);
        if (world.isRemote) return;
        if (!world.isAirBlock(x, y + 1, z)) return;
        if (world.getBlockLightValue(x, y + 1, z) >= 8) return;
        if (!world.getGameRules()
            .getGameRuleBooleanValue("doMobSpawning")) return;
        if (world.difficultySetting == EnumDifficulty.PEACEFUL) return;
        if (random.nextInt(100) >= CursedEarthConfig.cursedEarthSpawnRate) return;

        AxisAlignedBB spawnArea = AxisAlignedBB.getBoundingBox(x, y + 1, z, x + 1, y + 2, z + 1);
        List<EntityLiving> entitiesAbove = world.getEntitiesWithinAABB(EntityLiving.class, spawnArea);
        if (!entitiesAbove.isEmpty()) return;

        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        List<BiomeGenBase.SpawnListEntry> monsterList = biome.getSpawnableList(EnumCreatureType.monster);

        if (monsterList == null || monsterList.isEmpty()) return;

        BiomeGenBase.SpawnListEntry spawnEntry = monsterList.get(random.nextInt(monsterList.size()));

        EntityLiving mob;
        try {
            mob = spawnEntry.entityClass.getConstructor(World.class)
                .newInstance(world);
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
            0.0F);

        world.spawnEntityInWorld(mob);
    }

    /*
     * @Override
     * public TileEntity createNewTileEntity(World worldIn, int meta) {
     * return new TileEntityCursedEarth();
     * }
     */

    public static class ItemBlockCursedEarth extends ItemBlock {

        public ItemBlockCursedEarth(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
            tooltip.add(StatCollector.translateToLocal("tile.cursed_earth.desc"));
            super.addInformation(stack, player, tooltip, p_77624_4_);
        }
    }
}
