package com.fouristhenumber.utilitiesinexcess.transfer.gui;

import java.util.function.Supplier;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.UpgradeInventory;

public class NodeGui {

    public static ModularPanel buildUI(UpgradeInventory upgrades, String upgradeSlotGroupName, String title,
        StringSyncValue searchText, Supplier<Widget> bufferSlotBuilder, PanelSyncManager syncManager) {
        syncManager.syncValue("searchLocationSyncer", searchText);

        SlotGroup upgradeSlotGroup = new SlotGroup(upgradeSlotGroupName, 1);

        ModularPanel panel = new ModularPanel("panel");
        panel.bindPlayerInventory();

        panel.child(
            new ParentWidget<>().coverChildren()
                .topRelAnchor(0, 1)
                .child(
                    IKey.str(StatCollector.translateToLocal(title))
                        .asWidget()
                        .marginLeft(5)
                        .marginRight(5)
                        .marginTop(12)
                        .marginBottom(-15)));

        panel.child(
            IKey.dynamic(searchText::getStringValue)
                .asWidget()
                .marginTop(20)
                .horizontalCenter());

        Flow flow = Flow.row();
        flow.pos(34, 60)
            .size(108, 18);

        // Upgrades
        IItemHandler upgradeItemHandler = new InvWrapper(upgrades);

        for (int i = 0; i < upgrades.getSizeInventory(); i++) {
            flow.child(
                new ItemSlot().slot(
                    new ModularSlot(upgradeItemHandler, i).slotGroup(upgradeSlotGroup)
                        .changeListener(upgrades)));
        }
        panel.child(flow);

        // Buffer slot
        addBufferSlot(panel, bufferSlotBuilder);

        return panel;
    }

    private static void addBufferSlot(ModularPanel panel, Supplier<Widget> slotBuilder) {
        if (slotBuilder == null) return;

        panel.child(
            new Grid().coverChildren()
                .pos(79, 34)
                .mapTo(1, 1, index -> slotBuilder.get()));
    }
}
