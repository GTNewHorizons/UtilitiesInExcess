package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

public class TileEntitySignificantlyShrunkChest extends TileEntityMarginallyMaximisedChest
    implements IGuiHolder<PosGuiData> {

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {

        SlotGroup slotGroup = new SlotGroup("significantly_shrunk_chest_inv", 1);

        ModularPanel panel = new ModularPanel("panel");
        panel.bindPlayerInventory();

        // Add title
        panel.child(
            IKey.lang(getInventoryName())
                .asWidget()
                .marginLeft(5)
                .marginTop(5));

        IItemHandler itemHandler = makeHandler();
        ModularSlot slot = new ModularSlot(itemHandler, 0).slotGroup(slotGroup);

        // Add item slot
        panel.child(
            new ItemSlot().slot(slot)
                .horizontalCenter()
                .top(34));

        return panel;
    }

    protected IItemHandler makeHandler() {
        return new InvWrapper(this);
    }

    @Override
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(UtilitiesInExcess.MODID, mainPanel);
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public String getInventoryName() {
        return "tile.significantly_shrunk_chest.name";
    }
}
