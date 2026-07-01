package com.fouristhenumber.utilitiesinexcess.common.blocks.voidquarry;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.blocks.voidquarry.VoidQuarryUpgradeManager.TieredVoidQuarryUpgrade;
import com.fouristhenumber.utilitiesinexcess.common.blocks.voidquarry.VoidQuarryUpgradeManager.VoidQuarryUpgrade;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockVoidQuarryUpgrade extends Block {

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public BlockVoidQuarryUpgrade() {
        super(Material.iron);
        setHardness(1f);
        setBlockName("void_quarry_upgrade");
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
        for (int i = 0; i < VoidQuarryUpgradeManager.VoidQuarryUpgrade.VALUES.length; ++i) {
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
        icons = new IIcon[VoidQuarryUpgradeManager.VoidQuarryUpgrade.VALUES.length];
        for (int i = 0; i < VoidQuarryUpgradeManager.VoidQuarryUpgrade.VALUES.length; i++) {
            VoidQuarryUpgradeManager.VoidQuarryUpgrade upgrade = VoidQuarryUpgradeManager.VoidQuarryUpgrade.VALUES[i];
            icons[i] = reg.registerIcon(upgrade.getTextureName());
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    public static class ItemVoidQuarryUpgrade extends ItemBlock {

        public ItemVoidQuarryUpgrade(Block block) {
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

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            VoidQuarryUpgrade upgrade = VoidQuarryUpgrade.VALUES[stack.getItemDamage()];
            tooltip.add(
                StatCollector.translateToLocalFormatted("tile.void_quarry_upgrade.desc.energy", upgrade.getCost()));
            if (!upgrade.isBoolean() && upgrade.getTierGroup()
                .equals(TieredVoidQuarryUpgrade.SPEED.getBaseName())) {
                tooltip.add(
                    StatCollector.translateToLocalFormatted("tile.void_quarry_upgrade.desc.speed", upgrade.getValue()));
            }
        }
    }
}
