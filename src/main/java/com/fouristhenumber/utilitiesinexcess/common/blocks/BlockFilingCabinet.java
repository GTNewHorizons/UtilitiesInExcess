package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.ArrayList;
import java.util.List;

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
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.CabinetTier;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.TileEntityFilingCabinet;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import com.gtnewhorizon.gtnhlib.blockstate.properties.OrientationBlockProperty;
import com.gtnewhorizon.gtnhlib.geometry.Orientation;
import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// Adapted from Filing Cabinets by phantamanta44 (MIT License)
// Source: https://github.com/phantamanta44/filing-cabinets
public class BlockFilingCabinet extends BlockContainer {

    public BlockFilingCabinet() {
        super(Material.iron);
        this.setHardness(2F);
        this.setBlockName("filing_cabinet");
    }

    public CabinetTier[] getTiers() {
        return CabinetTier.values();
    }

    protected String getTextureBasePath() {
        return "filing_cabinet";
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> items) {
        for (CabinetTier tier : getTiers()) {
            if (tier.isEnabled()) {
                items.add(new ItemStack(this, 1, tier.meta()));
            }
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityFilingCabinet cab) {
            cab.setOrientation(getOrientationFromPlacer(placer));
            cab.readFromItemStack(stack);
            cab.inventory.updateCounts();
            cab.markDirty();
        }
        super.onBlockPlacedBy(world, x, y, z, placer, stack);

    }

    private static Orientation getOrientationFromPlacer(EntityLivingBase placer) {
        int yawByte = MathHelper.floor_double((placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        ForgeDirection yaw = switch (yawByte) {
            case 1 -> ForgeDirection.EAST;
            case 2 -> ForgeDirection.SOUTH;
            case 3 -> ForgeDirection.WEST;
            default -> ForgeDirection.NORTH;
        };

        float pitch = placer.rotationPitch;
        ForgeDirection facing = pitch > 55f ? ForgeDirection.UP : pitch < -45f ? ForgeDirection.DOWN : yaw;

        Orientation o = Orientation.getOrientation(facing, yaw);
        return o != null && o != Orientation.UNKNOWN ? o : Orientation.NORTH_NORTH;
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {

        if (worldIn.isRemote) return true;

        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (!(te instanceof TileEntityFilingCabinet cabinet)) {
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

    // Keep the cabinet's contents inside the dropped item instead of spilling them onto the ground.
    // The block removal is delayed until harvestBlock so the tile entity is still alive in getDrops.
    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        if (willHarvest) {
            return true;
        }
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        super.harvestBlock(world, player, x, y, z, meta);
        world.setBlockToAir(x, y, z);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        ItemStack stack = new ItemStack(this, 1, damageDropped(metadata));
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityFilingCabinet cab && cab.hasStoredData()) {
            cab.writeToItemStack(stack);
        }
        drops.add(stack);
        return drops;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        CabinetTier[] tiers = getTiers();
        int m = (meta >= 0 && meta < tiers.length) ? meta : 0;
        return tiers[m].createTile();
    }

    @SideOnly(Side.CLIENT)
    private IIcon[][] icons;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        CabinetTier[] tiers = getTiers();
        icons = new IIcon[tiers.length][];
        for (CabinetTier tier : tiers) {
            String basePath = String.format("%s:%s/%s/", UtilitiesInExcess.MODID, getTextureBasePath(), tier.getName());
            IIcon side = reg.registerIcon(basePath + "side");
            IIcon front = reg.registerIcon(basePath + "front");
            icons[tier.meta()] = new IIcon[] { side, front };
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (icons == null || meta < 0 || meta >= icons.length || icons[meta] == null) {
            return super.getIcon(side, meta);
        }
        return side == 3 ? icons[meta][1] : icons[meta][0];
    }

    public static class ItemBlockFilingCabinet extends ItemBlock {

        private final BlockFilingCabinet cabinet;

        public ItemBlockFilingCabinet(Block block) {
            super(block);
            setHasSubtypes(true);
            this.cabinet = (BlockFilingCabinet) block;
        }

        @Override
        public int getMetadata(int damage) {
            return damage;
        }

        @Override
        public String getUnlocalizedName(final ItemStack stack) {
            CabinetTier[] tiers = cabinet.getTiers();
            int meta = stack.getItemDamage();
            String name = (meta >= 0 && meta < tiers.length) ? tiers[meta].getName() : tiers[0].getName();
            return this.getUnlocalizedName() + "." + name;
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean flags) {
            CabinetTier[] tiers = cabinet.getTiers();
            int meta = stack.getItemDamage();
            if (meta < 0 || meta >= tiers.length) return;
            String key = "tile.filing_cabinet." + tiers[meta].getName() + ".desc";
            if (StatCollector.canTranslate(key)) {
                tooltip.add(ChatFormatting.GRAY + I18n.format(key));
            }
        }
    }

    public static class CabinetOrientationProperty implements OrientationBlockProperty {

        public static final CabinetOrientationProperty instance = new CabinetOrientationProperty();

        private CabinetOrientationProperty() {}

        @Override
        public String getName() {
            return "orientation";
        }

        @Override
        public boolean hasTrait(BlockPropertyTrait trait) {
            return switch (trait) {
                case SupportsWorld, SupportsStacks -> true;
                default -> false;
            };
        }

        @Override
        public Orientation getValue(IBlockAccess world, int x, int y, int z) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (!(te instanceof TileEntityFilingCabinet cab)) return Orientation.NORTH_NORTH;
            return cab.getOrientation();
        }

        @Override
        public Orientation getValue(ItemStack stack) {
            return Orientation.NORTH_NORTH;
        }

        @Override
        public boolean isValidOrientation(Orientation value) {
            return value != null && value != Orientation.UNKNOWN;
        }
    }
}