package com.fouristhenumber.utilitiesinexcess.common.items;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.SyncHandlers;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemGoldenBag extends Item implements IGuiHolder<GuiData> {

    private final IItemHandlerModifiable inventoryHandler = new ItemStackHandler(54);

    public ItemGoldenBag() {
        setUnlocalizedName("golden_bag");
        setTextureName("utilitiesinexcess:golden_bag");
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            GuiFactories.playerInventory().openFromMainHand(player);
        }

        return super.onItemRightClick(stack, world, player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(StatCollector.translateToLocal("item.golden_bag.desc"));
    }

    @Override
    public ModularPanel buildUI(GuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = ModularPanel.defaultPanel("golden_bag", 176, 220);

        syncManager.registerSlotGroup("golden_bag_items", 9);

        panel.child(new Column()
            .child(new ParentWidget<>().widthRel(1f).height(138)
                .child(IKey.str("Golden Bag of Holding").asWidget().margin(6, 0, 5, 0).align(Alignment.TopLeft))
                .child(SlotGroupWidget.builder()
                    .row("IIIIIIIII")
                    .row("IIIIIIIII")
                    .row("IIIIIIIII")
                    .row("IIIIIIIII")
                    .row("IIIIIIIII")
                    .row("IIIIIIIII")
                    .key('I', index -> new ItemSlot().slot(SyncHandlers.itemSlot(inventoryHandler, index)
                        .slotGroup("golden_bag_items")))
                    .build()
                    .align(Alignment.Center).marginTop(1))
                .child(IKey.str("Inventory").asWidget().alignX(0.05f).alignY(0.99f)))
                .child(SlotGroupWidget.playerInventory(true))
            );

        return panel;
    }

}
