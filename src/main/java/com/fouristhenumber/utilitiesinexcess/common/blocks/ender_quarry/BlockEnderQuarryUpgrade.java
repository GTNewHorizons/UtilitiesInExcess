package com.fouristhenumber.utilitiesinexcess.common.blocks.ender_quarry;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEnderQuarryUpgrade extends Block {

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public BlockEnderQuarryUpgrade() {
        super(Material.iron);
        setHardness(1f);
        setBlockName("utilitiesinexcess:ender_quarry_upgrade");
        setBlockBounds(0.5F / 16F, 1.5F / 16F, 0.5F / 16F, 15.5F / 16F, 15F / 16F, 15.5F / 16F);
        setLightOpacity(0);
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask,
        List<AxisAlignedBB> list, Entity collider) {
        this.setBlockBounds(0.5F / 16F, 1.5F / 16F, 0.5F / 16F, 15.5F / 16F, 15F / 16F, 15.5F / 16F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < EnderQuarryUpgradeManager.EnderQuarryUpgrade.VALUES.length; ++i) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        icons = new IIcon[EnderQuarryUpgradeManager.EnderQuarryUpgrade.VALUES.length];
        for (int i = 0; i < EnderQuarryUpgradeManager.EnderQuarryUpgrade.VALUES.length; i++) {
            EnderQuarryUpgradeManager.EnderQuarryUpgrade upgrade = EnderQuarryUpgradeManager.EnderQuarryUpgrade.VALUES[i];
            icons[i] = reg.registerIcon(upgrade.getTextureName());
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    public static class ItemEnderQuarryUpgrade extends ItemBlock {

        public ItemEnderQuarryUpgrade(Block block) {
            super(block);
            setMaxDamage(0);
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
