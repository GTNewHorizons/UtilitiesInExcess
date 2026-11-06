package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDecorative extends Block {

    private static final int META_VALUES = 12;

    public BlockDecorative() {
        super(Material.rock);
        setBlockName("decorative_block");
        setStepSound(soundTypeStone);
        setResistance(6F);
    }

    @Override
    public float getBlockHardness(World worldIn, int x, int y, int z) {
        if (worldIn == null) return 1f;
        int meta = worldIn.getBlockMetadata(x, y, z);
        return switch (meta) {
            // Obsidian
            case 0, 5 -> 50F;
            // Endstone
            case 1 -> 3F;
            // Quartz
            case 2 -> 0.8F;
            // Sand
            case 3 -> 0.5F;
            // Gravel
            case 6, 10 -> 0.6F;
            // Stone
            default -> 1.5F;
        };
    }

    @Override
    public int getHarvestLevel(int metadata) {
        return switch (metadata) {
            case 3, 6, 10 -> 0;
            case 0, 5 -> 3;
            default -> 1;
        };
    }

    @Override
    public boolean isToolEffective(String type, int metadata) {
        return switch (metadata) {
            case 3, 6, 10 -> type.equals("shovel");
            default -> type.equals("pickaxe");
        };
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        if (meta == 3 || meta == 5 || meta == 10) return true;
        return super.canHarvestBlock(player, meta);
    }

    @Override
    public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double exX, double exY,
        double exZ) {
        int meta = world.getBlockMetadata(x, y, z);
        return switch (meta) {
            case 0, 5 -> 1200F;
            case 1 -> 9F;
            case 2 -> 0.8F;
            case 3 -> 0.5F;
            case 6, 10 -> 0.6F;
            default -> 6F;
        };
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < META_VALUES; ++i) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[META_VALUES];
        for (int i = 0; i < META_VALUES; i++) {
            icons[i] = iconRegister.registerIcon("utilitiesinexcess:decorative_block_" + i);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta < 0 || meta >= icons.length) {
            meta = 0;
        }
        return icons[meta];
    }

    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return 1;
    }

    public static class ItemBlockDecorative extends ItemBlock {

        public ItemBlockDecorative(Block block) {
            super(block);
            this.setHasSubtypes(true);
            this.setMaxDamage(0);
        }

        @Override
        public int getMetadata(int damage) {
            return damage;
        }

        @Override
        public String getUnlocalizedName(final ItemStack stack) {
            return this.getUnlocalizedName() + "." + stack.getItemDamage();
        }
    }
}
