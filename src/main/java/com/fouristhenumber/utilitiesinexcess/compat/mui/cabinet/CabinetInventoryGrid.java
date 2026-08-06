package com.fouristhenumber.utilitiesinexcess.compat.mui.cabinet;

import java.util.List;

import com.cleanroommc.modularui.api.layout.ILayoutWidget;
import com.cleanroommc.modularui.api.layout.IViewport;
import com.cleanroommc.modularui.api.layout.IViewportStack;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.HoveredWidgetList;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.AbstractScrollWidget;
import com.cleanroommc.modularui.widget.scroll.HorizontalScrollData;
import com.cleanroommc.modularui.widget.scroll.ScrollArea;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityFilingCabinet;

public class CabinetInventoryGrid extends AbstractScrollWidget<IWidget, CabinetInventoryGrid> implements ILayoutWidget {

    private static final int SLOT_SIZE = 18;
    private static final int COLS = 9;
    private static final int ROWS = 6;
    private static final int SCROLLBAR_THICKNESS = 4;

    private final TileEntityFilingCabinet tile;

    public CabinetInventoryGrid(TileEntityFilingCabinet tile, PanelSyncManager syncManager) {
        super(null, null);
        this.tile = tile;
        name("cabinet_inventory_grid");

        SlotGroup group = new SlotGroup("cabinet_inventory", COLS, true);
        syncManager.registerSlotGroup(group);
        for (int i = 0; i < tile.inventory.getSlots(); i++) {
            ModularSlot slot = new ModularSlot(tile.inventory, i).slotGroup(group)
                .ignoreMaxStackSize(true)
                .canDragInto(false);
            addChild(new ItemSlot() {

                @Override
                protected void drawSlotAmountText(int amount, String format) {
                    if (amount <= 99) {
                        super.drawSlotAmountText(amount, format);
                        return;
                    }
                    // for some reason only this method that accept long does the scale according to the amount so we
                    // cast are int to long
                    long l = amount;
                    GuiDraw.drawAmountText(
                        l,
                        format,
                        0,
                        0,
                        getArea().width - 2,
                        getArea().height - 1,
                        Alignment.BottomRight);
                }

            }.slot(slot), getChildren().size());
        }
        applyScrollDirection();
    }

    // for some reason the scrollbar area was testing using absolute mouse coords so to fix it
    // we change it to use local coords.
    @Override
    public void getWidgetsAt(IViewportStack stack, HoveredWidgetList widgets, int x, int y) {
        if (widgets.peek() == this && !isMouseOverScrollbar(stack, x, y)) {
            if (hasChildren()) {
                IViewport.getChildrenAt(this, stack, widgets, x, y);
            }
        }
    }

    private boolean isMouseOverScrollbar(IViewportStack stack, int x, int y) {
        int localX = stack.unTransformX(x, y) - getScrollX();
        int localY = stack.unTransformY(x, y) - getScrollY();
        ScrollArea scroll = getScrollArea();
        return (scroll.getScrollX() != null && scroll.getScrollX()
            .isInsideScrollbarArea(scroll, localX, localY)) || (scroll.getScrollY() != null
                && scroll.getScrollY()
                    .isInsideScrollbarArea(scroll, localX, localY));
    }

    public void onSettingsChanged() {
        applyScrollDirection();
        if (isValid()) scheduleResize();
    }

    private void applyScrollDirection() {
        boolean horizontal = tile.settingsTab.getScrollDirection()
            .isHorizontal();
        getScrollArea().setScrollDataX(horizontal ? new HorizontalScrollData(false, SCROLLBAR_THICKNESS) : null);
        getScrollArea().setScrollDataY(horizontal ? null : new VerticalScrollData(false, SCROLLBAR_THICKNESS));
        width(COLS * SLOT_SIZE + (horizontal ? 0 : SCROLLBAR_THICKNESS));
        height(ROWS * SLOT_SIZE + (horizontal ? SCROLLBAR_THICKNESS : 0));
    }

    @Override
    public boolean layoutWidgets() {
        List<IWidget> children = getChildren();
        int n = children.size();
        if (n == 0) return true;
        for (IWidget child : children) {
            if (!child.resizer()
                .isWidthCalculated()
                || !child.resizer()
                    .isHeightCalculated())
                return false;
        }

        boolean horizontalScroll = tile.settingsTab.getScrollDirection()
            .isHorizontal();
        boolean horizontalFill = tile.settingsTab.getSlotDirection()
            .isHorizontal();
        int lanes = horizontalScroll ? ROWS : COLS;
        int span = (n + lanes - 1) / lanes;

        for (int d = 0; d < n; d++) {
            int row, col;
            if (horizontalScroll) {
                if (horizontalFill) {
                    col = d % span;
                    row = d / span;
                } else {
                    row = d % ROWS;
                    col = d / ROWS;
                }
            } else {
                if (horizontalFill) {
                    col = d % COLS;
                    row = d / COLS;
                } else {
                    row = d % span;
                    col = d / span;
                }
            }
            IWidget child = children.get(d);
            child.getArea().rx = col * SLOT_SIZE;
            child.getArea().ry = row * SLOT_SIZE;
            child.resizer()
                .setPosResized(true, true);
        }

        if (getScrollArea().getScrollX() != null) {
            getScrollArea().getScrollX()
                .setScrollSize(span * SLOT_SIZE);
        }
        if (getScrollArea().getScrollY() != null) {
            getScrollArea().getScrollY()
                .setScrollSize(span * SLOT_SIZE);
        }
        return true;
    }
}
