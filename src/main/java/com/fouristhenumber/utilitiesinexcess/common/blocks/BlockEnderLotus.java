package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.config.blocks.EnderLotusConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEnderLotus extends BlockCrops {

    private static final int MAX_STAGE = 7;

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public BlockEnderLotus() {
        this.setBlockName("ender_lotus");
        this.setTickRandomly(true);
        this.setHardness(0.0F);
        this.setStepSound(soundTypeGrass);
        this.setCreativeTab(null);
        this.disableStats();
    }

    @Override
    protected Item func_149866_i() {
        return ModItems.ENDER_LOTUS_SEED.get();
    }

    @Override
    protected Item func_149865_P() {
        return Items.ender_pearl;
    }

    @Override
    public boolean canPlaceBlockOn(Block ground) {
        return ground == Blocks.end_stone || ground == Blocks.dirt || ground == Blocks.grass;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        if (world.isRemote) return;
        int meta = world.getBlockMetadata(x, y, z);
        if (meta >= MAX_STAGE) return;

        boolean onEndstone = world.getBlock(x, y - 1, z) == Blocks.end_stone;
        int chance = onEndstone ? EnderLotusConfig.growthTicksOnEndstone : EnderLotusConfig.growthTicksOnDirt;

        if (rand.nextInt(chance) == 0) {
            world.setBlockMetadataWithNotify(x, y, z, meta + 1, 2);
        }
    }

    // fertilize()
    @Override
    public void func_149853_b(World world, Random rand, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        world.setBlockMetadataWithNotify(x, y, z, 7, 2);
        if (meta > 0) {
            world.setBlockMetadataWithNotify(x, y, z, 7, 2);
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta > 3 && !world.isRemote && entity instanceof EntityLivingBase) {
            entity.attackEntityFrom(DamageSource.cactus, 0.1F);
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        Random rand = world.rand;
        Item enderLotusSeed = ModItems.ENDER_LOTUS_SEED.get();

        drops.add(new ItemStack(enderLotusSeed, 1));
        if (metadata >= MAX_STAGE) {
            drops.add(new ItemStack(Items.ender_pearl, 1));

            if (world.getBlock(x, y - 1, z) == Blocks.end_stone
                && rand.nextDouble() < EnderLotusConfig.extraSeedChanceOnEndstone) {
                drops.add(new ItemStack(enderLotusSeed, 1));
            }
        }
        return drops;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        icons = new IIcon[MAX_STAGE + 1];
        for (int i = 0; i <= MAX_STAGE; i++) {
            icons[i] = reg.registerIcon("utilitiesinexcess:crops/ender_lotus_stage_" + i);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta < 0) meta = 0;
        if (meta > MAX_STAGE) meta = MAX_STAGE;
        return icons[meta];
    }

    @Override
    public int getRenderType() {
        return 1;
    }
}
