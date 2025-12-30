package com.fouristhenumber.utilitiesinexcess.common.blocks;

import static com.fouristhenumber.utilitiesinexcess.render.FacingRotation.calculateFacingRotation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.TileFilingCabinetAdvanced;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.TileFilingCabinetBasic;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.TileFilingCabinetElite;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.base.TileFilingCabinetBase;
import com.fouristhenumber.utilitiesinexcess.config.blocks.FilingCabinetsConfig;
import com.fouristhenumber.utilitiesinexcess.render.FacingRotation;
import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// Adapted from Filing Cabinets by phantamanta44 (MIT License)
// Source: https://github.com/phantamanta44/filing-cabinets

public class BlockFilingCabinet extends BlockContainer {

    public enum Type {

        BASIC(TileFilingCabinetBasic::new, FilingCabinetsConfig.cabinetBasic.enable),
        ADVANCED(TileFilingCabinetAdvanced::new, FilingCabinetsConfig.cabinetAdvanced.enable),
        ELITE(TileFilingCabinetElite::new, FilingCabinetsConfig.cabinetElite.enable);

        private static final Type[] VALUES = values();

        public static Type getForItemMeta(int meta) {
            return VALUES[meta];
        }

        private final Supplier<TileEntity> tileFactory;
        private final boolean isEnabled;

        Type(Supplier<TileEntity> tileFactory, boolean isEnabled) {
            this.tileFactory = tileFactory;
            this.isEnabled = isEnabled;
        }

        TileEntity createTileEntity() {
            return tileFactory.get();
        }

        public String getName() {
            return name().toLowerCase();
        }

        public int getItemMeta() {
            return ordinal();
        }

    }

    public BlockFilingCabinet() {
        super(Material.iron);
        this.setHardness(2F);
        this.setBlockName("filing_cabinet");
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> items) {
        for (Type type : Type.VALUES) {
            if (type.isEnabled) {
                items.add(new ItemStack(this, 1, type.getItemMeta()));
            }
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public int getRenderType() {
        return UtilitiesInExcess.rotatableblockRenderID;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        FacingRotation fr = calculateFacingRotation(placer);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileFilingCabinetBase cabinet) {
            cabinet.setFacing(fr.facing);
            cabinet.setYaw(fr.yaw);
        }
        super.onBlockPlacedBy(world, x, y, z, placer, stack);

    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {

        if (worldIn.isRemote) return true;

        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (!(te instanceof TileFilingCabinetBase cabinet)) {
            return false;
        }

        ItemStack held = player.getCurrentEquippedItem();

        if (held != null && cabinet.installCapacityUpgrade(worldIn, player, held)) {
            return true;
        }

        GuiFactories.tileEntity()
            .open(player, x, y, z);
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        dropInventory(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }

    public static void dropInventory(World world, int x, int y, int z) {
        if (world.isRemote) return;

        TileEntity te = world.getTileEntity(x, y, z);
        if (te == null) return;

        if (te instanceof TileFilingCabinetBase inv) {
            inv.dropInventory(world, x, y, z);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return Type.getForItemMeta(meta)
            .createTileEntity();
    }


    @SideOnly(Side.CLIENT)
    private Map<Type, IIcon[]> icons;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        icons = new HashMap<>();
        for (Type type : Type.VALUES) {
            String basePath = String.format("%s:filing_cabinet/%s/", UtilitiesInExcess.MODID, type.getName());
            IIcon side = reg.registerIcon(basePath + "side");
            IIcon front = reg.registerIcon(basePath + "front");
            icons.put(type, new IIcon[] { side, front });
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return side == 3 ? getFrontIcon(meta) : getSideIcon(meta);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getSideIcon(int meta) {
        Type type = Type.getForItemMeta(meta);
        return icons.get(type)[0]; // side
    }

    @SideOnly(Side.CLIENT)
    public IIcon getFrontIcon(int meta) {
        Type type = Type.getForItemMeta(meta);
        return icons.get(type)[1];
    }

    public static class ItemBlockFilingCabinet extends ItemBlock {

        public ItemBlockFilingCabinet(Block block) {
            super(block);
            setHasSubtypes(true);
        }

        @Override
        public int getMetadata(int damage) {
            return damage;
        }

        @Override
        public String getUnlocalizedName(final ItemStack stack) {
            return this.getUnlocalizedName() + "."
                + Type.getForItemMeta(stack.getItemDamage())
                    .getName();
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean flags) {
            switch (Type.getForItemMeta(stack.getItemDamage())) {
                case BASIC:
                    tooltip.add(ChatFormatting.GRAY + I18n.format("tile.filing_cabinet.basic.desc"));
                    break;
                case ADVANCED:
                    tooltip.add(ChatFormatting.GRAY + I18n.format("tile.filing_cabinet.advanced.desc"));
                    break;
            }
        }
    }
}
