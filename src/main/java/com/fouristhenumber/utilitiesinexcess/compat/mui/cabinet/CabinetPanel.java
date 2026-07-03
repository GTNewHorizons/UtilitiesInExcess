package com.fouristhenumber.utilitiesinexcess.compat.mui.cabinet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityFilingCabinet;

public class CabinetPanel extends ModularPanel {

    private static final int WIDTH = 176;
    private static final int HEIGHT = 226;

    private static final int GRID_LEFT = 7;
    private static final int GRID_TOP = 18;

    private static final int INVENTORY_LABEL_LEFT = 7;
    private static final int INVENTORY_LABEL_TOP = HEIGHT - 95;

    private TileEntityFilingCabinet tile;
    private PosGuiData data;
    private PanelSyncManager syncManager;
    private CabinetStatusInfo status;
    private CabinetSettingsTab settingsTab;
    private CabinetUpgradeTab upgradeTab;
    private CabinetInventoryGrid grid;

    public CabinetPanel(TileEntityFilingCabinet tile, PosGuiData data, PanelSyncManager syncManager,
        UISettings settings) {
        super("filing_cabinet");
        this.tile = tile;
        this.data = data;
        this.syncManager = syncManager;
        settings.customContainer(() -> new CabinetContainer(tile));
        this.settingsTab = tile.settingsTab;
        this.upgradeTab = new CabinetUpgradeTab(tile, syncManager);
        this.status = new CabinetStatusInfo(tile);
        this.grid = new CabinetInventoryGrid(tile, syncManager);
        buildShell();
    }

    private void buildShell() {
        size(WIDTH, HEIGHT);
        bindPlayerInventory();

        status.addTo(this);
        settingsTab.addTo(this);
        upgradeTab.addTo(this);

        child(grid.pos(GRID_LEFT, GRID_TOP));
        settingsTab.onChanged(grid::onSettingsChanged);

        child(
            IKey.str(StatCollector.translateToLocal("container.inventory"))
                .asWidget()
                .pos(INVENTORY_LABEL_LEFT, INVENTORY_LABEL_TOP));
    }

    public static class CabinetContainer extends ModularContainer {

        private static final int LEFT_MOUSE = 0;
        private static final int RIGHT_MOUSE = 1;
        private static final int PICKUP = 0;
        private static final int QUICK_MOVE = 1;

        private final TileEntityFilingCabinet tile;

        public CabinetContainer(TileEntityFilingCabinet tile) {
            this.tile = tile;
        }

        @Override
        public void onModularContainerOpened() {
            tile.sortInventory();
        }

        @Override
        public void onUpdate() {
            super.onUpdate();
            if (!isClient() && tile.inventory.consumeContentsChanged()) {
                tile.sortInventory();
                detectAndSendChanges();
            }
        }

        @Override
        public ItemStack slotClick(int slotId, int mouseButton, int mode, EntityPlayer player) {
            if (mode == PICKUP && (mouseButton == LEFT_MOUSE || mouseButton == RIGHT_MOUSE)
                && slotId >= 0
                && slotId < this.inventorySlots.size()
                && player.inventory.getItemStack() != null
                && isCabinetSlot(getSlot(slotId))) {
                insertHeldStack(player, mouseButton == RIGHT_MOUSE);
                tile.sortInventory();
                detectAndSendChanges();
                return null;
            }
            if ((mode == PICKUP || mode == QUICK_MOVE) && (mouseButton == LEFT_MOUSE || mouseButton == RIGHT_MOUSE)
                && slotId >= 0
                && slotId < this.inventorySlots.size()
                && isUpgradeSlot(getSlot(slotId))
                && getSlot(slotId).getStack() != null
                && (mode == QUICK_MOVE || player.inventory.getItemStack() == null)) {
                takeUpgrades(player, getSlot(slotId).getStack(), mouseButton, mode == QUICK_MOVE);
                detectAndSendChanges();
                return null;
            }
            ItemStack result = super.slotClick(slotId, mouseButton, mode, player);
            tile.sortInventory();
            detectAndSendChanges();
            return result;
        }

        /**
         * sometime the client and server desync leading to doubling of the items when sorting while
         * we insert items , this aims to fix it by using the super.setStackInSlot that is present in
         * ItemStackHandler instead of our custom implementation
         */
        @Override
        public void putStackInSlot(int slotId, ItemStack stack) {
            Slot slot = getSlot(slotId);
            if (isCabinetSlot(slot)) {
                tile.inventory.setStackSynced(slot.getSlotIndex(), stack);
            } else {
                super.putStackInSlot(slotId, stack);
            }
        }

        @Override
        public void putStacksInSlots(ItemStack[] items) {
            // the inherited implementation bypasses putStackInSlot, so route every slot through it
            for (int i = 0; i < items.length && i < this.inventorySlots.size(); i++) {
                putStackInSlot(i, items[i]);
            }
        }

        private boolean isCabinetSlot(Slot slot) {
            return slot instanceof ModularSlot modularSlot && modularSlot.getItemHandler() == tile.inventory;
        }

        private boolean isUpgradeSlot(Slot slot) {
            return slot instanceof ModularSlot modularSlot && modularSlot.getItemHandler() == tile.upgradeInventory;
        }

        private void takeUpgrades(EntityPlayer player, ItemStack slotStack, int mouseButton, boolean toInventory) {
            int visible = Math.min(slotStack.stackSize, slotStack.getMaxStackSize());
            int request = (!toInventory && mouseButton == RIGHT_MOUSE) ? (visible + 1) / 2 : visible;
            ItemStack taken = tile.upgradeInventory.extractItem(0, request, false);
            if (taken == null) return;
            if (toInventory) {
                if (!player.inventory.addItemStackToInventory(taken) && taken.stackSize > 0) {
                    // player inventory is full, return what did not fit
                    tile.upgradeInventory.insertItem(0, taken, false);
                }
            } else {
                player.inventory.setItemStack(taken);
            }
        }

        private void insertHeldStack(EntityPlayer player, boolean single) {
            ItemStack held = player.inventory.getItemStack();
            if (single) {
                ItemStack one = held.splitStack(1);
                ItemStack rest = tile.inventory.insertBulk(one);
                if (rest != null) held.stackSize += rest.stackSize; // did not fit, give it back
            } else {
                player.inventory.setItemStack(tile.inventory.insertBulk(held));
            }
            ItemStack left = player.inventory.getItemStack();
            if (left != null && left.stackSize <= 0) {
                player.inventory.setItemStack(null);
            }
        }
    }
}
