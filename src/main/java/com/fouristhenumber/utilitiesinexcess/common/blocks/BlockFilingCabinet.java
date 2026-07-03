package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityFilingCabinet;
import com.fouristhenumber.utilitiesinexcess.config.blocks.FilingCabinetsConfig;
import com.fouristhenumber.utilitiesinexcess.config.blocks.FilingCabinetsConfig.CabinetConfig;
import com.fouristhenumber.utilitiesinexcess.render.ItemGridTooltip;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import com.gtnewhorizon.gtnhlib.blockstate.properties.OrientationBlockProperty;
import com.gtnewhorizon.gtnhlib.geometry.Orientation;
import com.mojang.realmsclient.gui.ChatFormatting;

// Adapted from Filing Cabinets by phantamanta44 (MIT License)
// Source: https://github.com/phantamanta44/filing-cabinets
public class BlockFilingCabinet extends BlockContainer {

    public enum CabinetTier {

        BASIC(FilingCabinetsConfig.cabinetBasic, stack -> stack.getMaxStackSize() != 1, true,
            TileEntityFilingCabinet.Basic::new),
        ADVANCED(FilingCabinetsConfig.cabinetAdvanced, stack -> stack.getMaxStackSize() == 1, false,
            TileEntityFilingCabinet.Advanced::new),
        ELITE(FilingCabinetsConfig.cabinetElite, stack -> true, false, TileEntityFilingCabinet.Elite::new);

        public static final CabinetTier[] VALUES = values();

        private final CabinetConfig config;
        private final Predicate<ItemStack> accepts;
        private final boolean lockToFirstItem;
        private final Supplier<TileEntityFilingCabinet> factory;

        CabinetTier(CabinetConfig config, Predicate<ItemStack> accepts, boolean lockToFirstItem,
            Supplier<TileEntityFilingCabinet> factory) {
            this.config = config;
            this.accepts = accepts;
            this.lockToFirstItem = lockToFirstItem;
            this.factory = factory;
        }

        public static CabinetTier from(int meta) {
            return (meta >= 0 && meta < VALUES.length) ? VALUES[meta] : BASIC;
        }

        public CabinetConfig config() {
            return config;
        }

        public boolean isItemAllowed(ItemStack stack) {
            return accepts.test(stack);
        }

        public Predicate<ItemStack> extractMatcher(ItemStack first) {
            return lockToFirstItem ? is -> Objects.equals(first.getItem(), is.getItem()) : is -> true;
        }

        public int meta() {
            return ordinal();
        }

        public String getName() {
            return name().toLowerCase();
        }

        public boolean isEnabled() {
            return config.enable;
        }

        public TileEntity createTile() {
            return factory.get();
        }
    }

    public BlockFilingCabinet() {
        super(Material.iron);
        this.setHardness(2F);
        this.setBlockName("filing_cabinet");
    }

    public CabinetTier[] getTiers() {
        return CabinetTier.VALUES;
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
        if (!(te instanceof TileEntityFilingCabinet)) {
            return false;
        }

        GuiFactories.tileEntity()
            .open(player, x, y, z);
        return true;
    }

    // delay the tile removal so we can read the content and drop it using getDrops
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

    public static class ItemBlockFilingCabinet extends ItemBlock implements ItemGridTooltip.Provider {

        private final BlockFilingCabinet cabinet;

        public ItemBlockFilingCabinet(Block block) {
            super(block);
            setHasSubtypes(true);
            this.cabinet = (BlockFilingCabinet) block;
        }

        @Override
        public @NotNull List<ItemStack> getGridContents(ItemStack stack) {
            return readStoredContents(stack).stacks();
        }

        @Override
        public List<String> getGridHeader(ItemStack stack, List<String> vanillaLines) {
            if (readStoredContents(stack).stacks()
                .isEmpty()) {
                return vanillaLines;
            }
            List<String> header = new ArrayList<>(vanillaLines);
            header.add(ChatFormatting.GRAY + StatCollector.translateToLocal("tile.filing_cabinet.tooltip.contents"));
            return header;
        }

        @Override
        public @NotNull List<String> getGridFooter(ItemStack stack) {
            StoredContents contents = readStoredContents(stack);
            List<String> footer = new ArrayList<>();
            if (!contents.stacks()
                .isEmpty()) {
                footer.add(
                    ChatFormatting.GRAY + StatCollector
                        .translateToLocalFormatted("tile.filing_cabinet.tooltip.total", contents.totalItems()));
            }
            if (contents.upgrades() > 0) {
                footer.add(
                    ChatFormatting.GRAY + StatCollector
                        .translateToLocalFormatted("tile.filing_cabinet.tooltip.upgrades", contents.upgrades()));
            }
            return footer;
        }

        private static StoredContents readStoredContents(ItemStack stack) {
            List<ItemStack> stored = new ArrayList<>();
            int total = 0;
            int upgrades = 0;
            if (stack != null && stack.hasTagCompound()
                && stack.getTagCompound()
                    .hasKey("CabinetData", Constants.NBT.TAG_COMPOUND)) {
                NBTTagCompound data = stack.getTagCompound()
                    .getCompoundTag("CabinetData");
                if (data.hasKey("Inventory", Constants.NBT.TAG_COMPOUND)) {
                    ItemStackHandler handler = new ItemStackHandler();
                    handler.deserializeNBT(data.getCompoundTag("Inventory"));
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack slot = handler.getStackInSlot(i);
                        if (slot != null && slot.stackSize > 0) {
                            stored.add(slot);
                            total += slot.stackSize;
                        }
                    }
                }
                if (data.hasKey("Upgrades", Constants.NBT.TAG_COMPOUND)) {
                    ItemStackHandler handler = new ItemStackHandler();
                    handler.deserializeNBT(data.getCompoundTag("Upgrades"));
                    ItemStack up = handler.getStackInSlot(0);
                    upgrades = (up == null) ? 0 : up.stackSize;
                }
            }
            return new StoredContents(stored, total, upgrades);
        }

        private record StoredContents(List<ItemStack> stacks, int totalItems, int upgrades) {}

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
