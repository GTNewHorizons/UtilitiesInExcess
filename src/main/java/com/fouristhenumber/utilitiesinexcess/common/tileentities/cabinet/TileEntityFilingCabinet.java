package com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet;

import static com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.CabinetSetting.ScrollDirection;
import static com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.CabinetSetting.SlotDirection;
import static com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.CabinetSetting.SortType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.ItemHandlerHelper;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.compat.mui.cabinet.CabinetGui;
import com.fouristhenumber.utilitiesinexcess.config.blocks.FilingCabinetsConfig;
import com.fouristhenumber.utilitiesinexcess.utils.NbtHelper;
import com.gtnewhorizon.gtnhlib.geometry.Orientation;

public class TileEntityFilingCabinet extends TileEntity implements IGuiHolder<PosGuiData>, ISidedInventory {

    private static final Predicate<ItemStack> UPGRADE_MATCHER = is -> ModItems.CAPACITY_UPGRADE.newItemStack()
        .isItemEqual(is);

    public final ItemStackHandler upgradeInventory = new ItemStackHandler(1) {

        @Override
        public int getSlotLimit(int slot) {
            return FilingCabinetsConfig.upgradeCountMax;
        }

        @Override
        public int getStackLimit(int slot, ItemStack stack) {
            return FilingCabinetsConfig.upgradeCountMax;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack != null && UPGRADE_MATCHER.test(stack);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack current = getStackInSlot(slot);
            int have = current == null ? 0 : current.stackSize;
            int extractable = Math.max(0, have - getMinimumUpgrades());
            return super.extractItem(slot, Math.min(amount, extractable), simulate);
        }

        @Override
        public void onContentsChanged(int slot) {
            markDirty();
        }
    };

    private Orientation orientation = Orientation.NORTH_NORTH;

    private CabinetTier tier;

    public CabinetInventory inventory;

    public SortType sortType = SortType.BY_NAME;
    public ScrollDirection scrollDirection = ScrollDirection.VERTICAL;
    public SlotDirection slotDirection = SlotDirection.HORIZONTAL;

    private static final String NBT_SORT_TYPE = "SortType";
    private static final String NBT_SCROLL_DIR = "ScrollDir";
    private static final String NBT_SLOT_DIR = "SlotDir";
    private static final String NBT_CABINET_DATA = "CabinetData";

    public TileEntityFilingCabinet() {}

    public CabinetTier getTier() {
        return tier;
    }

    protected void setTier(CabinetTier tier) {
        this.tier = tier;
        this.inventory = new CabinetInventory(
            this,
            tier.config().numSlots,
            tier.config().numItems,
            FilingCabinetsConfig.upgradeCapacity);
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        if (orientation != null && orientation != Orientation.UNKNOWN) {
            this.orientation = orientation;
        }
    }

    public int getNumberOfUpgrades() {
        ItemStack stack = upgradeInventory.getStackInSlot(0);
        return stack == null ? 0 : stack.stackSize;
    }

    private int getMinimumUpgrades() {
        if (inventory == null) return 0;
        int stored = inventory.getStoredQuantity();
        int base = inventory.getBaseCapacity();
        if (stored <= base) return 0;
        return (int) Math.ceil((double) (stored - base) / FilingCabinetsConfig.upgradeCapacity);
    }

    public ItemStack getUpgradeStack(int count) {
        return ModItems.CAPACITY_UPGRADE.newItemStack(count);
    }

    public boolean isItemAllowed(ItemStack stack) {
        return tier.isItemAllowed(stack);
    }

    public Predicate<ItemStack> extractMatcher(ItemStack stack) {
        return tier.extractMatcher(stack);
    }

