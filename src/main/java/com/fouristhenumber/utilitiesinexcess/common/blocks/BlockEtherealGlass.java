package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEtherealGlass extends BlockGlass {

    public BlockEtherealGlass() {
        super(Material.glass, false);
        setBlockName("etherealGlass");
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask,
        List<AxisAlignedBB> list, Entity collider) {
        int meta = worldIn.getBlockMetadata(x, y, z);

        if ((meta == 0 || meta == 2) && collider instanceof EntityPlayer) {
            return;
        }

        if ((meta == 3 || meta == 5) && !(collider instanceof EntityPlayer)) {
            return;
        }

        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
    }

    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        return (meta == 0 || meta == 3) ? 0 : 255;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0)); // Normal
        list.add(new ItemStack(this, 1, 2)); // Dark
        list.add(new ItemStack(this, 1, 3)); // Inverted
        list.add(new ItemStack(this, 1, 5)); // Dark Inverted
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[16];
        icons[0] = iconRegister.registerIcon("utilitiesinexcess:etherealGlass");
        icons[2] = iconRegister.registerIcon("utilitiesinexcess:etherealGlassDark");
        icons[3] = iconRegister.registerIcon("utilitiesinexcess:etherealGlassInverted");
        icons[5] = iconRegister.registerIcon("utilitiesinexcess:etherealGlassDarkInverted");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta < 0 || meta >= icons.length) {
            meta = 0;
        }
        return icons[meta];
    }

    public static class ItemBlockEtherealGlass extends ItemBlock {

        public ItemBlockEtherealGlass(Block block) {
            super(block);
            setHasSubtypes(true);
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
