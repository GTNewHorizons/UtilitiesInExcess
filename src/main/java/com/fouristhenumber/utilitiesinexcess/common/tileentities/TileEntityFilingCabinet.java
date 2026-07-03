package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.Arrays;
import java.util.Comparator;
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
import net.minecraftforge.common.util.Constants;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.ItemHandlerHelper;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockFilingCabinet.CabinetTier;
import com.fouristhenumber.utilitiesinexcess.compat.mui.cabinet.CabinetPanel;
import com.fouristhenumber.utilitiesinexcess.compat.mui.cabinet.CabinetSettingsTab;
import com.fouristhenumber.utilitiesinexcess.config.blocks.FilingCabinetsConfig;
import com.gtnewhorizon.gtnhlib.geometry.Orientation;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;

public abstract class TileEntityFilingCabinet extends TileEntity implements IGuiHolder<PosGuiData>, ISidedInventory {

    public static final class Basic extends TileEntityFilingCabinet {

        public Basic() {
            super(CabinetTier.BASIC);
        }
    }

    public static final class Advanced extends TileEntityFilingCabinet {

        public Advanced() {
            super(CabinetTier.ADVANCED);
        }
    }

    public static final class Elite extends TileEntityFilingCabinet {

        public Elite() {
            super(CabinetTier.ELITE);
        }
    }

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
            return stack != null && ModItems.CAPACITY_UPGRADE.newItemStack()
                .isItemEqual(stack);
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

    public final CabinetSettingsTab settingsTab = new CabinetSettingsTab(this);

    private static final String NBT_CABINET_DATA = "CabinetData";

    protected TileEntityFilingCabinet(CabinetTier tier) {
        setTier(tier);
    }

