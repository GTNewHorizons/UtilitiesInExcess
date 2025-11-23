package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.config.blocks.CursedEarthConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCursedEarth extends Block {

    public final boolean blessed;

    public IIcon sideTexture;

    // TODO: Handle spreading? Look into how we wanna do that
    // if/when we handle the sigil etc

    // Most of the logic for cursed earth interactions with spawners is
    // implemented in mixins.early.minecraft.CursedEarthSpawner
    public BlockCursedEarth(boolean blessed) {
        super(Material.ground);
        this.blessed = blessed;
        if (!blessed) {
            setBlockName("cursed_earth");
            setBlockTextureName("utilitiesinexcess:cursed_earth");
            this.setStepSound(soundTypeGravel);
        } else {
            setBlockName("cursed_earth.blessed");
            setBlockTextureName("utilitiesinexcess:blessed_earth");
            this.setStepSound(soundTypeCloth);
        }
        this.setHardness(0.5F);
        this.setResistance(200.0F);
        this.setTickRandomly(true);
    }

    @Override
    public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        ItemStack tool = player.getCurrentEquippedItem();
        return tool != null && tool.getItem() instanceof ItemSpade;
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        for (int i = 0; i < (blessed ? 2 : 4); i++) {
            double px = x + random.nextFloat();
            double py = y + random.nextFloat();
            double pz = z + random.nextFloat();
            double vx = (random.nextFloat() - 0.5) * 0.5;
            double vy = (random.nextFloat() - 0.5) * 0.5;
            double vz = (random.nextFloat() - 0.5) * 0.5;
            if (blessed) {
                world.spawnParticle("enchantmenttable", px, py + 0.7, pz, vx, vy, vz);
            } else {
                world.spawnParticle("portal", px, py, pz, vx, vy, vz);
            }
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
        tryBurn(world, x, y, z, random);
        trySpawnMob(world, x, y, z, random);
    }

    public void trySpawnMob(World world, int x, int y, int z, Random random) {
        if (!world.isAirBlock(x, y + 1, z)) return;
        int light = world.getBlockLightValue(x, y + 1, z);
        if ((light >= 8 && !blessed) || light < 8 && blessed) return;
        if (!world.getGameRules()
            .getGameRuleBooleanValue("doMobSpawning")) return;
        if (world.difficultySetting == EnumDifficulty.PEACEFUL && !blessed) return;
        if (random.nextInt(100)
            >= (blessed ? CursedEarthConfig.blessedEarthSpawnRate : CursedEarthConfig.cursedEarthSpawnRate)) return;

        AxisAlignedBB spawnArea = AxisAlignedBB.getBoundingBox(x, y + 1, z, x + 1, y + 2, z + 1);
        List<EntityLiving> entitiesAbove = world.getEntitiesWithinAABB(EntityLiving.class, spawnArea);
        if (!entitiesAbove.isEmpty()) return;

        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        List<BiomeGenBase.SpawnListEntry> monsterList = blessed ? biome.getSpawnableList(EnumCreatureType.creature)
            : biome.getSpawnableList(EnumCreatureType.monster);

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

        if (!mob.getCanSpawnHere()) return;

        world.spawnEntityInWorld(mob);
    }

    public void tryBurn(World world, int x, int y, int z, Random random) {
        if (random.nextInt(4) != 0) return;

        Block aboveBlock = world.getBlock(x, y + 1, z);
        if (world.isSideSolid(x, y + 1, z, ForgeDirection.DOWN) && !(aboveBlock instanceof BlockMobSpawner)) {
            world.setBlock(x, y, z, Blocks.dirt);
            return;
        }

        if (blessed && !CursedEarthConfig.enableBlessedEarthBurn) return;

        if (world.isAirBlock(x, y + 1, z) && shouldBurn(world, x, y, z)) {
            world.setBlock(x, y + 1, z, Blocks.fire);
            return;
        }

        if (aboveBlock instanceof BlockFire) {
            world.setBlock(x, y, z, Blocks.dirt);
        }
    }

    public boolean shouldBurn(World world, int x, int y, int z) {
        boolean lit = world.getBlockLightValue(x, y + 1, z) >= 8;
        if (blessed) {
            return !lit || (world.canBlockSeeTheSky(x, y + 1, z) && !world.isDaytime());
        }
        return lit;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_top");
        this.sideTexture = reg.registerIcon(this.getTextureName() + "_side");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.blockIcon : (side == 0 ? Blocks.dirt.getBlockTextureFromSide(side) : this.sideTexture);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
        return getIcon(side, 0);
    }

    @Override
    public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return face == ForgeDirection.UP;
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
            if (((BlockCursedEarth) field_150939_a).blessed) {
                tooltip.add(StatCollector.translateToLocal("tile.cursed_earth.blessed.desc"));
            } else {
                tooltip.add(StatCollector.translateToLocal("tile.cursed_earth.desc"));
            }
            super.addInformation(stack, player, tooltip, p_77624_4_);
        }
    }
}
