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
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

public class ItemGoldenBag extends Item implements IGuiHolder<PlayerInventoryGuiData> {

    private final int inventorySize = 54;
    private ItemStackHandler inventoryHandler;
    private InvWrapper playerInventory;

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

        this.inventoryHandler = new ItemStackHandler(inventorySize) {

            @Override
            protected void onContentsChanged(int slot) {
                ItemStack usedItem = data.getUsedItemStack();
                if (usedItem != null) usedItem.setTagCompound(this.serializeNBT());
            }
        };

        this.playerInventory = new InvWrapper(player.inventory) {

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot == player.inventory.currentItem) {
                    return null;
                }

                return super.extractItem(slot, amount, simulate);
            }
        };

        if (!player.worldObj.isRemote) {
            if (usedItem.hasTagCompound()) {
                inventoryHandler.deserializeNBT(usedItem.getTagCompound());
            }
        }

        syncManager.registerSlotGroup("golden_bag_items", inventorySize);

        panel.child(
            new Column().child(
                new ParentWidget<>().widthRel(1f)
                    .height(138)
                    .child(
                        IKey.str(StatCollector.translateToLocal("item.golden_bag.name"))
                            .asWidget()
                            .margin(6, 0, 5, 0)
                            .align(Alignment.TopLeft))
                    .child(
                        buildBagSlotGroup().align(Alignment.Center)
                            .marginTop(1))
                    .child(
                        IKey.str("Inventory")
                            .asWidget()
                            .alignX(0.05f)
                            .alignY(0.99f)))
                .child(
                    buildPlayerInventorySlotGroup().align(Alignment.TopLeft)
                        .marginLeft(7)
                        .marginTop(panelHeight - 83))
                .child(
                    buildPlayerHotbarSlotGroup().align(Alignment.TopLeft)
                        .marginLeft(7)
                        .marginTop(panelHeight - 25)));

        return panel;
    }

    private SlotGroupWidget buildPlayerInventorySlotGroup() {
        return SlotGroupWidget.builder()
            .row("PPPPPPPPP")
            .row("PPPPPPPPP")
            .row("PPPPPPPPP")
            .key('P', i -> new ItemSlot().slot(new ModularSlot(playerInventory, 9 + i).slotGroup("player_inventory")))
            .build();
    }

    private SlotGroupWidget buildPlayerHotbarSlotGroup() {
        return SlotGroupWidget.builder()
            .row("HHHHHHHHH")
            .key('H', i -> new ItemSlot().slot(new ModularSlot(playerInventory, i).slotGroup("player_inventory")))
            .build();
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
