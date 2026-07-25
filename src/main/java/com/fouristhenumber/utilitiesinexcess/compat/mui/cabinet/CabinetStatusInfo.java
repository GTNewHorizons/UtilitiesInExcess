package com.fouristhenumber.utilitiesinexcess.compat.mui.cabinet;

import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.formatNumber;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.DoubleValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityFilingCabinet;

public class CabinetStatusInfo {

    public static final UITexture STORAGE_PROGRESS = UITexture.builder()
        .location(UtilitiesInExcess.MODID, "gui/cabinet/progress_storage")
        .adaptable(1)
        .imageSize(32, 8)
        .build();

    public static final UITexture SLOT_PROGRESS = UITexture.builder()
        .location(UtilitiesInExcess.MODID, "gui/cabinet/progress_slot_used")
        .adaptable(1)
        .imageSize(32, 8)
        .build();

    private final TileEntityFilingCabinet tile;

    public CabinetStatusInfo(TileEntityFilingCabinet tile) {
        this.tile = tile;
    }

    public void addTo(ModularPanel panel) {
        panel.child(
            new ProgressWidget().value(
                new DoubleValue.Dynamic(() -> (double) tile.inventory.getSlotsUsed() / tile.inventory.getSlots(), null))
                .texture(SLOT_PROGRESS, 4)
                .direction(ProgressWidget.Direction.RIGHT)
                .size(32, 4)
                .leftRel(1f)
                .anchorLeft(1f)
                .marginTop(6)
                .marginRight(7)
                .tooltipAutoUpdate(true)
                .tooltipDynamic(
                    tt -> tt.add(
                        StatCollector.translateToLocalFormatted(
                            "uie.gui.tooltip.cabinet.slot",
                            formatNumber(tile.inventory.getSlotsUsed()),
                            formatNumber(tile.inventory.getSlots())))));

        panel.child(
            new ProgressWidget()
                .value(
                    new DoubleValue.Dynamic(
                        () -> (double) tile.inventory.getStoredQuantity() / tile.inventory.getCapacity(),
                        null))
                .texture(STORAGE_PROGRESS, 8)
                .direction(ProgressWidget.Direction.RIGHT)
                .size(32, 4)
                .leftRel(1f)
                .anchorLeft(1f)
                .marginTop(10)
                .marginRight(7)
                .tooltipAutoUpdate(true)
                .tooltipDynamic(
                    tt -> tt.add(
                        StatCollector.translateToLocalFormatted(
                            "uie.gui.tooltip.cabinet.storage",
                            formatNumber(tile.inventory.getStoredQuantity()),
                            formatNumber(tile.inventory.getCapacity())))));

        panel.child(
            new ParentWidget<>().coverChildren()
                .leftRel(0f)
                .anchorLeft(0f)
                .marginTop(6)
                .marginLeft(7)
                .child(
                    IKey.str(StatCollector.translateToLocal(tile.getInventoryName()))
                        .asWidget()));
    }
}