    public boolean installCapacityUpgrade(World world, EntityPlayer player, ItemStack stack) {
        if (stack != null && UPGRADE_MATCHER.test(stack)) {
            IChatComponent msg;
            if (getNumberOfUpgrades() < FilingCabinetsConfig.upgradeCountMax) {
                ItemStack one = ItemHandlerHelper.copyStackWithSize(stack, 1);
                ItemStack leftover = upgradeInventory.insertItem(0, one, false);
                if (leftover != null && leftover.stackSize > 0) {
                    return false; // slot rejected it (should not happen below the cap)
                }
                if (!player.capabilities.isCreativeMode) {
                    stack.stackSize -= 1;
                }
                world.playSoundAtEntity(player, "random.levelup", 1F, 1F);
                msg = new ChatComponentTranslation("tile.filing_cabinet.capacity_upgrade.success");
                msg.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN));
            } else {
                world.playSoundAtEntity(player, "random.levelup", 1F, 1F);
                msg = new ChatComponentTranslation("tile.filing_cabinet.capacity_upgrade.maxed");
                msg.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_RED));
            }
            player.addChatComponentMessage(msg);
            return true;
        }
        return false;
    }

    public boolean hasStoredData() {
        return (inventory != null && inventory.getStoredQuantity() > 0) || getNumberOfUpgrades() > 0;
    }

    public void writeToItemStack(ItemStack stack) {
        NBTTagCompound data = new NBTTagCompound();
        writeContents(data);
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound()
            .setTag(NBT_CABINET_DATA, data);
    }

    public void readFromItemStack(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()
            && stack.getTagCompound()
                .hasKey(NBT_CABINET_DATA, Constants.NBT.TAG_COMPOUND)) {
            readContents(
                stack.getTagCompound()
                    .getCompoundTag(NBT_CABINET_DATA));
        }
    }

    public static final class StoredContents {

        public final List<ItemStack> stacks;
        public final int totalItems;
        public final int upgrades;

        private StoredContents(List<ItemStack> stacks, int totalItems, int upgrades) {
            this.stacks = stacks;
            this.totalItems = totalItems;
            this.upgrades = upgrades;
        }

        public boolean isEmpty() {
            return stacks.isEmpty() && upgrades == 0;
        }
    }

    public static StoredContents readStoredContents(ItemStack stack) {
        List<ItemStack> stored = new ArrayList<>();
        int total = 0;
        int upgrades = 0;
        if (stack != null && stack.hasTagCompound()
            && stack.getTagCompound()
                .hasKey(NBT_CABINET_DATA, Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound data = stack.getTagCompound()
                .getCompoundTag(NBT_CABINET_DATA);
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

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new CabinetGui(this).build(data, syncManager, settings);
    }

    private void writeContents(NBTTagCompound compound) {
        compound.setTag("Upgrades", upgradeInventory.serializeNBT());
        compound.setTag("Inventory", this.inventory.serializeNBT());
        NbtHelper.writeEnumToNBT(compound, NBT_SORT_TYPE, sortType);
        NbtHelper.writeEnumToNBT(compound, NBT_SCROLL_DIR, scrollDirection);
        NbtHelper.writeEnumToNBT(compound, NBT_SLOT_DIR, slotDirection);
    }

    private void readContents(NBTTagCompound compound) {
        if (compound.hasKey("Upgrades", Constants.NBT.TAG_COMPOUND)) {
            upgradeInventory.deserializeNBT(compound.getCompoundTag("Upgrades"));
        }
        if (compound.hasKey("Inventory", Constants.NBT.TAG_COMPOUND)) {
            this.inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        }
        sortType = NbtHelper.readEnumFromNBT(compound, NBT_SORT_TYPE, SortType.values(), SortType.BY_NAME);
        scrollDirection = NbtHelper
            .readEnumFromNBT(compound, NBT_SCROLL_DIR, ScrollDirection.values(), ScrollDirection.VERTICAL);
        slotDirection = NbtHelper
            .readEnumFromNBT(compound, NBT_SLOT_DIR, SlotDirection.values(), SlotDirection.HORIZONTAL);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("orientation", orientation.ordinal());
        if (tier != null) {
            compound.setInteger("Tier", tier.meta());
        }
        writeContents(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        int ord = compound.getInteger("orientation");
        Orientation[] values = Orientation.values();
        orientation = (ord >= 0 && ord < values.length) ? values[ord] : Orientation.NORTH_NORTH;
        setTier(CabinetTier.from(compound.getInteger("Tier")));
        readContents(compound);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.worldObj != null && !this.worldObj.isRemote) {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.inventory.getStackInSlot(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.inventory.setStackInSlot(slot, stack);
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        return inventory.extractItem(slot, count, false);
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getRemainingCapacity();
    }

    @Override
    public String getInventoryName() {
        return "tile.filing_cabinet." + tier.getName() + ".name";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return this.inventory.isItemValid(slot, stack);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        int[] slots = new int[getSizeInventory()];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = i;
        }
        return slots;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return this.inventory.getStackInSlot(slot) != null;
    }

    public static class CabinetInventory extends ItemStackHandler {

        private final TileEntityFilingCabinet cabinet;
        private final int baseCapacity;
        private final int capacityPerUpgrade;
        private int usedSlotCount = 0;
        private int itemCount = 0;

        @Nullable
        private Predicate<ItemStack> itemMatcher = null;

        public CabinetInventory(TileEntityFilingCabinet cabinet, int numSlots, int baseCapacity,
            int capacityPerUpgrade) {
            super(numSlots);
            this.cabinet = cabinet;
            this.baseCapacity = baseCapacity;
            this.capacityPerUpgrade = capacityPerUpgrade;
        }

        @Override
        public int getStackLimit(int slot, @Nullable ItemStack stack) {
            return this.getSlotLimit(slot);
        }

        @Override
        public int getSlotLimit(int slot) {
            ItemStack stack = getStackInSlot(slot);
            return (stack != null) ? stack.stackSize + getRemainingCapacity() : getRemainingCapacity();
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            validateSlotIndex(slot);

            if (isEmpty(stack)) {
                super.setStackInSlot(slot, null);
                onContentsChanged(slot);
                return;
            }

            // Merge with any compatible slot first
            for (int i = 0; i < getSlots(); i++) {
                if (i == slot) continue;
                ItemStack existing = getStackInSlot(i);
                if (existing != null && ItemHandlerHelper.canItemStacksStack(existing, stack)) {
                    long transferable = Math.min(stack.stackSize, getSlotLimit(i));
                    transferable = Math.min(transferable, getRemainingCapacity());
                    if (transferable > 0) {
                        existing.stackSize += transferable;
                        stack.stackSize -= transferable;
                        onContentsChanged(i);
                    }
                    if (stack.stackSize <= 0) return;
                }
            }

            // Place leftover in target slot
            super.setStackInSlot(slot, stack.stackSize > 0 ? stack : null);
            onContentsChanged(slot);
        }

        @Override
        public void onContentsChanged(int slot) {
            updateCounts();
            cabinet.markDirty();
        }

        @Override
        public void onLoad() {
            updateCounts();
        }

        public void updateCounts() {
            this.itemCount = 0;
            this.usedSlotCount = 0;
            ItemStack firstStack = null;

            for (ItemStack stack : stacks) {
                if (isEmpty(stack)) {
                    continue;
                }
                usedSlotCount++;
                itemCount += stack.stackSize;
                if (firstStack == null) firstStack = stack;
            }
            itemMatcher = (firstStack != null) ? cabinet.extractMatcher(firstStack) : null;
        }

        public int getStoredQuantity() {
            return itemCount;
        }

        public int getCapacity() {
            return baseCapacity + capacityPerUpgrade * cabinet.getNumberOfUpgrades();
        }

        public int getBaseCapacity() {
            return baseCapacity;
        }

        public int getSlotsUsed() {
            return usedSlotCount;
        }

        public int getRemainingCapacity() {
            return getCapacity() - getStoredQuantity();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (isEmpty(stack)) return false;
            boolean itemMatches = itemMatcher == null || itemMatcher.test(stack);
            if (!itemMatches) return false;
            int remainingCapacity = getRemainingCapacity();
            if (remainingCapacity <= 0) return false;

            return cabinet.isItemAllowed(stack);
        }

        private static boolean isEmpty(ItemStack stack) {
            return stack == null || stack.stackSize <= 0;
        }
    }
}