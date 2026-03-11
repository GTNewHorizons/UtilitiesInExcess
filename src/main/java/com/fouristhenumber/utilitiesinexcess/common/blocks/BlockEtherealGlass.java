package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;
import java.util.Random;

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

// Extending glass makes the rendering work properly
public class BlockEtherealGlass extends BlockGlass {

    public enum EtherealGlassType {

        NORMAL(0, "ethereal_glass"),
        INEFFABLE(1, "ineffable_glass"),
        DARK(2, "ethereal_glass_dark"),
        INVERTED(3, "ethereal_glass_inverted"),
        INEFFABLE_INVERTED(4, "ineffable_glass_inverted"),
        DARK_INVERTED(5, "ethereal_glass_dark_inverted");

        public final int meta;
        public final String textureName;

        EtherealGlassType(int meta, String textureName) {
            this.meta = meta;
            this.textureName = textureName;
        }
    }

    public BlockEtherealGlass() {
        super(Material.glass, false);
        setBlockName("ethereal_glass");
        setHardness(0.3F);
        setStepSound(soundTypeGlass);
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    // Have to override glass method back to the default
    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask,
        List<AxisAlignedBB> list, Entity collider) {
        int meta = worldIn.getBlockMetadata(x, y, z);

        if ((meta == EtherealGlassType.NORMAL.meta || meta == EtherealGlassType.DARK.meta
            || meta == EtherealGlassType.INEFFABLE.meta) && collider instanceof EntityPlayer player
            && !player.isSneaking()) {
            return;
        }

        if ((meta == EtherealGlassType.INVERTED.meta || meta == EtherealGlassType.DARK_INVERTED.meta
            || meta == EtherealGlassType.INEFFABLE_INVERTED.meta)
            && !(collider instanceof EntityPlayer player && !player.isSneaking())) {
            return;
        }

        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
    }

    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        return (meta == EtherealGlassType.DARK.meta || meta == EtherealGlassType.DARK_INVERTED.meta) ? 255 : 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (EtherealGlassType value : EtherealGlassType.values()) {
            list.add(new ItemStack(this, 1, value.meta));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[16];

        for (EtherealGlassType value : EtherealGlassType.values()) {
            icons[value.meta] = iconRegister.registerIcon("utilitiesinexcess:" + value.textureName);
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
