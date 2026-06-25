package com.fouristhenumber.utilitiesinexcess.common.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

public class ItemGoldenBag extends Item implements IGuiHolder<PlayerInventoryGuiData> {

    private ItemStackHandler inventoryHandler;

    public ItemGoldenBag() {
        setUnlocalizedName("golden_bag");
        setTextureName("utilitiesinexcess:golden_bag");
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            GuiFactories.playerInventory()
                .openFromMainHand(player);
        }

        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings) {
        final int panelHeight = 220;
        final ModularPanel panel = ModularPanel.defaultPanel("golden_bag", 176, panelHeight);
        final ItemStack usedItem = data.getUsedItemStack();
        final EntityPlayer player = data.getPlayer();

        int inventorySize = 54;
        this.inventoryHandler = new ItemStackHandler(inventorySize) {

            @Override
            protected void onContentsChanged(int slot) {
                ItemStack usedItem = data.getUsedItemStack();
                if (usedItem != null) usedItem.setTagCompound(this.serializeNBT());
            }
        };

        if (!player.worldObj.isRemote) {
            if (usedItem.hasTagCompound()) {
                inventoryHandler.deserializeNBT(usedItem.getTagCompound());
            }
        }

        syncManager.registerSlotGroup("golden_bag_items", inventorySize);

        panel.bindPlayerInventory();

        panel.child(
            Flow.column()
                .child(
                    IKey.str(StatCollector.translateToLocal("item.golden_bag.name"))
                        .asWidget()
                        .margin(6, 0, 5, 0)
                        .left(6))
                .child(
                    buildBagSlotGroup().horizontalCenter()
                        .marginTop(3)));
        return panel;
    }

    @Override
    public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(UtilitiesInExcess.MODID, mainPanel);
    }

    private SlotGroupWidget buildBagSlotGroup() {
        return SlotGroupWidget.builder()
            .row("IIIIIIIII")
            .row("IIIIIIIII")
            .row("IIIIIIIII")
            .row("IIIIIIIII")
            .row("IIIIIIIII")
            .row("IIIIIIIII")
            .key(
                'I',
                index -> new ItemSlot().slot(
                    new ModularSlot(inventoryHandler, index).slotGroup("golden_bag_items")
                        // Don't allow placing the bag inside itself
                        .filter(stack -> !(stack.getItem() instanceof ItemGoldenBag))))
            .build();
    }

}
