package com.fouristhenumber.utilitiesinexcess.compat.mui.cabinet;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Interpolation;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.Expandable;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityFilingCabinet;
import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;

public class CabinetUpgradeTab {

    private static final int TOP = 26;

    private static final int TAB_SIZE = CabinetSettingsTab.TAB_SIZE;
    private static final int GAP = CabinetSettingsTab.GAP;
    private static final int SLOT_SIZE = 18;
    private static final int SLOT_TOP = (TAB_SIZE - SLOT_SIZE) / 2;

    private final TileEntityFilingCabinet tile;
    private final PanelSyncManager syncManager;

    public CabinetUpgradeTab(TileEntityFilingCabinet tile, PanelSyncManager syncManager) {
        this.tile = tile;
        this.syncManager = syncManager;
    }

    public void addTo(ModularPanel panel) {
        SlotGroup upgradeGroup = new SlotGroup("cabinet_upgrades", 1, 50, true);
        syncManager.registerSlotGroup(upgradeGroup);

        ItemSlot upgradeSlot = new ItemSlot().slot(new ModularSlot(tile.upgradeInventory, 0).slotGroup(upgradeGroup))
            .tooltipDynamic(
                tt -> tt.addLine(
                    StatCollector.translateToLocalFormatted(
                        "uie.gui.tooltip.cabinet.upgrades",
                        tile.getNumberOfUpgrades(),
                        BlockConfig.filingCabinets.upgradeCountMax)));

        int expandedWidth = TAB_SIZE + GAP + SLOT_SIZE + 5;

        panel.child(
            new Expandable().name("upgrades expandable")
                .top(TOP)
                .leftRelOffset(1f, 1)
                .background(GuiTextures.MC_BACKGROUND)
                .excludeAreaInRecipeViewer()
                .stencilTransform((r, expanded) -> {
                    r.width = Math.max(TAB_SIZE, r.width - 5);
                    r.height = Math.max(TAB_SIZE, r.height - 5);
                })
                .animationDuration(300)
                .interpolation(Interpolation.SINE_OUT)
                .collapsedView(
                    GuiTextures.ADD.asIcon()
                        .asWidget()
                        .size(TAB_SIZE)
                        .pos(0, 0))
                .expandedView(
                    new ParentWidget<>().name("upgrades tab")
                        .child(
                            GuiTextures.ADD.asIcon()
                                .asWidget()
                                .size(TAB_SIZE)
                                .pos(0, 0))
                        .child(upgradeSlot.pos(TAB_SIZE + GAP, SLOT_TOP))
                        .size(expandedWidth, TAB_SIZE)));
    }
}