    private void setTier(CabinetTier tier) {
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

    public boolean isItemAllowed(ItemStack stack) {
        return tier.isItemAllowed(stack);
    }

    public Predicate<ItemStack> extractMatcher(ItemStack stack) {
        return tier.extractMatcher(stack);
    }

    public boolean hasStoredData() {
        return (inventory != null && inventory.getStoredQuantity() > 0) || getNumberOfUpgrades() > 0;
    }

    public void sortInventory() {
        inventory.sortContents(
            settingsTab.getSortType()
                .stackComparator());
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

    @Override
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(UtilitiesInExcess.MODID, mainPanel);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new CabinetPanel(this, data, syncManager, settings);
    }

    private void writeContents(NBTTagCompound compound) {
        compound.setTag("Upgrades", upgradeInventory.serializeNBT());
        compound.setTag("Inventory", this.inventory.serializeNBT());
        CabinetSettingsTab.writeToNBT(compound, settingsTab);
    }

    private void readContents(NBTTagCompound compound) {
        if (compound.hasKey("Upgrades", Constants.NBT.TAG_COMPOUND)) {
            upgradeInventory.deserializeNBT(compound.getCompoundTag("Upgrades"));
        }
        if (compound.hasKey("Inventory", Constants.NBT.TAG_COMPOUND)) {
            this.inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        }
        CabinetSettingsTab.readFromNBT(compound, settingsTab);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("orientation", orientation.ordinal());
        writeContents(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        int ord = compound.getInteger("orientation");
        Orientation[] values = Orientation.values();
        orientation = (ord >= 0 && ord < values.length) ? values[ord] : Orientation.NORTH_NORTH;
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

    // this is a phantom slot that we use to insert past 64
    private boolean isInsertSlot(int slot) {
        return slot == inventory.getSlots();
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSlots() + 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return isInsertSlot(slot) ? null : this.inventory.getStackInSlot(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (isInsertSlot(slot)) {
            this.inventory.insertBulk(stack);
        } else {
            this.inventory.setStackInSlot(slot, stack);
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        return isInsertSlot(slot) ? null : inventory.extractItem(slot, count, false);
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
        if (isInsertSlot(slot)) {
            return this.inventory.canInsert(stack);
        }
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
        return !isInsertSlot(slot) && this.inventory.getStackInSlot(slot) != null;
    }

    public static class CabinetInventory extends ItemStackHandler {

        private final int NO_SLOT = -1;

        private final TileEntityFilingCabinet cabinet;
        private final int baseCapacity;
        private final int capacityPerUpgrade;

        private static final Hash.Strategy<ItemStack> ITEM_IDENTITY = new Hash.Strategy<>() {

            @Override
            public int hashCode(ItemStack stack) {
                if (stack == null) return 0;
                int h = System.identityHashCode(stack.getItem());
                h = 31 * h + stack.getItemDamage();
                NBTTagCompound tag = stack.getTagCompound();
                return 31 * h + (tag == null ? 0 : tag.hashCode());
            }

            @Override
            public boolean equals(ItemStack a, ItemStack b) {
                if (a == b) return true;
                if (a == null || b == null) return false;
                return ItemHandlerHelper.canItemStacksStack(a, b);
            }
        };

        private final Object2IntOpenCustomHashMap<ItemStack> slotByItem = new Object2IntOpenCustomHashMap<>(
            ITEM_IDENTITY);
        private final ItemStack[] slotKey;
        private final QuantityTracker quantities;

        @Nullable
        private Predicate<ItemStack> itemMatcher = null;

        private boolean contentsChanged;

        public CabinetInventory(TileEntityFilingCabinet cabinet, int numSlots, int baseCapacity,
            int capacityPerUpgrade) {
            super(numSlots);
            this.cabinet = cabinet;
            this.baseCapacity = baseCapacity;
            this.capacityPerUpgrade = capacityPerUpgrade;
            this.slotByItem.defaultReturnValue(NO_SLOT);
            this.slotKey = new ItemStack[numSlots];
            this.quantities = new QuantityTracker(numSlots);
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
                return;
            }

            int canonical = slotByItem.getInt(stack);
            if (canonical != NO_SLOT && canonical != slot) {
                ItemStack existing = getStackInSlot(canonical);
                if (existing != null) {
                    int transferable = Math.min(stack.stackSize, getRemainingCapacity());
                    if (transferable > 0) {
                        existing.stackSize += transferable;
                        stack.stackSize -= transferable;
                        onContentsChanged(canonical);
                    }
                    if (stack.stackSize <= 0) return;
                }
            }

            super.setStackInSlot(slot, stack.stackSize > 0 ? stack : null);
        }

        @Override
        public void onContentsChanged(int slot) {
            ItemStack stack = getStackInSlot(slot);
            quantities.set(slot, isEmpty(stack) ? 0 : stack.stackSize);
            reconcileSlot(slot);
            cabinet.markDirty();
            contentsChanged = true;
        }

        public void setStackSynced(int slot, ItemStack stack) {
            super.setStackInSlot(slot, isEmpty(stack) ? null : stack);
        }

        public boolean consumeContentsChanged() {
            boolean changed = contentsChanged;
            contentsChanged = false;
            return changed;
        }

        @Override
        public void onLoad() {
            updateCounts();
        }

        public void updateCounts() {
            slotByItem.clear();
            quantities.reset();

            int n = Math.min(getSlots(), slotKey.length);
            for (int i = 0; i < n; i++) {
                ItemStack stack = getStackInSlot(i);
                ItemStack key = isEmpty(stack) ? null : stack;
                slotKey[i] = key;
                quantities.set(i, isEmpty(stack) ? 0 : stack.stackSize);
                if (key != null) slotByItem.putIfAbsent(key, i);
            }
            refreshMatcher();
        }

        public void sortContents(Comparator<ItemStack> comparator) {
            stacks.sort(comparator);
            updateCounts();
            cabinet.markDirty();
            contentsChanged = false;
        }

        private void reconcileSlot(int slot) {
            if (slot < 0 || slot >= slotKey.length) return;

            ItemStack stack = getStackInSlot(slot);
            ItemStack newKey = isEmpty(stack) ? null : stack;
            ItemStack oldKey = slotKey[slot];
            if (ITEM_IDENTITY.equals(oldKey, newKey)) return;

            if (oldKey != null) slotByItem.remove(oldKey, slot);
            if (newKey != null) slotByItem.putIfAbsent(newKey, slot);
            slotKey[slot] = newKey;
            refreshMatcher();
        }

        private void refreshMatcher() {
            if (slotByItem.isEmpty()) {
                itemMatcher = null;
                return;
            }
            int repSlot = slotByItem.values()
                .iterator()
                .nextInt();
            ItemStack rep = getStackInSlot(repSlot);
            itemMatcher = rep != null ? cabinet.extractMatcher(rep) : null;
        }

        public int getStoredQuantity() {
            return quantities.total();
        }

        public int getCapacity() {
            return baseCapacity + capacityPerUpgrade * cabinet.getNumberOfUpgrades();
        }

        public int getBaseCapacity() {
            return baseCapacity;
        }

        public int getSlotsUsed() {
            return slotByItem.size();
        }

        public int getRemainingCapacity() {
            return getCapacity() - getStoredQuantity();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (!canAccept(stack)) return false;
            ItemStack current = getStackInSlot(slot);
            return current == null || ItemHandlerHelper.canItemStacksStack(current, stack);
        }

        private boolean canAccept(ItemStack stack) {
            if (isEmpty(stack)) return false;
            if (itemMatcher != null && !itemMatcher.test(stack)) return false;
            if (getRemainingCapacity() <= 0) return false;
            return cabinet.isItemAllowed(stack);
        }

        public boolean canInsert(ItemStack stack) {
            return canAccept(stack) && (slotByItem.getInt(stack) != NO_SLOT || getSlotsUsed() < getSlots());
        }

        public ItemStack insertBulk(ItemStack stack) {
            if (!canAccept(stack)) return stack;
            int add = Math.min(stack.stackSize, getRemainingCapacity());
            if (add <= 0) return stack;

            int canonical = slotByItem.getInt(stack);
            ItemStack existing = canonical == NO_SLOT ? null : getStackInSlot(canonical);
            if (existing != null) {
                existing.stackSize += add;
                stack.stackSize -= add;
                onContentsChanged(canonical);
                return isEmpty(stack) ? null : stack;
            }
            for (int i = 0; i < getSlots(); i++) {
                if (isEmpty(getStackInSlot(i))) {
                    setStackInSlot(i, ItemHandlerHelper.copyStackWithSize(stack, add));
                    stack.stackSize -= add;
                    return isEmpty(stack) ? null : stack;
                }
            }
            return stack;
        }

        private static boolean isEmpty(ItemStack stack) {
            return stack == null || stack.stackSize <= 0;
        }

        private static final class QuantityTracker {

            private final int[] slotCount;
            private int total = 0;

            QuantityTracker(int numSlots) {
                this.slotCount = new int[numSlots];
            }

            void set(int slot, int count) {
                if (slot < 0 || slot >= slotCount.length) return;
                total += count - slotCount[slot];
                slotCount[slot] = count;
            }

            void reset() {
                total = 0;
                Arrays.fill(slotCount, 0);
            }

            int total() {
                return total;
            }
        }
    }
}
